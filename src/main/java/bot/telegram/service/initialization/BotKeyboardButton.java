package bot.telegram.service.initialization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

@RequiredArgsConstructor
public abstract class BotKeyboardButton {

    @Getter
    private final String keyboardButtonText;

    public abstract void processBotKeyboardButton(CallbackQuery callbackQuery, AbsSender sender);
}
