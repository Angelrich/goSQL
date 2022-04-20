
import com.gosql.core.transaction.metainf.impl.model.DocPartDataImpl;
import com.gosql.core.transaction.metainf.impl.model.DocPartRowImpl;
import com.gosql.core.transaction.metainf.impl.model.PathStack;
import com.gosql.core.transaction.metainf.impl.model.PathStack.PathArrayIdx;
import com.gosql.core.transaction.metainf.impl.model.PathStack.PathInfo;
import com.gosql.core.transaction.metainf.impl.model.PathStack.PathNodeType;
import com.gosql.kvdocument.types.ArrayType;
import com.gosql.kvdocument.types.DocumentType;
import com.gosql.kvdocument.types.KvType;
import com.gosql.kvdocument.values.KvArray;
import com.gosql.kvdocument.values.KvDocument;
import com.gosql.kvdocument.values.KvDocument.DocEntry;
import com.gosql.kvdocument.values.KvValue;

public class D2Relational {

  private static final DocumentVisitor visitor = new DocumentVisitor();

  private final ConsumerFromArrayIdx fromArrayIdx = new ConsumerFromArrayIdx();
  private final DocConsumer docComsumer = new DocConsumer();
  private final PathStack pathStack;
  private final DocPartDataCollection docPartDataCollection;

  public D2Relational(TableRefFactory tableRefFactory,
      DocPartDataCollection docPartDataCollection) {
    this.pathStack = new PathStack(tableRefFactory);
    this.docPartDataCollection = docPartDataCollection;
  }

  public void translate(KvDocument document) {
    docComsumer.consume(document);
  }

  public class DocConsumer {

    public void consume(KvDocument value) {
      PathInfo parentPath = pathStack.peek();
      DocPartDataImpl docPartData = docPartDataCollection.findDocPartData(parentPath);
      DocPartRowImpl docPartRow = docPartData.newRowObject(getDocumentIndex(parentPath), parentPath
          .findParentRowInfo());
      pathStack.pushObject(docPartRow);
      for (DocEntry<?> entry : value) {
        String key = entry.getKey();
        KvValue<?> entryValue = entry.getValue();
        if (isScalar(entryValue.getType())) {
          docPartRow.addScalar(key, entryValue);
        } else {
          docPartRow.addChild(key, entryValue);
          pathStack.pushField(key);
          entryValue.accept(visitor, docComsumer);
          pathStack.pop();
        }
      }
      pathStack.pop();
    }

    public void consume(KvArray value) {
      int i = 0;
      pathStack.pushArray();
      PathInfo current = pathStack.peek();
      DocPartDataImpl table = docPartDataCollection.findDocPartData(current);
      for (KvValue<?> val : value) {
        if (isScalar(val.getType())) {
          DocPartRowImpl rowInfo = table.newRowObject(i++, current.findParentRowInfo());
          rowInfo.addArrayItem(val);
        } else {
          pathStack.pushArrayIdx(i++);
          val.accept(visitor, fromArrayIdx);
          pathStack.pop();
        }
      }
      pathStack.pop();
    }

  }

  private class ConsumerFromArrayIdx extends DocConsumer {

    @Override
    public void consume(KvArray value) {
      PathArrayIdx current = (PathArrayIdx) pathStack.pop();
      DocPartDataImpl docPartData = docPartDataCollection.findDocPartData(current);
      DocPartRowImpl docPartRow = docPartData.newRowObject(current.getIdx(), pathStack.peek()
          .findParentRowInfo());
      docPartRow.addChildToArray(value);
      pathStack.pushArrayIdx(current.getIdx(), docPartRow);
      super.consume(value);
    }

  }

  private boolean isScalar(KvType kvType) {
    return (kvType != DocumentType.INSTANCE) && !(kvType instanceof ArrayType);
  }

  private Integer getDocumentIndex(PathInfo path) {
    if (path.is(PathNodeType.Idx)) {
      return ((PathArrayIdx) path).getIdx();
    }
    return null;
  }

}
