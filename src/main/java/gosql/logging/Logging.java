package gosql.config.model.logging;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.torodb.packaging.config.annotation.Description;
import com.torodb.packaging.config.model.generic.LogLevel;
import com.torodb.packaging.config.model.generic.LogPackages;

@JsonPropertyOrder({"logLevel", "logPackages", "logFile", "log4j2File"})
public class Logging {

  @JsonProperty(required = false)
  private LogLevel level;
  private LogPackages packages;
  private String file;
  private String log4j2File;

  public LogLevel getLevel() {
    return level;
  }

  public void setLevel(LogLevel logLevel) {
    this.level = logLevel;
  }

  public LogPackages getPackages() {
    return packages;
  }

  public void setPackages(LogPackages logPackages) {
    this.packages = logPackages;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String logFile) {
    this.file = logFile;
  }

  public String getLog4j2File() {
    return log4j2File;
  }

  public void setLog4j2File(String log4j2File) {
    this.log4j2File = log4j2File;
  }

}
