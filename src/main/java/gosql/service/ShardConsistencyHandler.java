package gosql.service;

import com.torodb.core.backend.BackendService;
import com.torodb.core.backend.MetaInfoKey;
import com.torodb.core.retrier.Retrier;

import java.util.concurrent.ThreadFactory;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class ShardConsistencyHandler extends AbstractConsistencyHandler {

  private final MetaInfoKey consistencyKey;

  ShardConsistencyHandler(String shardId, BackendService backendService,
      Retrier retrier, ThreadFactory threadFactory) {
    super(backendService, retrier, threadFactory);
    this.consistencyKey = () -> "repl.consistent.shard." + shardId;
  }

  @Override
  public MetaInfoKey getConsistencyKey() {
    return consistencyKey;
  }
}
