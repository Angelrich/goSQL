package gosql.utils;

import com.google.common.collect.ImmutableMap;
import com.torodb.packaging.config.jackson.AbstractBackendSerializer;

import gosql.config.model.backend.Backend;

public class BackendSerializer extends AbstractBackendSerializer<Backend> {

  public BackendSerializer() {
    super(
        ImmutableMap.of(
            "pool", (backend) -> backend.getPool()
        )
    );
  }

}
