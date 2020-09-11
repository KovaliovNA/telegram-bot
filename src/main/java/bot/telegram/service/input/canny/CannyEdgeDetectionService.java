package bot.telegram.service.input.canny;

import static bot.telegram.controller.canny.CannyFilterController.CANNY_FILTER_KEYBOARD_MESSAGE;
import static bot.telegram.controller.canny.CannyFilterController.CANNY_MAIN_KB;

import bot.telegram.service.initialization.keyboard.BotKeyboardsContainer;
import bot.telegram.service.input.InputDataProcessorService;
import bot.telegram.service.input.canny.algorithm.CannyEdgeDetector;
import bot.telegram.service.input.canny.algorithm.Parameters;
import bot.telegram.util.MessageUtil;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class CannyEdgeDetectionService implements InputDataProcessorService {

    @Value("${bot.token}")
    private String token;

    private final Map<Long, Parameters> cannyFilterParameters;
    private final BotKeyboardsContainer botKeyboardsContainer;

    @SuppressWarnings("rawtypes")
    @Override
    public void processInputForCommand(String command, Update update, AbsSender sender) {
        Message message = update.getMessage();
        List<PhotoSize> photos = message.getPhoto();
        Long chatId = message.getChatId();

        if (CollectionUtils.isEmpty(photos)) {
            MessageUtil.sendSimpleMessage("Please send a photo!", sender, chatId);
        }

        PhotoSize photo = photos.get(photos.size() - 1);
        try {
            File telegramFile = sender.execute(new GetFile().setFileId(photo.getFileId()));

            BufferedImage source = ImageIO.read(new URL(telegramFile.getFileUrl(token)));

            Parameters parameters = cannyFilterParameters.get(update.getMessage().getChatId());

            List<BufferedImage> result = processImage(source, parameters);
            SendMediaGroup mediaGroup = new SendMediaGroup()
                    .setChatId(chatId);
            ArrayList<InputMedia> processedPhotos = new ArrayList<>();

            for (int i = 0; i < result.size(); i++) {

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(result.get(i), "png", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                processedPhotos.add(new InputMediaPhoto()
                        .setMedia(is, "processedImg-" + i));
            }

            mediaGroup.setMedia(processedPhotos);

            sender.execute(mediaGroup);
            String text = "Image was processed with next parameters " + parameters.toString();
            MessageUtil.sendSimpleMessage(text, sender, chatId);
            MessageUtil.sendKeyboard(botKeyboardsContainer.getKeyboardByName(CANNY_MAIN_KB), CANNY_FILTER_KEYBOARD_MESSAGE,
                    sender, chatId);
        } catch (Exception e) {
            log.error("Processing image error!", e);
            MessageUtil.sendSimpleMessage("Processing image error!", sender, chatId);
        }
    }

    private List<BufferedImage> processImage(BufferedImage source, Parameters parameters) {
        CannyEdgeDetector canny = new CannyEdgeDetector();
        canny.setParameters(parameters);
        return canny.detectEdges(source).getResults();
    }
}