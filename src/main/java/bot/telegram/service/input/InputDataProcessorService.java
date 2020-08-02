package bot.telegram.service.input;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface InputDataProcessorService {

    void processInputForCommand(String command, Update update, AbsSender sender);
}
