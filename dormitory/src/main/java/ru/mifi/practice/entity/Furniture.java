package ru.mifi.practice.entity;

import ru.mifi.practice.ui.Screen;

/**
 * Предмет обстановки: стол, стул, шкаф. Отличаются только именем, размером и цветом,
 * поэтому класс один — заводить три пустых наследника было бы наследованием ради
 * наследования.
 *
 * <p>Мебель занимает прямоугольник от своего угла и не пускает сквозь себя: именно
 * из-за неё в {@code AbstractDynamicEntity.move2} впервые срабатывает ветка с
 * {@code blocks}.
 */
final class Furniture extends AbstractStaticEntity {
    private final String name;
    private final int color;

    Furniture(String name, int x, int y, int width, int height, int color) {
        super(x, y, 0, width, height, width);
        this.name = name;
        this.color = color;
    }

    @Override
    public void render(Screen screen) {
        for (int dy = 0; dy < height; dy += 8) {
            for (int dx = 0; dx < width; dx += 8) {
                screen.render(x + dx, y + dy, 0, color, 0);
            }
        }
    }

    @Override
    public boolean blocks(Entity entity) {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
