package bot.telegram.controller.base;

import bot.telegram.annotation.BotCommandRequestMapping;
import bot.telegram.annotation.BotController;
import bot.telegram.service.initialization.command.BotCommandsContainer;
import bot.telegram.service.initialization.command.CommandParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;

import java.util.stream.Collectors;

import static bot.telegram.util.MessageUtil.sendSimpleMessage;

@Slf4j
@BotController
@RequiredArgsConstructor
public class BaseCommandController {

    private final BotCommandsContainer botCommandsContainer;

    @BotCommandRequestMapping(command = "/start", description = "The command describing what the bot is for.")
    public void executeStartCommand(CommandParams commandParams) {
        String message = "Hi, this bot in implementing now! To see all available commands, please type /help";

        sendSimpleMessage(message, commandParams.getAbsSender(), commandParams.getChat().getId());
    }

    @BotCommandRequestMapping(command = "/help", description = "Displays all available commands.")
    public void executeHelpCommand(CommandParams commandParams) {
        String message = botCommandsContainer.getBotCommands().stream()
                .map(IBotCommand::toString)
                .collect(Collectors.joining("\n"));

        sendSimpleMessage(message, commandParams.getAbsSender(), commandParams.getChat().getId());
    }
}
