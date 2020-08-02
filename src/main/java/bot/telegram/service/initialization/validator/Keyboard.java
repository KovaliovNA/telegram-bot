package bot.telegram.service.initialization.validator;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Getter
@Builder
public final class Keyboard {
    private CallbackQuery callbackQuery;
    private String text;
    @NonNull
    private InlineKeyboardMarkup keyboard;
}
