package ru.mifi.practice.ui;

import java.awt.image.BufferedImage;

/**
 * Лист спрайтов. Из картинки берётся только младший байт синего канала и сжимается
 * до четырёх градаций — это не цвет, а номер оттенка в палитре тайла, поэтому одну
 * и ту же картинку можно раскрасить как угодно.
 */
public final class SpriteSheet {
    final int width;
    final int height;
    final int[] pixels;

    public SpriteSheet(BufferedImage image) {
        width = image.getWidth();
        height = image.getHeight();
        pixels = image.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (pixels[i] & 0xff) / 64;
        }
    }
}
