package bot.telegram.service.input.canny.algorithm;

/**
 * This class contains methods for masking an image array with horizontal and vertical Sobel masks.
 *
 * @author robert
 */

public class Sobel {
    //The masks for each Sobel convolution
    public static final int[][] MASK_H = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    public static final int[][] MASK_V = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

    /**
     * Send this method an int[][] array of grayscale pixel values to get a an image resulting
     * from the convolution of this image with the horizontal Sobel mask.
     *
     * @param raw int[][], array of grayscale pixel values 0-255
     * @return out  int[][], output array of convolved image.
     */
    public int[][] process(int[][] raw, int[][] mask) {
        int height = raw.length;
        int width = raw[0].length;
        int[][] out = new int[height - 2][width - 2];

        for (int r = 1; r < height - 1; r++) {
            for (int c = 1; c < width - 1; c++) {
                int sum = 0;

                for (int kr = -1; kr < 2; kr++) {
                    for (int kc = -1; kc < 2; kc++) {
                        sum += (mask[kr + 1][kc + 1] * raw[r + kr][c + kc]);
                    }
                }

                out[r - 1][c - 1] = sum;
            }
        }

        return out;
    }
}
