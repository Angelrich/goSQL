package gosql.service;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Injector;
import com.torodb.core.Shutdowner;
import com.torodb.core.backend.BackendBundle;
import com.torodb.core.backend.BackendService;
import com.torodb.core.backend.DdlOperationExecutor;
import com.torodb.core.bundle.Bundle;
import com.torodb.core.bundle.BundleConfig;
import com.torodb.core.bundle.BundleConfigImpl;
import com.torodb.core.exceptions.user.UserException;
import com.torodb.core.logging.ComponentLoggerFactory;
import com.torodb.core.retrier.Retrier;
import com.torodb.core.retrier.RetrierGiveUpException;
import com.torodb.core.supervision.Supervisor;
import com.torodb.core.supervision.SupervisorDecision;
import com.torodb.mongodb.repl.ConsistencyHandler;
import com.torodb.mongodb.repl.sharding.MongoDbShardingBundle;
import com.torodb.mongodb.repl.sharding.MongoDbShardingConfig;
import com.torodb.mongodb.repl.sharding.MongoDbShardingConfigBuilder;
import com.torodb.torod.TorodBundle;
import com.torodb.torod.impl.sql.SqlTorodBundle;
import com.torodb.torod.impl.sql.SqlTorodConfig;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

public class GoSQLService extends AbstractIdleService implements Supervisor {

  private final Logger logger;
  private final ThreadFactory threadFactory;
  private final GoSQLConfig stampedeConfig;
  private final Injector essentialInjector;
  private final BundleConfig generalBundleConfig;
  private final Shutdowner shutdowner;

  public GoSQLService(GoSQLConfig stampedeConfig) {
    this.logger = stampedeConfig.getLifecycleLoggerFactory().apply(this.getClass());
    this.stampedeConfig = stampedeConfig;

    this.essentialInjector = stampedeConfig.getEssentialInjector();
    this.threadFactory = essentialInjector.getInstance(ThreadFactory.class);
    this.generalBundleConfig = new BundleConfigImpl(essentialInjector, this);
    this.shutdowner = essentialInjector.getInstance(Shutdowner.class);
  }

  @Override
  protected Executor executor() {
    return (Runnable command) -> {
      Thread thread = threadFactory.newThread(command);
      thread.start();
    };
  }

  @Override
  public SupervisorDecision onError(Object supervised, Throwable error) {
    logger.error("Error reported by " + supervised + ". Stopping", error);
    this.stopAsync();
    return SupervisorDecision.IGNORE;
  }

  @Override
  protected void startUp() throws Exception {
    logger.info("Starting up");

    shutdowner.startAsync();
    shutdowner.awaitRunning();
    
    BackendBundle backendBundle = stampedeConfig.getBackendBundleGenerator()
        .apply(generalBundleConfig);
    startBundle(backendBundle);

    Map<String, ConsistencyHandler> consistencyHandlers = createConsistencyHandlers(
        backendBundle,
        stampedeConfig.getThreadFactory()
    );

    resolveInconsistencies(backendBundle, consistencyHandlers);

    TorodBundle torodBundle = createBundle(backendBundle);
    startBundle(torodBundle);

    MongoDbShardingBundle shardingBundle = createShardingBundle(torodBundle, consistencyHandlers);
    startBundle(shardingBundle);

    logger.info("Running...");
  }

  @Override
  protected void shutDown() throws Exception {
    logger.info("Shutting down");
    if (shutdowner != null) {
      shutdowner.stopAsync();
      shutdowner.awaitTerminated();
    }
    logger.info("Shutted down");
  }

  private Map<String, ConsistencyHandler> createConsistencyHandlers(BackendBundle backendBundle,
      ThreadFactory threadFactory) {
    Retrier retrier = essentialInjector.getInstance(Retrier.class);
    BackendService backendService = backendBundle.getExternalInterface().getBackendService();

    Function<String, ConsistencyHandler> chSupplier;
    if (stampedeConfig.getShardConfigBuilders().size() == 1) {
      chSupplier = (shardId) -> new DefaultConsistencyHandler(
          backendService, 
          retrier, 
          threadFactory
      );
    } else {
      chSupplier = (shardId) -> new ShardConsistencyHandler(
          shardId, backendService, retrier, threadFactory
      );
    }

    Map<String, ConsistencyHandler> result = new HashMap<>();

    stampedeConfig.getShardConfigBuilders().stream()
        .map(GoSQLConfig.ShardConfigBuilder::getShardId)
        .forEachOrdered((shardId) -> {
          ConsistencyHandler consistencyHandler = chSupplier.apply(shardId);

          consistencyHandler.startAsync();
          consistencyHandler.awaitRunning();

          result.put(shardId, consistencyHandler);
        });

    return result;
  }

  private TorodBundle createBundle(BackendBundle backendBundle) {
    return new SqlTorodBundle(new SqlTorodConfig(
        backendBundle,
        essentialInjector,
        this
    ));
  }

  private MongoDbShardingBundle createShardingBundle(TorodBundle torodBundle,
      Map<String, ConsistencyHandler> consistencyHandler) {

    MongoDbShardingConfigBuilder configBuilder;
    if (stampedeConfig.isUnsharded()) {
      configBuilder = MongoDbShardingConfigBuilder.createUnshardedBuilder(generalBundleConfig);
    } else {
      configBuilder = MongoDbShardingConfigBuilder.createShardedBuilder(generalBundleConfig);
    }

    configBuilder.setTorodBundle(torodBundle)
        .setUserReplFilter(stampedeConfig.getUserReplicationFilters())
        .setLifecycleLoggerFactory(stampedeConfig.getLifecycleLoggerFactory())
        .setOffHeapBufferConfig(stampedeConfig.getOffHeapBufferConfig());

    stampedeConfig.getShardConfigBuilders().forEach(shardConfBuilder ->
        addShard(configBuilder, shardConfBuilder, consistencyHandler)
    );

    return new MongoDbShardingBundle(configBuilder.build());
  }

  private void addShard(
      MongoDbShardingConfigBuilder shardingConfBuilder,
      GoSQLConfig.ShardConfigBuilder shardConfBuilder,
      Map<String, ConsistencyHandler> consistencyHandlers) {

    ConsistencyHandler consistencyHandler = consistencyHandlers.get(shardConfBuilder.getShardId());

    MongoDbShardingConfig.ShardConfig shardConfig = shardConfBuilder.createConfig(
        consistencyHandler);

    shardingConfBuilder.addShard(shardConfig);
  }

  private void dropUserData(BackendBundle backendBundle) throws UserException {
    BackendService backendService = backendBundle.getExternalInterface().getBackendService();
    try (DdlOperationExecutor ddlEx = backendService.openDdlOperationExecutor()) {
      ddlEx.dropUserData();
    }
  }

  private void startBundle(Bundle<?> bundle) {
    bundle.startAsync();
    bundle.awaitRunning();

    shutdowner.addStopShutdownListener(bundle);
  }

  private void resolveInconsistencies(BackendBundle backendBundle, 
      Map<String, ConsistencyHandler> consistencyHandlers) throws UserException,
      RetrierGiveUpException {

    Optional<String> inconsistentShard = consistencyHandlers.entrySet().stream()
        .filter(e -> !e.getValue().isConsistent())
        .map(Map.Entry::getKey)
        .findAny();

    if (inconsistentShard.isPresent()) {
      logger.warn("Found that replication shard {} is not consistent.",
          inconsistentShard.orElse("unknown")
      );
      logger.warn("Dropping user data.");
      dropUserData(backendBundle);

      for (Map.Entry<String, ConsistencyHandler> entry : consistencyHandlers.entrySet()) {
        String shardId = entry.getKey();
        ConsistencyHandler consistencyHandler = entry.getValue();
        Logger logger = new ComponentLoggerFactory("REPL-" + shardId)
            .apply(this.getClass());
        consistencyHandler.setConsistent(logger, false);
      }
    } else {
      logger.info("All replication shards are consistent");
    }
  }
}
