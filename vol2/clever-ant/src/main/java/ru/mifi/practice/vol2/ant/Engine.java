package ru.mifi.practice.vol2.ant;

import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Прогоняет по карте всех муравьёв, найденных через {@link ServiceLoader}. Сам движок
 * не знает ни одной реализации: стратегии подключаются модулями, а сравниваются
 * по числу шагов и по остатку еды.
 */
public final class Engine {
    private static final String MAP = "/basic.txt";
    private static final int LIMIT = 500;
    private final List<Ant> ants = new ArrayList<>();

    public Engine() {
        ServiceLoader.load(Ant.class).forEach(ants::add);
    }

    private static Grid.Place searchAnt(Grid grid) {
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Grid.Place place = grid.at(x, y);
                if (place.element() == Grid.Element.ANT) {
                    return place;
                }
            }
        }
        throw new IllegalStateException("Map has no ant on it");
    }

    public void all() throws IOException {
        all(MAP, LIMIT);
    }

    public void all(String map, int limit) throws IOException {
        URL resource = Resources.getResource(Engine.class, map);
        Grid grid = Resources.readLines(resource, StandardCharsets.UTF_8, Grid.toroidProcessor());
        Grid.Place place = searchAnt(grid);
        for (Ant ant : ants) {
            State state = new State.Default(grid, place.x(), place.y());
            boolean walking = true;
            while (walking && state.steps() < limit && state.foods() > 0) {
                walking = state.next(ant);
            }
            System.out.printf("%s: шагов %d, осталось еды %d%n", ant.getName(), state.steps(), state.foods());
        }
    }
}
