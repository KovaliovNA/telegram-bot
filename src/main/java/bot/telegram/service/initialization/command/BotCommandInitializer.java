package bot.telegram.service.initialization.command;

import bot.telegram.annotation.BotCommandRequestMapping;
import bot.telegram.annotation.BotController;
import bot.telegram.service.initialization.validator.BotControllerMethodReturnService;
import bot.telegram.service.input.InputDataProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotCommandInitializer implements InitializingBean {

    private final ApplicationContext applicationContext;
    private final Map<Long, InputDataProcessorService> delayCommandsForChat;
    private final BotControllerMethodReturnService returnProcessor;
    private final BotCommandsContainer botCommandsContainer;

    @Override
    public void afterPropertiesSet() {
        log.debug("Commands initialization...");

        botCommandsContainer.setBotCommands(initializeBotCommands());
    }

    private List<BotCommand> initializeBotCommands() {
        return applicationContext.getBeansWithAnnotation(BotController.class).values().stream()
                .map(this::transformBeanMethods)
                .flatMap(List::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<BotCommand> transformBeanMethods(Object bean) {
        return Arrays.stream(bean.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(BotCommandRequestMapping.class))
                .peek(this::validateParameters)
                .peek(returnProcessor::validateMethodReturn)
                .map(method -> createBotCommandBasedOnMethod(bean, method))
                .collect(Collectors.toList());
    }

    private BotCommand createBotCommandBasedOnMethod(Object bean, Method method) {
        BotCommandRequestMapping annotation = method.getAnnotation(BotCommandRequestMapping.class);

        log.debug("Command: {} initialization.", annotation.command());
        return new BotCommand(annotation.command(), annotation.description()) {
            @Override
            public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
                log.trace("The command: {} was executed by user: {}", annotation.command(), user.getUserName());

                removeDelayCommandIfNeeded(chat);

                try {
                    Object result = method.invoke(bean, CommandParams.builder()
                            .executedCommand(annotation.command())
                            .absSender(absSender)
                            .user(user)
                            .chat(chat)
                            .arguments(arguments)
                            .build());

                    returnProcessor.processReturnResult(result, absSender, chat.getId());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("Error while execution method: {}, in controller: {}", method.getName(),
                            method.getDeclaringClass(), e);
                } catch (Exception e) {
                    log.error("Execution of command: {} error!", annotation.command(), e);
                }
            }
        };
    }

    private void removeDelayCommandIfNeeded(Chat chat) {
        delayCommandsForChat.entrySet().removeIf(entry -> entry.getKey().equals(chat.getId()));
    }

    private void validateParameters(Method method) {
        Object bean = applicationContext.getBean(method.getDeclaringClass());
        BotCommandRequestMapping annotation = method.getAnnotation(BotCommandRequestMapping.class);

        if (method.getParameters().length != 1 || !method.getParameters()[0].getType().equals(CommandParams.class)) {
            log.error("Command: {} initialization failed!", annotation.command());
            log.error("Method: {} in bean: {} contains more than 1 argument or argument not CommandParams!", method.getName(),
                    bean.getClass());
            throw new IllegalArgumentException("Incorrect method argument in bean: " + bean.getClass());
        }
    }
}
