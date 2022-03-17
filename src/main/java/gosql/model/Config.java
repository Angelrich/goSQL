package gosql.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.torodb.core.annotations.DoNotChange;
import com.torodb.core.metrics.MetricsConfig;
import com.torodb.packaging.config.annotation.Description;
import com.torodb.packaging.config.validation.MutualExclusiveReplSetOrShards;
import com.torodb.packaging.config.validation.RequiredParametersForAuthentication;
import com.torodb.packaging.config.validation.SslEnabledForX509Authentication;

import gosql.config.model.backend.Backend;
import gosql.config.model.cache.OffHeapBuffer;
import gosql.config.model.logging.Logging;
import gosql.config.model.mongo.replication.Replication;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class Config implements MetricsConfig {

  @NotNull
  @Valid
  @JsonProperty(required = true)
  private Logging logging = new Logging();

  @NotNull
  @JsonProperty(required = true)
  private Boolean metricsEnabled = false;

  private OffHeapBuffer offHeapBuffer = new OffHeapBuffer();

  @Valid
  @MutualExclusiveReplSetOrShards
  @SslEnabledForX509Authentication
  @RequiredParametersForAuthentication
  @JsonProperty(required = true)
  private Replication replication = new Replication();

  @NotNull
  @Valid
  @JsonProperty(required = true)
  private Backend backend = new Backend();

  public Logging getLogging() {
    return logging;
  }

  public void setLogging(Logging logging) {
    if (logging != null) {
      this.logging = logging;
    }
  }

  @Override
  public Boolean getMetricsEnabled() {
    return metricsEnabled;
  }

  public void setMetricsEnabled(Boolean metricsEnabled) {
    this.metricsEnabled = metricsEnabled;
  }


  public OffHeapBuffer getOffHeapBuffer() {
    return offHeapBuffer;
  }

  public void setOffHeapBuffer(OffHeapBuffer offHeapBuffer) {
    if (offHeapBuffer != null) {
      this.offHeapBuffer = offHeapBuffer;
    }
  }

  @DoNotChange
  @MutualExclusiveReplSetOrShards
  public Replication getReplication() {
    return replication;
  }

  public void setReplication(Replication replication) {
    this.replication = replication;
  }

  public Backend getBackend() {
    return backend;
  }

  public void setBackend(Backend backend) {
    this.backend = backend;
  }
}
