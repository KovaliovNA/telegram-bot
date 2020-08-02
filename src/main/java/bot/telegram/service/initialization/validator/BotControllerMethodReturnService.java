package bot.telegram.service.initialization.validator;

import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.reflect.Method;

public interface BotControllerMethodReturnService {
    void processReturnResult(Object result, AbsSender sender, Long chatId);

    void validateMethodReturn(Method method);
}
