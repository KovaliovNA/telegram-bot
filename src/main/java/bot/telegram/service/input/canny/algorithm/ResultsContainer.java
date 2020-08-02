package bot.telegram.service.input.canny.algorithm;

import lombok.Builder;
import lombok.Getter;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

@Builder
public class ResultsContainer {
    private final boolean intermediateResultsEnabled;
    @Getter
    @Builder.Default
    private final List<BufferedImage> results = new LinkedList<>();

    public void storeIntermediateResult(int[][] img) {
        if (intermediateResultsEnabled) {
            results.add(grayscaleArrayOfPixelsToImage(img));
        }
    }

    public void storeDefaultValue(int[][] img) {
        if (!intermediateResultsEnabled) {
            results.add(grayscaleArrayOfPixelsToImage(img));
        }
    }

    public void storeMagnitude(double[][] mag) {
        if (intermediateResultsEnabled) {
            results.add(grayscaleArrayOfPixelsToImage(mag));
        }
    }

    private static BufferedImage grayscaleArrayOfPixelsToImage(int[][] raw) {
        int height = raw.length;
        int width = raw[0].length;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                img.setRGB(j, i, (raw[i][j] << 16) | (raw[i][j] << 8) | (raw[i][j]));
            }
        }

        return img;
    }

    private static BufferedImage grayscaleArrayOfPixelsToImage(double[][] raw) {
        int height = raw.length;
        int width = raw[0].length;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                img.setRGB(j, i, (int) raw[i][j] << 16 | (int) raw[i][j] << 8 | (int) raw[i][j]);
            }
        }

        return img;
    }
}
