package bot.telegram.service.input.canny;

import bot.telegram.service.initialization.keyboard.BotKeyboardsContainer;
import bot.telegram.service.input.InputDataProcessorService;
import bot.telegram.service.input.canny.algorithm.Parameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;

import static bot.telegram.controller.canny.CannyFilterController.PARAMETERS_TEXT;
import static bot.telegram.controller.canny.CannyFilterParameter.*;
import static bot.telegram.util.MessageUtil.sendKeyboard;
import static bot.telegram.util.MessageUtil.sendSimpleMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParametersService implements InputDataProcessorService {

    private final Map<Long, Parameters> cannyFilterParameters;
    private final BotKeyboardsContainer botKeyboardsContainer;

    @Override
    public void processInputForCommand(String command, Update update, AbsSender sender) {
        if (!update.hasMessage()) {
            log.error("Invalid input of parameters! By command: {} in update: {}", command, update);
        }

        Long chatId = update.getMessage().getChatId();
        String inputData = update.getMessage().getText();

        Parameters parameters = cannyFilterParameters.get(chatId);
        try {
            switch (command) {
                case INTERMEDIATE_RESULTS:
                    parameters.setEnableIntermediateResults(Boolean.parseBoolean(inputData));
                    break;
                case GAUSSIAN_RADIUS:
                    parameters.setGaussianRadius(Integer.parseInt(inputData));
                    break;
                case GAUSSIAN_INTENSITY:
                    parameters.setGaussianIntensity(Double.parseDouble(inputData));
                    break;
                case CANNY_LOW_THRESHOLD:
                    parameters.setLowThreshold(Double.parseDouble(inputData));
                    break;
                case CANNY_HIGH_THRESHOLD:
                    parameters.setHighThreshold(Double.parseDouble(inputData));
                    break;
                default:
                    return;
            }

            InlineKeyboardMarkup paramsKeyboard = botKeyboardsContainer.getKeyboardByName(CANNY_PARAMS_KB);
            String text = String.format(PARAMETERS_TEXT, parameters.toString());
            sendKeyboard(paramsKeyboard, text, sender, chatId);
        } catch (NumberFormatException e) {
            sendSimpleMessage("Invalid input for command: " + command, sender, chatId);
        }
    }
}
