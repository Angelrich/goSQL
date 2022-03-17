package gosql.service;

import com.torodb.core.backend.BackendService;
import com.torodb.core.backend.MetaInfoKey;
import com.torodb.core.retrier.Retrier;

import java.util.concurrent.ThreadFactory;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DefaultConsistencyHandler extends AbstractConsistencyHandler {

  private static final MetaInfoKey CONSISTENCY_KEY = () -> "repl.consistent";

  public DefaultConsistencyHandler(BackendService backendService, Retrier retrier,
      ThreadFactory threadFactory) {
    super(backendService, retrier, threadFactory);
  }

  @Override
  public MetaInfoKey getConsistencyKey() {
    return CONSISTENCY_KEY;
  }

}
