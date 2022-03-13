package gosql.config.model.mongo.replication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.torodb.packaging.config.annotation.Description;
import com.torodb.packaging.config.model.common.EnumWithDefault;
import com.torodb.packaging.config.model.common.ListOfStringWithDefault;
import com.torodb.packaging.config.model.common.StringWithDefault;
import com.torodb.packaging.config.model.protocol.mongo.AbstractShardReplication;
import com.torodb.packaging.config.model.protocol.mongo.Auth;
import com.torodb.packaging.config.model.protocol.mongo.Role;
import com.torodb.packaging.config.model.protocol.mongo.Ssl;
import com.torodb.packaging.config.validation.NotEmptyListOfSrtingWithDefault;

import javax.validation.constraints.NotNull;

@JsonPropertyOrder({"replSetName", "syncSource", "ssl", "auth"})
public class ShardReplication extends AbstractShardReplication {

  @JsonProperty(required = true)
  public StringWithDefault getReplSetName() {
    return super.getReplSetName();
  }

  @JsonIgnore
  public EnumWithDefault<Role> getRole() {
    return super.getRole();
  }

  @NotEmptyListOfSrtingWithDefault
  @JsonProperty(required = false)
  public ListOfStringWithDefault getSyncSource() {
    return super.getSyncSource();
  }

  @NotNull
  @JsonProperty(required = true)
  public Ssl getSsl() {
    return super.getSsl();
  }

  @NotNull
  @JsonProperty(required = true)
  public Auth getAuth() {
    return super.getAuth();
  }
}
