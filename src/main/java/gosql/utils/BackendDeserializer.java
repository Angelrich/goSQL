package gosql.utils;

import com.google.common.collect.ImmutableMap;
import com.torodb.packaging.config.jackson.AbstractBackendDeserializer;

import gosql.config.model.backend.Backend;
import gosql.config.model.backend.Pool;

import org.jooq.lambda.tuple.Tuple2;

import java.util.function.BiConsumer;

public class BackendDeserializer extends AbstractBackendDeserializer<Backend> {

  public BackendDeserializer() {
    super(() -> new Backend(),
        ImmutableMap.<String, Tuple2<Class<?>, BiConsumer<Backend, Object>>>of(
            "pool", new Tuple2<>(Pool.class, (backend, value) -> backend.setPool((Pool) value))
        )
    );
  }

}
