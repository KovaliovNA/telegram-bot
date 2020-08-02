package bot.telegram.service.initialization.keyboard;

import bot.telegram.annotation.BotController;
import bot.telegram.annotation.BotKeyboardRequestMapping;
import bot.telegram.service.initialization.BotKeyboardButton;
import bot.telegram.service.initialization.validator.BotControllerMethodReturnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotKeyboardInitializerService implements InitializingBean {

    private final ApplicationContext applicationContext;
    private final BotControllerMethodReturnService returnProcessor;
    private final BotKeyboardsContainer botKeyboardsContainer;
    private final KeyBoardsDescription keyboardsDescriptions;

    @Override
    public void afterPropertiesSet() {
        log.debug("Keyboards commands initialization...");

        Map<String, Object> botControllers = applicationContext.getBeansWithAnnotation(BotController.class);

        Map<String, List<Method>> keyboardsMapping = botControllers.values().stream()
                .map(bean -> bean.getClass().getMethods())
                .flatMap(Arrays::stream)
                .filter(method -> method.isAnnotationPresent(BotKeyboardRequestMapping.class))
                .collect(Collectors.groupingBy(method -> method.getAnnotation(BotKeyboardRequestMapping.class).name()));

        validateKeyboardCommandsUniqueness(keyboardsMapping);

        botKeyboardsContainer.setBotKeyboardButton(initializeKeyboardButtons(keyboardsMapping));

        botKeyboardsContainer.setKeyboards(initializeKeyboardsMarkup(keyboardsMapping));
    }

    private List<BotKeyboardButton> initializeKeyboardButtons(Map<String, List<Method>> keyboardsMapping) {
        return keyboardsMapping.values().stream()
                .flatMap(List::stream)
                .peek(returnProcessor::validateMethodReturn)
                .map(this::getBotKeyboardButton)
                .collect(Collectors.toUnmodifiableList());
    }

    private BotKeyboardButton getBotKeyboardButton(Method method) {
        final String button = method.getAnnotation(BotKeyboardRequestMapping.class).buttonText();
        final String desc = keyboardsDescriptions.getDescription(button);
        return new BotKeyboardButton(button) {
            @Override
            public void processBotKeyboardButton(CallbackQuery callbackQuery, AbsSender sender) {
                log.trace("The button: {} was pressed by user: {}", this.getKeyboardButtonText(),
                        callbackQuery.getFrom().getUserName());
                try {
                    Object[] args = selectArgumentsBasedOnOriginalMethod(method, callbackQuery, sender);
                    Object result = method.invoke(applicationContext.getBean(method.getDeclaringClass()), args);

                    if (desc != null && void.class.equals(method.getReturnType())) {
                        result = desc;
                    }

                    returnProcessor.processReturnResult(result, sender, callbackQuery.getMessage().getChatId());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("Menu command execution error!", e);
                }
            }
        };
    }

    private Map<String, InlineKeyboardMarkup> initializeKeyboardsMarkup(Map<String, List<Method>> keyboardsMapping) {
        return keyboardsMapping.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> createKeyboardMarkup(entry.getValue())
                ));
    }

    private InlineKeyboardMarkup createKeyboardMarkup(List<Method> buttons) {
        List<List<InlineKeyboardButton>> rows = buttons.stream()
                .collect(Collectors.groupingBy(this::getButtonRowNumber)).values().stream()
                .map(this::getButtonsRow)
                .collect(Collectors.toList());
        return new InlineKeyboardMarkup().setKeyboard(rows);
    }

    private List<InlineKeyboardButton> getButtonsRow(List<Method> methods) {
        return methods.stream()
                .map(method -> method.getAnnotation(BotKeyboardRequestMapping.class))
                .sorted(Comparator.comparingInt(BotKeyboardRequestMapping::index))
                .map(annotation -> new InlineKeyboardButton()
                        .setText(annotation.buttonText())
                        .setCallbackData(annotation.buttonText()))
                .collect(Collectors.toList());

    }

    private int getButtonRowNumber(Method method) {
        return method.getAnnotation(BotKeyboardRequestMapping.class).row();
    }

    private Object[] selectArgumentsBasedOnOriginalMethod(Method method, CallbackQuery callbackQuery, AbsSender sender) {
        List<Object> args = new LinkedList<>();
        Parameter[] parameters = method.getParameters();

        if (parameters.length > 2) {
            throw new IllegalArgumentException("Illegal arguments count in method: " + method.getName()
                    + "Count of arguments must be between 0 and 2!");
        }

        for (Parameter parameter : parameters) {
            Class<?> parameterType = parameter.getType();
            if (!parameterType.equals(CallbackQuery.class) && !parameterType.equals(AbsSender.class)) {
                throw new IllegalArgumentException("Illegal parameter type for: " + parameter.getName()
                        + " in method: " + method.getName());
            }

            if (parameterType.equals(CallbackQuery.class)) {
                args.add(callbackQuery);
            }

            if (parameterType.equals(AbsSender.class)) {
                args.add(sender);
            }
        }

        return args.toArray();
    }

    private void validateKeyboardCommandsUniqueness(Map<String, List<Method>> keyboards) {
        keyboards.forEach((key, value) -> {
            List<String> keyboardCommands = value.stream()
                    .map(method -> method.getAnnotation(BotKeyboardRequestMapping.class).buttonText())
                    .distinct()
                    .collect(Collectors.toList());

            if (keyboardCommands.size() != value.size()) {
                throw new IllegalArgumentException(
                        "Incorrect BotKeyboardRequestMapping parameters for keyboard: " + key +
                                " All commands, for one keyboard, must be unique!");
            }
        });
    }
}
