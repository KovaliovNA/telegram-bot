package bot.telegram.annotation;

import bot.telegram.service.initialization.validator.Keyboard;
import org.springframework.beans.factory.annotation.Qualifier;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This mark for methods that will be present telegram bot keyboard button {@link InlineKeyboardButton}.
 * Based on name, key, text, and row will be creating a bean {@link InlineKeyboardMarkup} with name,
 * that equals {@code name}. For injecting them into your bean, please mark your field using {@link Qualifier}.
 * <br>
 * <b>Note:</b> This injection doesn`t work with library lombok!
 * <br><br>
 * Accepted input parameters count and types:
 * Accepted count from 0 to 2.
 * Accepted types:
 * <ul>
 * <li>{@link CallbackQuery} - standard telegram bot pojo that contains information about keyboard executed action;</li>
 * <li>{@link AbsSender} - contains required api for sending some actions to telegram bot.</li>
 * </ul>
 * <br>
 * Accepted return types and how them will be processed:
 * <ul>
 * <li>{@link String} - will be processed as message and sent in current chat;</li>
 * <li>{@link Keyboard} - this pojo will be sent as keyboard and user message;</li>
 * <li>void - processing for this return type is not provided.</li>
 * </ul>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BotKeyboardRequestMapping {
    /**
     * Unique name of keyboard. On the basis of which all buttons for this keyboard will be aggregated.
     */
    String name();

    /**
     * Button user text.
     */
    String buttonText();

    /**
     * Button row number. It is intended to indicate on which line this button should be displayed. Eg.:
     * row = 0: key1 key2
     * row = 1: key3 key4
     * row = 2: key5 key6
     */
    int row();

    /**
     * Based on this {@code index} value, in each keyboard buttons will be sorted.
     */
    int index();
}
