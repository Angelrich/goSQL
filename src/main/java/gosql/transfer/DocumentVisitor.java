
import com.gosql.core.d2r.impl.D2Relational.DocConsumer;
import com.gosql.kvdocument.values.KvArray;
import com.gosql.kvdocument.values.KvBinary;
import com.gosql.kvdocument.values.KvBoolean;
import com.gosql.kvdocument.values.KvDate;
import com.gosql.kvdocument.values.KvDecimal128;
import com.gosql.kvdocument.values.KvDeprecated;
import com.gosql.kvdocument.values.KvDocument;
import com.gosql.kvdocument.values.KvDouble;
import com.gosql.kvdocument.values.KvInstant;
import com.gosql.kvdocument.values.KvInteger;
import com.gosql.kvdocument.values.KvLong;
import com.gosql.kvdocument.values.KvMaxKey;
import com.gosql.kvdocument.values.KvMinKey;
import com.gosql.kvdocument.values.KvMongoDbPointer;
import com.gosql.kvdocument.values.KvMongoJavascript;
import com.gosql.kvdocument.values.KvMongoJavascriptWithScope;
import com.gosql.kvdocument.values.KvMongoObjectId;
import com.gosql.kvdocument.values.KvMongoRegex;
import com.gosql.kvdocument.values.KvMongoTimestamp;
import com.gosql.kvdocument.values.KvNull;
import com.gosql.kvdocument.values.KvString;
import com.gosql.kvdocument.values.KvTime;
import com.gosql.kvdocument.values.KvUndefined;
import com.gosql.kvdocument.values.KvValueVisitor;

class DocumentVisitor implements KvValueVisitor<Void, DocConsumer> {

  @Override
  public Void visit(KvBoolean value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvNull value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvArray value, DocConsumer arg) {
    arg.consume(value);
    return null;
  }

  @Override
  public Void visit(KvInteger value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvLong value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvDouble value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvString value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvDocument value, DocConsumer arg) {
    arg.consume(value);
    return null;
  }

  @Override
  public Void visit(KvMongoObjectId value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvInstant value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvDate value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvTime value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvBinary value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvMongoTimestamp value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvDecimal128 value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvMongoJavascript value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvMongoJavascriptWithScope value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvMinKey value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvMaxKey value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvUndefined kvUndefined, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvMongoRegex value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvMongoDbPointer value, DocConsumer arg) {
    return null;
  }

  @Override
  public Void visit(KvDeprecated value, DocConsumer arg) {
    return null;
  }

}
