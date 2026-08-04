package ru.mifi.practice.ui;

/**
 * Палитра из 216 цветов — по шесть градаций на канал, как в шеститоновой гамме
 * {@link Color}. Экран хранит не цвета, а индексы в этой таблице, поэтому кадр
 * занимает байт на пиксель, а не четыре.
 *
 * <p>Каждый цвет слегка сдвинут к своей яркости и подрезан по контрасту — картинка
 * получается мягче, чем при равномерной сетке.
 */
final class Palette {
    private static final int STEPS = 6;
    private static final int MAX = 255;
    private final int[] colors;

    Palette() {
        this.colors = new int[256];
        int index = 0;
        for (int r = 0; r < STEPS; r++) {
            for (int g = 0; g < STEPS; g++) {
                for (int b = 0; b < STEPS; b++) {
                    colors[index++] = blend(r * MAX / 5, g * MAX / 5, b * MAX / 5);
                }
            }
        }
    }

    int rgb(int index) {
        return colors[index];
    }

    private static int blend(int red, int green, int blue) {
        int mid = (red * 30 + green * 59 + blue * 11) / 100;
        return soften(red, mid) << 16 | soften(green, mid) << 8 | soften(blue, mid);
    }

    private static int soften(int channel, int mid) {
        return (channel + mid) / 2 * 230 / MAX + 10;
    }
}
