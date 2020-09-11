package bot.telegram.service.input;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InputDataProcessorPojo {

    private final String command;
    private final InputDataProcessorService service;
}
