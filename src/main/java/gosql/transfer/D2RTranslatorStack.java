
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.gosql.core.TableRefFactory;
import com.gosql.core.d2r.CollectionData;
import com.gosql.core.d2r.D2RTranslator;
import com.gosql.core.d2r.IdentifierFactory;
import com.gosql.core.d2r.ReservedIdGenerator;
import com.gosql.core.transaction.metainf.MetaDatabase;
import com.gosql.core.transaction.metainf.MutableMetaCollection;
import com.gosql.kvdocument.values.KvDocument;

public class D2RTranslatorStack implements D2RTranslator {

  private final CollectionMetaInfo collectionMetaInfo;
  private final DocPartDataCollection docPartDataCollection;
  private final D2Relational d2Relational;

  @Inject
  public D2RTranslatorStack(TableRefFactory tableRefFactory, IdentifierFactory identifierFactory,
      ReservedIdGenerator ridGenerator, @Assisted MetaDatabase database,
      @Assisted MutableMetaCollection collection) {
    this.collectionMetaInfo = new CollectionMetaInfo(database, collection, identifierFactory,
        ridGenerator);
    this.docPartDataCollection = new DocPartDataCollection(collectionMetaInfo);
    this.d2Relational = new D2Relational(tableRefFactory, docPartDataCollection);
  }

  @Override
  public void translate(KvDocument doc) {
    d2Relational.translate(doc);
  }

  @Override
  public CollectionData getCollectionDataAccumulator() {
    return docPartDataCollection;
  }

}
