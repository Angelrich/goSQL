package gosql.config.model.backend;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import com.torodb.packaging.config.annotation.Description;
import com.torodb.packaging.config.model.backend.AbstractBackend;
import com.torodb.packaging.config.model.backend.BackendImplementation;
import com.torodb.packaging.config.model.backend.ConnectionPoolConfig;

import gosql.utils.BackendDeserializer;
import gosql.utils.BackendSerializer;
import gosql.config.model.backend.mysql.MySql;
import gosql.config.model.backend.postgres.Postgres;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonSerialize(using = BackendSerializer.class)
@JsonDeserialize(using = BackendDeserializer.class)
public class Backend extends AbstractBackend {

  public static final ImmutableMap<String, Class<? extends BackendImplementation>> BACKEND_CLASSES =
      ImmutableMap.<String, Class<? extends BackendImplementation>>builder()
          .put("postgres", Postgres.class)
          .put("mysql", MySql.class)
          .build();

  @NotNull
  @Valid
  @JsonProperty(required = true)
  private Pool pool = new Pool();

  public Backend() {
    this(new Postgres());
  }

  public Backend(BackendImplementation backendImplementation) {
    super(BACKEND_CLASSES);
    setBackendImplementation(backendImplementation);
  }

  @JsonIgnore
  @Override
  public ConnectionPoolConfig getConnectionPoolConfig() {
    return getPool();
  }

  @NotNull
  @Valid
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
  @JsonSubTypes({
      @JsonSubTypes.Type(name = "postgres", value = Postgres.class),
      @JsonSubTypes.Type(name = "mysql", value = MySql.class)
      })
  @JsonProperty(required = true)
  public BackendImplementation getBackendImplementation() {
    return super.getBackendImplementation();
  }

  public Pool getPool() {
    return pool;
  }

  public void setPool(Pool pool) {
    this.pool = pool;
  }
}
