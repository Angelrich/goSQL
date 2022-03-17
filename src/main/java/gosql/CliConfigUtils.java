package gosql;

import static com.torodb.packaging.config.util.ConfigUtils.validateBean;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.torodb.packaging.config.util.ConfigUtils;

import gosql.config.model.Config;
import gosql.config.model.backend.Backend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CliConfigUtils {

  private CliConfigUtils() {
  }

  public static Config readConfig(CliConfig cliConfig) throws FileNotFoundException,
      JsonProcessingException,
      IOException, JsonParseException, IllegalArgumentException, Exception {
    try {
      return uncatchedReadConfig(cliConfig);
    } catch (JsonMappingException jsonMappingException) {
      throw ConfigUtils.transformJsonMappingException(jsonMappingException);
    }
  }

  private static Config uncatchedReadConfig(final CliConfig cliConfig) throws Exception {
    ObjectMapper objectMapper = ConfigUtils.mapper(true);

    Config defaultConfig = new Config();
    ObjectNode configNode = (ObjectNode) objectMapper.valueToTree(defaultConfig);

    if (cliConfig.getBackend() != null) {
      Backend backend = new Backend(
          CliConfig.getBackendClass(cliConfig.getBackend()).newInstance());
      ObjectNode backendNode = (ObjectNode) objectMapper.valueToTree(backend);
      configNode.set("backend", backendNode);
    }

    Config config = objectMapper.treeToValue(configNode, Config.class);

    validateBean(config);

    return config;
  }
}
