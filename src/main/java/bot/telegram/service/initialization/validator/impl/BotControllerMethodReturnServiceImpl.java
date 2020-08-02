package bot.telegram.service.initialization.validator.impl;

import bot.telegram.service.initialization.validator.BotControllerMethodReturnService;
import bot.telegram.service.initialization.validator.Keyboard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.reflect.Method;

import static bot.telegram.util.MessageUtil.*;

@Slf4j
@Service
public class BotControllerMethodReturnServiceImpl implements BotControllerMethodReturnService {
    @Override
    public void processReturnResult(Object result, AbsSender sender, Long chatId) {
        if (result == null) {
            return;
        }

        if (result instanceof String) {
            sendSimpleMessage(String.valueOf(result), sender, chatId);
        }

        if (result instanceof Keyboard) {
            Keyboard keyboard = (Keyboard) result;

            if (keyboard.getCallbackQuery() == null) {
                sendKeyboard(keyboard.getKeyboard(), keyboard.getText(), sender, chatId);
            } else {
                editKeyboard(sender, keyboard);
            }
        }
    }

    @Override
    public void validateMethodReturn(Method method) {
        Class<?> returnType = method.getReturnType();

        if (!returnType.equals(Keyboard.class) && !returnType.equals(String.class) && !returnType.equals(void.class)) {
            log.error("Illegal return type in method: {} that placed in controller: {}",
                    method.getName(), method.getDeclaringClass().getName());
            throw new IllegalArgumentException("Illegal return type in method: " + method.getName());
        }
    }
}
