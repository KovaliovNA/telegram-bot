package bot.telegram.config;

import bot.telegram.service.initialization.BotInitializer;
import bot.telegram.service.initialization.command.BotCommandsContainer;
import bot.telegram.service.initialization.command.NonCommandUpdateService;
import bot.telegram.service.input.InputDataProcessorPojo;
import bot.telegram.service.input.canny.algorithm.Parameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class AppConfig {

    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;

    @Bean
    public AbsSender absSender(DefaultBotOptions botOptions,
                               BotCommandsContainer botCommandsContainer,
                               NonCommandUpdateService nonCommandUpdateService) {
        return new BotInitializer(botOptions, name, token, botCommandsContainer.getBotCommands(),
                nonCommandUpdateService);
    }

    @Bean
    public Map<Long, InputDataProcessorPojo> inputProcessorForCommand() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<Long, Parameters> cannyFilterParameters() {
        return new ConcurrentHashMap<>();
    }
}