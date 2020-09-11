package bot.telegram.util;

import bot.telegram.service.initialization.validator.Keyboard;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@UtilityClass
public class MessageUtil {

    public void sendSimpleMessage(String text, AbsSender sender, Long chatId) {
        try {
            sender.execute(new SendMessage()
                    .setChatId(chatId)
                    .enableHtml(true)
                    .setText(text));
        } catch (TelegramApiException e) {
            log.error("Sending simple message error! With text: {} to chat: {}!", text, chatId, e);
        }
    }

    public void sendKeyboard(InlineKeyboardMarkup keyboardMarkup, String text, AbsSender sender, Long chatId) {
        try {
            sender.execute(new SendMessage()
                    .setChatId(chatId)
                    .setText(text)
                    .setReplyMarkup(keyboardMarkup));
        } catch (TelegramApiException e) {
            log.error("Sending keyboard error! With text: {} to chat: {}", text, chatId, e);
        }
    }

    public void editKeyboard(AbsSender sender, Keyboard keyboard) {
        CallbackQuery callbackQuery = keyboard.getCallbackQuery();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Long chatId = callbackQuery.getMessage().getChatId();

        EditMessageReplyMarkup replyMarkup = new EditMessageReplyMarkup()
                .setChatId(chatId)
                .setMessageId(messageId)
                .setReplyMarkup(keyboard.getKeyboard());

        EditMessageText editMessageText = new EditMessageText()
                .setMessageId(messageId)
                .setChatId(chatId)
                .setText(keyboard.getText());

        try {
            sender.execute(editMessageText);
            sender.execute(replyMarkup);
        } catch (TelegramApiException e) {
            log.error("Keyboard editing error! In message: {} with text: {} to chat: {}",
                    messageId, keyboard.getText(), chatId, e);
        }
    }
}
