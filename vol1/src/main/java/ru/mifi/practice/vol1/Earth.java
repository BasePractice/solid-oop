package ru.mifi.practice.vol1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Популяция, живущая по месяцам. Время двигают только пары и одиночки — каждый
 * ровно один раз за такт, поэтому свободные люди и люди в паре стареют одинаково.
 *
 * <p>Источник случайности один и передаётся внутрь: с фиксированным зерном прогон
 * повторяется, а значит его можно проверить тестом.
 */
public final class Earth {
    private static final int YEARS = 2500;
    private static final int MONTHS = 12;
    private final Random random;

    public Earth(Random random) {
        this.random = random;
    }

    public static void main(String[] args) {
        new Earth(new Random()).start();
    }

    public void start() {
        List<Relation> relations = new ArrayList<>();
        int generation = 0;
        do {
            circle(YEARS, relations);
            ++generation;
        } while (relations.isEmpty());
        System.out.println("Generation: " + generation);
        for (Relation relation : relations) {
            System.out.println(relation);
        }
    }

    private void circle(int years, List<Relation> relations) {
        List<Human> humans = new ArrayList<>();
        Relation.of(Human.women(random), Human.men(random), humans).ifPresent(relations::add);
        for (int tick = 0; tick < years * MONTHS; tick++) {
            humans.forEach(Human::tick);
            relations.forEach(Relation::tick);
            relations.removeIf(relation -> forget(relation, humans));
            humans.removeIf(Human::isDied);
            createRelation(relations, humans);
        }
    }

    private void createRelation(List<Relation> relations, List<Human> humans) {
        if (humans.size() < 3) {
            return;
        }
        Human first = humans.remove(random.nextInt(humans.size()));
        Human second = humans.remove(random.nextInt(humans.size()));
        Optional<Relation> relation = Relation.of(first, second, humans);
        relation.ifPresent(relations::add);
        if (relation.isEmpty()) {
            humans.add(first);
            humans.add(second);
        }
    }

    private static boolean forget(Relation relation, List<Human> humans) {
        if (!relation.isDied()) {
            return false;
        }
        humans.remove(relation.father);
        humans.remove(relation.mother);
        return true;
    }
}
