package bot.telegram.config;

import bot.telegram.service.initialization.keyboard.KeyBoardsDescription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

@Configuration
public class PropertiesConfig {

    private final Yaml yaml = new Yaml();

    @Value("classpath:/keyboards-button-descriptions.yml")
    private Resource keyboardDescriptionResource;

    @Bean
    public KeyBoardsDescription keyboardsDescriptions() throws IOException {
        return yaml.loadAs(keyboardDescriptionResource.getInputStream(), KeyBoardsDescription.class);
    }
}