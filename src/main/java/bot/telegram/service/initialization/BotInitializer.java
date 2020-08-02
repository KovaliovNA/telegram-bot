package bot.telegram.service.initialization;

import bot.telegram.service.initialization.command.NonCommandUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collection;

@Slf4j
public class BotInitializer extends TelegramLongPollingCommandBot {

    private final NonCommandUpdateService nonCommandUpdateService;
    private final String token;

    public BotInitializer(DefaultBotOptions botOptions,
                          String name,
                          String token,
                          Collection<BotCommand> botCommands,
                          NonCommandUpdateService nonCommandUpdateService) {

        super(botOptions, name);

        this.nonCommandUpdateService = nonCommandUpdateService;
        this.token = token;

        botCommands.forEach(this::register);

        registerDefaultAction((absSender, message) -> {
            log.debug("[UNKNOWN-COMMAND] User {} is trying to execute unknown command '{}'.", message.getFrom().getUserName(),
                    message.getText());

            SendMessage text = new SendMessage();
            text.setChatId(message.getChatId());
            text.setText(message.getText() + " command not found! Please, type /help to see all available commands.");

            try {
                absSender.execute(text);
            } catch (TelegramApiException e) {
                log.error("Error while replying on unknown command to user {}.", message.getFrom(), e);
            }
        });
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        nonCommandUpdateService.processUpdate(update, this);
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
