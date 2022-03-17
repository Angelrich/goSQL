
package gosql.service;

import com.google.common.base.Preconditions;
import com.torodb.core.backend.BackendService;
import com.torodb.core.backend.DmlTransaction;
import com.torodb.core.backend.MetaInfoKey;
import com.torodb.core.backend.WriteDmlTransaction;
import com.torodb.core.exceptions.user.UserException;
import com.torodb.core.retrier.Retrier;
import com.torodb.core.retrier.RetrierAbortException;
import com.torodb.core.retrier.RetrierGiveUpException;
import com.torodb.core.services.IdleTorodbService;
import com.torodb.core.transaction.RollbackException;
import com.torodb.kvdocument.types.BooleanType;
import com.torodb.kvdocument.values.KvBoolean;
import com.torodb.kvdocument.values.KvValue;
import com.torodb.mongodb.repl.ConsistencyHandler;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.ThreadFactory;

public abstract class AbstractConsistencyHandler extends IdleTorodbService
    implements ConsistencyHandler {

  private boolean consistent;
  private final BackendService backendService;
  private final Retrier retrier;

  public AbstractConsistencyHandler(BackendService backendService, Retrier retrier,
      ThreadFactory threadFactory) {
    super(threadFactory);
    this.backendService = backendService;
    this.retrier = retrier;
  }

  public abstract MetaInfoKey getConsistencyKey();

  @Override
  protected void startUp() throws Exception {
    loadConsistent();
  }

  @Override
  protected void shutDown() throws Exception {
  }

  @Override
  public boolean isConsistent() {
    Preconditions.checkState(isRunning(), "Error");
    return consistent;
  }

  @Override
  public void setConsistent(Logger logger, boolean consistency) throws RetrierGiveUpException {
    Preconditions.checkState(isRunning(), "Error");
    this.consistent = consistency;
    flushConsistentState();
  }

  private void loadConsistent() {
    try (DmlTransaction trans = backendService.openReadTransaction()) {
      Optional<KvValue<?>> valueOpt = trans.readMetaInfo(getConsistencyKey());
      if (!valueOpt.isPresent()) {
        consistent = false;
        return;
      }
      KvValue<?> value = valueOpt.get();
      if (!value.getType().equals(BooleanType.INSTANCE)) {
        throw new IllegalStateException("Unexpected value");
      }
      consistent = ((KvBoolean) value).getPrimitiveValue();
    }
  }

  private void flushConsistentState() throws RollbackException, RetrierGiveUpException {
    retrier.retry(() -> {
      try (WriteDmlTransaction trans = backendService.openWriteTransaction()) {

        trans.writeMetaInfo(getConsistencyKey(), KvBoolean.from(consistent));
        trans.commit();
      } catch (UserException ex) {
        throw new RetrierAbortException(ex);
      }
      return null;
    });
  }


}
