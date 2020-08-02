package bot.telegram.service.initialization.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface NonCommandUpdateService {

    void processUpdate(Update update, AbsSender sender);
}
