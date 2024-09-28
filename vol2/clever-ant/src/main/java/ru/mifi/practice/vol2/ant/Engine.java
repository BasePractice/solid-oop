package ru.mifi.practice.vol2.ant;

import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public final class Engine {
    private final List<Ant> ants = new ArrayList<>();

    public Engine() {
        ServiceLoader<Ant> loader = ServiceLoader.load(Ant.class);
        loader.forEach(ants::add);
    }

    private static Grid.Place searchAnt(Grid grid) {
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                Grid.Place place = grid.at(i, j);
                if (place.element() == Grid.Element.ANT) {
                    return place;
                }
            }
        }
        throw new IllegalStateException("No such ant");
    }

    public void all() throws IOException {
        URL resource = Resources.getResource(Engine.class, "/basic.txt");
        Grid grid = Resources.readLines(resource, StandardCharsets.UTF_8, Grid.toroidProcessor());
        Grid.Place place = searchAnt(grid);

        for (Ant ant : ants) {
            State state = new State.Default(grid, place.x(), place.y());
            while (state.next(ant) && state.foods() > 0) {
                if (state.steps() > 500) {
                    break;
                }
            }
            System.out.printf("%s: %d/%d%n", ant.getName(), state.steps(), state.foods());
        }
    }
}
