package bot.telegram.controller.canny;

import bot.telegram.annotation.BotController;
import bot.telegram.annotation.BotKeyboardRequestMapping;
import bot.telegram.service.initialization.keyboard.BotKeyboardsContainer;
import bot.telegram.service.initialization.validator.Keyboard;
import bot.telegram.service.input.InputDataProcessorPojo;
import bot.telegram.service.input.canny.algorithm.Parameters;
import bot.telegram.service.input.canny.ParametersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Map;

import static bot.telegram.controller.canny.CannyFilterController.CANNY_FILTER_KEYBOARD_MESSAGE;
import static bot.telegram.controller.canny.CannyFilterController.CANNY_MAIN_KB;

@Slf4j
@BotController
@RequiredArgsConstructor
public class CannyFilterParameter {

    public static final String CANNY_PARAMS_KB = "CANNY_PARAMS_KB";

    public static final String INTERMEDIATE_RESULTS = "Intermediate results";
    public static final String GAUSSIAN_RADIUS = "Gaussian radius";
    public static final String GAUSSIAN_INTENSITY = "Gaussian intensity";
    public static final String CANNY_LOW_THRESHOLD = "Low threshold";
    public static final String CANNY_HIGH_THRESHOLD = "High threshold";
    private static final String PARAMS_EXIT = "Exit";

    private final ParametersService parametersService;
    private final Map<Long, Parameters> cannyFilterParameters;
    private final Map<Long, InputDataProcessorPojo> inputProcessorForCommand;
    private final BotKeyboardsContainer botKeyboardsContainer;

    @BotKeyboardRequestMapping(name = CANNY_PARAMS_KB, buttonText = INTERMEDIATE_RESULTS, index = 0, row = 0)
    public void enableIntermediateResults(CallbackQuery callbackQuery) {
        initializeParametersInput(callbackQuery, INTERMEDIATE_RESULTS);
    }

    @BotKeyboardRequestMapping(name = CANNY_PARAMS_KB, buttonText = GAUSSIAN_RADIUS, index = 0, row = 1)
    public void gaussianRadius(CallbackQuery callbackQuery) {
        initializeParametersInput(callbackQuery, GAUSSIAN_RADIUS);
    }

    @BotKeyboardRequestMapping(name = CANNY_PARAMS_KB, buttonText = GAUSSIAN_INTENSITY, index = 1, row = 1)
    public void gaussianIntensity(CallbackQuery callbackQuery) {
        initializeParametersInput(callbackQuery, GAUSSIAN_INTENSITY);
    }

    @BotKeyboardRequestMapping(name = CANNY_PARAMS_KB, buttonText = CANNY_LOW_THRESHOLD, index = 0, row = 2)
    public void lowThreshold(CallbackQuery callbackQuery) {
        initializeParametersInput(callbackQuery, CANNY_LOW_THRESHOLD);
    }

    @BotKeyboardRequestMapping(name = CANNY_PARAMS_KB, buttonText = CANNY_HIGH_THRESHOLD, index = 1, row = 2)
    public void highThreshold(CallbackQuery callbackQuery) {
        initializeParametersInput(callbackQuery, CANNY_HIGH_THRESHOLD);
    }

    private void initializeParametersInput(CallbackQuery callbackQuery, String command) {
        Long chatId = callbackQuery.getMessage().getChatId();

        if (!cannyFilterParameters.containsKey(chatId)) {
            cannyFilterParameters.put(chatId, Parameters.builder().build());
        }

        inputProcessorForCommand.put(chatId, InputDataProcessorPojo.builder()
                .command(command)
                .service(parametersService)
                .build());
    }

    @BotKeyboardRequestMapping(name = CANNY_PARAMS_KB, buttonText = PARAMS_EXIT, index = 0, row = 3)
    public Keyboard paramsExit(CallbackQuery callbackQuery) {
        inputProcessorForCommand.entrySet()
                .removeIf(entry -> entry.getKey().equals(callbackQuery.getMessage().getChatId()));

        return Keyboard.builder()
                .callbackQuery(callbackQuery)
                .text(CANNY_FILTER_KEYBOARD_MESSAGE)
                .keyboard(botKeyboardsContainer.getKeyboardByName(CANNY_MAIN_KB))
                .build();
    }
}
