package ru.mifi.practice.ui;

/**
 * Цвет в шеститоновой гамме: число вида {@code 543} читается как «красный 5,
 * зелёный 4, синий 3». Четыре таких числа упаковываются в один int — это четыре
 * оттенка одного тайла, а {@code -1} означает прозрачность.
 */
public interface Color {
    static int get(int a, int b, int c, int d) {
        return (get(d) << 24) + (get(c) << 16) + (get(b) << 8) + (get(a));
    }

    static int get(int d) {
        if (d < 0) {
            return 255;
        }
        int r = d / 100 % 10;
        int g = d / 10 % 10;
        int b = d % 10;
        return r * 36 + g * 6 + b;
    }
}
