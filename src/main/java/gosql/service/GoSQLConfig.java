package gosql.service;

import com.google.inject.Injector;
import com.torodb.core.backend.BackendBundle;
import com.torodb.core.bundle.BundleConfig;
import com.torodb.core.logging.LoggerFactory;
import com.torodb.mongodb.repl.ConsistencyHandler;
import com.torodb.mongodb.repl.filters.ReplicationFilters;
import com.torodb.mongodb.repl.oplogreplier.offheapbuffer.OffHeapBufferConfig;
import com.torodb.mongodb.repl.sharding.MongoDbShardingConfig;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

public class GoSQLConfig {

  private final Injector essentialInjector;
  private final Function<BundleConfig, BackendBundle> backendBundleGenerator;
  private final ReplicationFilters userReplFilters;
  private final boolean unsharded;
  private final List<ShardConfigBuilder> shardConfigBuilders;
  private final LoggerFactory lifecycleLoggerFactory;
  private final OffHeapBufferConfig offHeapBufferConfig;

  private GoSQLConfig(
      Injector essentialInjector,
      Function<BundleConfig, BackendBundle> backendBundleGenerator,
      ReplicationFilters userReplFilters,
      List<ShardConfigBuilder> shardConfigBuilders,
      LoggerFactory lf,
      OffHeapBufferConfig offHeapBufferConfig) {
    this.essentialInjector = essentialInjector;
    this.backendBundleGenerator = backendBundleGenerator;
    this.userReplFilters = userReplFilters;
    this.shardConfigBuilders = shardConfigBuilders;
    this.lifecycleLoggerFactory = lf;
    this.unsharded = false;
    this.offHeapBufferConfig = offHeapBufferConfig;
  }

  private GoSQLConfig(
      Injector essentialInjector,
      Function<BundleConfig, BackendBundle> backendBundleGenerator,
      ReplicationFilters userReplFilters,
      ShardConfigBuilder shardConfigBuilder,
      LoggerFactory lf,
      OffHeapBufferConfig offHeapBufferConfig) {
    this.essentialInjector = essentialInjector;
    this.backendBundleGenerator = backendBundleGenerator;
    this.userReplFilters = userReplFilters;
    this.shardConfigBuilders = Collections.singletonList(shardConfigBuilder);
    this.lifecycleLoggerFactory = lf;
    this.unsharded = true;
    this.offHeapBufferConfig = offHeapBufferConfig;
  }

  public static GoSQLConfig createShardingConfig(
      Injector essentialInjector,
      Function<BundleConfig, BackendBundle> backendBundleGenerator,
      ReplicationFilters userReplFilters,
      List<ShardConfigBuilder> shardConfigBuilders,
      LoggerFactory lf,
      OffHeapBufferConfig offHeapBufferConfig) {
    return new GoSQLConfig(
        essentialInjector,
        backendBundleGenerator,
        userReplFilters,
        shardConfigBuilders,
        lf,
        offHeapBufferConfig);
  }

  public static GoSQLConfig createUnshardedConfig(
      Injector essentialInjector,
      Function<BundleConfig, BackendBundle> backendBundleGenerator,
      ReplicationFilters userReplFilters,
      ShardConfigBuilder shardConfigBuilder,
      LoggerFactory lf,
      OffHeapBufferConfig offHeapBufferConfig) {
    return new GoSQLConfig(
        essentialInjector,
        backendBundleGenerator,
        userReplFilters,
        shardConfigBuilder,
        lf,
        offHeapBufferConfig);
  }

  public Injector getEssentialInjector() {
    return essentialInjector;
  }

  public ThreadFactory getThreadFactory() {
    return getEssentialInjector().getInstance(ThreadFactory.class);
  }

  public boolean isUnsharded() {
    return unsharded;
  }

  public Function<BundleConfig, BackendBundle> getBackendBundleGenerator() {
    return backendBundleGenerator;
  }

  public ReplicationFilters getUserReplicationFilters() {
    return userReplFilters;
  }

  public List<ShardConfigBuilder> getShardConfigBuilders() {
    return Collections.unmodifiableList(shardConfigBuilders);
  }

  public LoggerFactory getLifecycleLoggerFactory() {
    return lifecycleLoggerFactory;
  }

  public OffHeapBufferConfig getOffHeapBufferConfig() {
    return offHeapBufferConfig;
  }

  public static interface ShardConfigBuilder {

    String getShardId();

    MongoDbShardingConfig.ShardConfig createConfig(ConsistencyHandler consistencyHandler);
  }
}
