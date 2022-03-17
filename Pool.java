package gosql.config.model.backend;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.torodb.packaging.config.model.backend.ConnectionPoolConfig;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@JsonPropertyOrder({"connectionPoolTimeout", "connectionPoolSize"})
public class Pool implements ConnectionPoolConfig {

  @NotNull
  @JsonProperty(required = true)
  private Long connectionPoolTimeout = 10L * 1000;
  @NotNull
  @Min(20)
  @JsonProperty(required = true)
  private Integer connectionPoolSize = 30;
  @NotNull
  @Min(1)
  @JsonIgnore
  private Integer reservedReadPoolSize = 1;

  @Override
  public Long getConnectionPoolTimeout() {
    return connectionPoolTimeout;
  }

  public void setConnectionPoolTimeout(Long connectionPoolTimeout) {
    this.connectionPoolTimeout = connectionPoolTimeout;
  }

  @Override
  public Integer getConnectionPoolSize() {
    return connectionPoolSize;
  }

  public void setConnectionPoolSize(Integer connectionPoolSize) {
    this.connectionPoolSize = connectionPoolSize;
  }

  @Override
  public Integer getReservedReadPoolSize() {
    return reservedReadPoolSize;
  }

  public void setReservedReadPoolSize(Integer reserverdReadPoolSize) {
    this.reservedReadPoolSize = reserverdReadPoolSize;
  }

}
