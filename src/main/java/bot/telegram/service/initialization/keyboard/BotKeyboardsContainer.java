package bot.telegram.service.initialization.keyboard;

import bot.telegram.service.initialization.BotKeyboardButton;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PACKAGE;

@Component
public class BotKeyboardsContainer {
    @Getter
    @Setter(PACKAGE)
    private List<BotKeyboardButton> botKeyboardButton;
    @Setter(PACKAGE)
    private Map<String, InlineKeyboardMarkup> keyboards;

    public InlineKeyboardMarkup getKeyboardByName(String keyboardName) {
        return keyboards.get(keyboardName);
    }
}
