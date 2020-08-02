package bot.telegram.service.input;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InputDataProcessorPojo {

    private String command;
    private InputDataProcessorService service;
}
