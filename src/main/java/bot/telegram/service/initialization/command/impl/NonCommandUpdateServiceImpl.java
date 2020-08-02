package bot.telegram.service.initialization.command.impl;

import bot.telegram.service.initialization.BotKeyboardButton;
import bot.telegram.service.initialization.command.NonCommandUpdateService;
import bot.telegram.service.initialization.keyboard.BotKeyboardsContainer;
import bot.telegram.service.input.InputDataProcessorPojo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;

import static bot.telegram.util.MessageUtil.sendSimpleMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class NonCommandUpdateServiceImpl implements NonCommandUpdateService {

    private final BotKeyboardsContainer botKeyboardsContainer;
    private final Map<Long, InputDataProcessorPojo> inputProcessorForCommand;

    @Override
    public void processUpdate(Update update, AbsSender sender) {
        if (update.hasCallbackQuery()) {
            botKeyboardsContainer.getBotKeyboardButton().stream()
                    .filter(botMenuCommand -> areMenuCommandAndUpdateDataEquals(update, botMenuCommand))
                    .findFirst()
                    .ifPresentOrElse(button -> button.processBotKeyboardButton(update.getCallbackQuery(), sender),
                            () -> nonCommandMessage(update, sender)
                    );
        } else {
            executeDelayCommandIfNeeded(update, sender);
        }
    }

    private boolean areMenuCommandAndUpdateDataEquals(Update update, BotKeyboardButton botKeyboardButton) {
        return botKeyboardButton.getKeyboardButtonText().equals(update.getCallbackQuery().getData());
    }

    private void executeDelayCommandIfNeeded(Update update, AbsSender sender) {
        inputProcessorForCommand.entrySet().stream()
                .filter(entry -> isCurrentChatHasDelayCommand(update, entry.getKey()))
                .findFirst()
                .ifPresentOrElse(entry -> processInput(entry.getValue(), entry.getKey(), update, sender),
                        () -> nonCommandMessage(update, sender));
    }

    private void processInput(InputDataProcessorPojo pojo, Long chatId, Update update, AbsSender sender) {
        pojo.getService().processInputForCommand(pojo.getCommand(), update, sender);
        inputProcessorForCommand.remove(chatId);
    }

    private boolean isCurrentChatHasDelayCommand(Update update, Long delayChatId) {
        Long currentChatId = getChatId(update);
        return currentChatId != null && currentChatId.equals(delayChatId);
    }

    private Long getChatId(Update update) {
        Message message = update.getMessage();
        if (message != null) {
            return message.getChatId();
        }

        CallbackQuery callbackQuery = update.getCallbackQuery();
        if (callbackQuery != null) {
            return callbackQuery.getMessage().getChatId();
        }

        return null;
    }

    private void nonCommandMessage(Update update, AbsSender sender) {
        Long chatId = getChatId(update);

        if (update.getMessage() != null) {
            sendSimpleMessage("I don`t know what to do... Please type /help!", sender, chatId);
        }

        if (update.getCallbackQuery() != null) {
            sendSimpleMessage("Command for this menu was interrupted!", sender, chatId);
        }
    }
}
