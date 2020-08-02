package bot.telegram.service.initialization.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;

import java.util.List;

@Component
public class BotCommandsContainer {
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private List<BotCommand> botCommands;
}
