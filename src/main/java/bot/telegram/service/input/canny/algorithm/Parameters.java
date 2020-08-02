package bot.telegram.service.input.canny.algorithm;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Parameters {
    private boolean enableIntermediateResults;
    @Builder.Default
    private int gaussianRadius = 7;
    @Builder.Default
    private double gaussianIntensity = 1.5;
    @Builder.Default
    private double lowThreshold = 0.001;
    @Builder.Default
    private double highThreshold = 0.06;

    @Override
    public String toString() {
        return "\nShow intermediate results enabled: " + enableIntermediateResults +
                "\nGaussian radius: " + gaussianRadius +
                "\nGaussian intensity: " + gaussianIntensity +
                "\nLow threshold: " + lowThreshold +
                "\nHigh threshold: " + highThreshold;
    }
}
