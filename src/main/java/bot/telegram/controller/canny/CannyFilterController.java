package bot.telegram.controller.canny;

import bot.telegram.annotation.BotCommandRequestMapping;
import bot.telegram.annotation.BotController;
import bot.telegram.annotation.BotKeyboardRequestMapping;
import bot.telegram.service.initialization.command.CommandParams;
import bot.telegram.service.initialization.keyboard.BotKeyboardsContainer;
import bot.telegram.service.initialization.validator.Keyboard;
import bot.telegram.service.input.InputDataProcessorPojo;
import bot.telegram.service.input.canny.CannyEdgeDetectionService;
import bot.telegram.service.input.canny.algorithm.Parameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Map;

import static bot.telegram.controller.canny.CannyFilterParameter.CANNY_PARAMS_KB;

@Slf4j
@BotController
@RequiredArgsConstructor
public class CannyFilterController {

    public static final String CANNY_MAIN_KB = "CANNY_MAIN_KB";
    private static final String PROCESS_IMAGE = "Process Image";
    private static final String PARAMS = "Parameters";

    public static final String PARAMETERS_TEXT = "Image processing parameters.\nCurrent parameters is %s";
    public static final String CANNY_FILTER_KEYBOARD_MESSAGE = "Canny filter keyboard";

    private final Map<Long, InputDataProcessorPojo> inputProcessorForCommand;
    private final Map<Long, Parameters> cannyFilterParameters;
    private final BotKeyboardsContainer botKeyboardsContainer;
    private final CannyEdgeDetectionService cannyEdgeDetectionService;

    @BotCommandRequestMapping(command = "/canny", description = "Canny filter for edge detection in image.")
    public Keyboard cannyFilterStartCommand(CommandParams commandParams) {
        cannyFilterParameters.put(commandParams.getChat().getId(), Parameters.builder().build());

        return Keyboard.builder()
                .text(CANNY_FILTER_KEYBOARD_MESSAGE)
                .keyboard(botKeyboardsContainer.getKeyboardByName(CANNY_MAIN_KB))
                .build();
    }

    @BotKeyboardRequestMapping(name = CANNY_MAIN_KB, buttonText = PROCESS_IMAGE, index = 0, row = 0)
    public String processImage(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        inputProcessorForCommand.put(chatId, InputDataProcessorPojo.builder()
                .command(PROCESS_IMAGE)
                .service(cannyEdgeDetectionService)
                .build());

        initializeParametersIfNeeded(chatId);

        return "Please send message to process it.";
    }

    @BotKeyboardRequestMapping(name = CANNY_MAIN_KB, buttonText = PARAMS, index = 1, row = 0)
    public Keyboard parameters(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Parameters parameters = initializeParametersIfNeeded(chatId);

        return Keyboard.builder()
                .callbackQuery(callbackQuery)
                .text(String.format(PARAMETERS_TEXT, parameters))
                .keyboard(botKeyboardsContainer.getKeyboardByName(CANNY_PARAMS_KB))
                .build();
    }

    private Parameters initializeParametersIfNeeded(Long chatId) {
        Parameters parameters = cannyFilterParameters.get(chatId);
        if (parameters == null) {
            parameters = Parameters.builder().build();
            cannyFilterParameters.put(chatId, parameters);
        }
        return parameters;
    }
}