package gosql;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.IParameterSplitter;
import com.torodb.packaging.config.model.backend.BackendImplementation;
import com.torodb.packaging.config.model.backend.derby.AbstractDerby;

import gosql.config.model.backend.Backend;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CliConfig {

    @Parameter(names = {"--connection-pool-timeout"}, descriptionKey =
      "config.generic.connectionPoolTimeout")
  private String connectionPoolTimeout;
  @Parameter(names = {"--connection-pool-size"}, descriptionKey =
      "config.generic.connectionPoolSize")
  private String connectionPoolSize;
  
  public static Class<? extends BackendImplementation> getBackendClass(String backend) {
    backend = backend.toLowerCase(Locale.US);

    for (Class<? extends BackendImplementation> backendClass : Backend.BACKEND_CLASSES.values()) {
      String backendClassLabel = backendClass.getSimpleName().toLowerCase(Locale.US);
      if (backend.equals(backendClassLabel)) {
        return backendClass;
      }
    }

    return null;
  }

  public String getSslKeyPassword() {
    return sslKeyPassword;
  }

  public String getAuthMode() {
    return authMode;
  }

  public String getAuthUser() {
    return authUser;
  }

  public String getAuthSource() {
    return authSource;
  }

  public void addParams() {
    if (logLevel != null) {
      addParam("/logging/level", logLevel);
    }
    if (log4j2File != null) {
      addParam("/logging/log4j2File", log4j2File);
    }
    if (metricsEnabled != null) {
      addParam("/metricsEnabled", metricsEnabled ? "true" : "false");
    }
    if (offHeapBufferEnabled != null) {
      addParam("/offHeapBufferEnabled", offHeapBufferEnabled ? "true" : "false");
    }
    if (offHeapBufferPath != null) {
      addParam("/offHeapBufferPath", offHeapBufferPath);
    }
    if (offHeapBufferMaxFiles != null) {
      addParam("/offHeapBufferMaxFiles", offHeapBufferMaxFiles);
    }
    if (offHeapBufferRollCycle != null) {
      addParam("/offHeapBufferRollCycle", offHeapBufferRollCycle);
    }
    if (replSetName != null) {
      addParam("/replication/replSetName", replSetName);
    }
    if (syncSource != null) {
      addParam("/replication/syncSource", syncSource);
    }
    if (sslEnabled != null) {
      addParam("/replication/ssl/enabled", sslEnabled ? "true" : "false");
    }
    if (sslAllowInvalidHostnames != null) {
      addParam("/replication/ssl/allowInvalidHostnames",
          sslAllowInvalidHostnames ? "true" : "false");
    }
  }

  public static class ParamListValueValidator implements IValueValidator<List<String>> {

    @Override
    public void validate(String name, List<String> value) throws ParameterException {
      for (String param : value) {
        if (param.indexOf('=') == -1) {
          throw new ParameterException("Wrong parameter format: " + param);
        }
      }
    }
  }

  public static class BackendValueValidator implements IValueValidator<String> {

    @Override
    public void validate(String name, String value) throws ParameterException {
      if (value != null && (getBackendClass(value) == null || getBackendClass(value)
          == AbstractDerby.class)) {
        List<String> possibleValues = new ArrayList<>();
        Iterable<Class<? extends BackendImplementation>> backendClasses = Backend.BACKEND_CLASSES
            .values();
        for (Class<? extends BackendImplementation> backendClass : backendClasses) {
          if (backendClass == AbstractDerby.class) {
            continue;
          }
          possibleValues.add(backendClass.getSimpleName().toLowerCase(Locale.US));
        }
        throw new ParameterException("Unknown backend: " + value + " (possible values are: "
            + possibleValues + ")");
      }
    }
  }

  public static class NoParameterSplitter implements IParameterSplitter {

    @Override
    public List<String> split(String value) {
      return Arrays.asList(new String[]{value});
    }
  }
}
