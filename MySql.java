package gosql.config.model.backend.mysql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.torodb.packaging.config.model.backend.mysql.AbstractMySql;
import com.torodb.packaging.config.util.ConfigUtils;
import com.torodb.packaging.config.validation.ExistsAnyPassword;
import com.torodb.packaging.config.validation.Host;
import com.torodb.packaging.config.validation.Port;

import javax.validation.constraints.NotNull;

@JsonPropertyOrder({"host", "port", "database", "user", "password", "toropassFile",
    "applicationName", "includeForeignKeys", "ssl"})
@ExistsAnyPassword
public class MySql extends AbstractMySql {

  public MySql() {
    super(
        "localhost",
        3306,
        "torod",
        "torodb",
        null,
        ConfigUtils.getUserHomeFilePath(".toropass"),
        "toro",
        false, 
        false
    );
  }

  @NotNull
  @Host
  @JsonProperty(required = true)
  @Override
  public String getHost() {
    return super.getHost();
  }

  @NotNull
  @Port
  @JsonProperty(required = true)
  @Override
  public Integer getPort() {
    return super.getPort();
  }

  @NotNull
  @JsonProperty(required = true)
  @Override
  public String getDatabase() {
    return super.getDatabase();
  }

  @NotNull
  @JsonProperty(required = true)
  @Override
  public String getUser() {
    return super.getUser();
  }

  @JsonIgnore
  @Override
  public String getPassword() {
    return super.getPassword();
  }

  @NotNull
  @JsonProperty(required = true)
  @Override
  public String getToropassFile() {
    return super.getToropassFile();
  }

  @NotNull
  @JsonProperty(required = true)
  @Override
  public String getApplicationName() {
    return super.getApplicationName();
  }

  @JsonIgnore
  @Override
  public Boolean getIncludeForeignKeys() {
    return super.getIncludeForeignKeys();
  }
  
  @JsonProperty(required = false)
  @NotNull
  @Override
  public Boolean getSsl() {
    return super.getSsl();
  }
}
