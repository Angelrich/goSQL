package gosql.config.model.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.torodb.mongodb.repl.oplogreplier.offheapbuffer.BufferRollCycle;
import com.torodb.mongodb.repl.oplogreplier.offheapbuffer.OffHeapBufferConfig;
import com.torodb.packaging.config.util.ConfigUtils;

@JsonPropertyOrder({"enabled", "path", "rollCycle", "maxFiles"})
public class OffHeapBuffer implements OffHeapBufferConfig {

  @JsonProperty(required = true)
  private Boolean enabled;

  private String path;

  private BufferRollCycle rollCycle;

  private int maxFiles;

  public OffHeapBuffer() {
    enabled = false;
    path = ConfigUtils.getDefaultTempPath();
    rollCycle = BufferRollCycle.DAILY;
    maxFiles = 5;
  }

  @Override
  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public int getMaxFiles() {
    return maxFiles;
  }

  public void setMaxFiles(int maxFiles) {
    this.maxFiles = maxFiles;
  }

  @Override
  public BufferRollCycle getRollCycle() {
    return rollCycle;
  }

  public void setRollCycle(BufferRollCycle rollCycle) {
    this.rollCycle = rollCycle;
  }
}
