package bot.telegram.service.initialization.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Getter
@Setter
@Builder
public final class CommandParams {

    private String executedCommand;
    private AbsSender absSender;
    private User user;
    private Chat chat;
    private String[] arguments;
}
