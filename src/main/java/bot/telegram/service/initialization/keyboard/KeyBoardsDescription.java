package bot.telegram.service.initialization.keyboard;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class KeyBoardsDescription {
    @Getter
    @Setter
    private List<Descriptions> descriptions;

    @Getter
    @Setter
    public static class Descriptions {
        private String button;
        private String desc;
    }

    public String getDescription(String button) {
        return descriptions.stream()
                .parallel()
                .filter(pair -> pair.getButton().equals(button))
                .findAny()
                .map(Descriptions::getDesc)
                .filter(StringUtils::isNotBlank)
                .orElse(null);
    }
}
