package gosql.config.model.mongo.replication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableList;
import com.torodb.packaging.config.annotation.Description;
import com.torodb.packaging.config.model.common.EnumWithDefault;
import com.torodb.packaging.config.model.common.ListOfStringWithDefault;
import com.torodb.packaging.config.model.common.StringWithDefault;
import com.torodb.packaging.config.model.protocol.mongo.AbstractReplication;
import com.torodb.packaging.config.model.protocol.mongo.AbstractShardReplication;
import com.torodb.packaging.config.model.protocol.mongo.Role;
import com.torodb.packaging.config.util.ConfigUtils;
import com.torodb.packaging.config.validation.NotEmptyListOfSrtingWithDefault;
import com.torodb.packaging.config.validation.NotEmptySrtingWithDefault;

import java.util.List;

import javax.validation.Valid;

@JsonPropertyOrder({"replSetName", "syncSource", "ssl", "auth", "include", "exclude",
    "mongopassFile", "shards"})
public class Replication extends AbstractReplication<ShardReplication> {

  private String mongopassFile = ConfigUtils.getUserHomeFilePath(".mongopass");

  public Replication() {
    super.setSyncSource(ListOfStringWithDefault.withDefault(ImmutableList.of("localhost:27017")));
    super.setReplSetName(StringWithDefault.withDefault("rs1"));
  }

  @JsonIgnore
  public StringWithDefault getName() {
    return super.getName();
  }
  
  @NotEmptySrtingWithDefault
  @JsonProperty(required = false)
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
  
  @JsonProperty(required = true)
  public String getMongopassFile() {
    return mongopassFile;
  }

  @Valid
  @JsonProperty(required = false)
  public List<ShardReplication> getShards() {
    return super.getShardList();
  }

  public void setShards(List<ShardReplication> shards) {
    super.setShardList(shards);
  }

  public void setMongopassFile(String mongopassFile) {
    this.mongopassFile = mongopassFile;
  }

  @Override
  public void setReplSetName(StringWithDefault replSetName) {
    super.setReplSetName(replSetName);
  }

  @Override
  public void setSyncSource(ListOfStringWithDefault syncSource) {
    super.setSyncSource(syncSource);
  }
  
  public ShardReplication mergeWith(AbstractShardReplication shard) {
    ShardReplication mergedShard = new ShardReplication();
    
    merge(shard, mergedShard);
    
    return mergedShard;
  }
}
