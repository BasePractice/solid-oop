package ru.mifi.practice.vol1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class Earth {
    private final Random random = new Random();

    public static void main(String[] args) {
        new Earth().start();
    }

    private void start() {
        random.setSeed(System.currentTimeMillis());
        List<Relation> relations = new ArrayList<>();
        int generation = 0;
        do {
            circle(2500, relations);
            ++generation;
        } while (relations.isEmpty());
        System.out.println("Generation: " + generation);
        for (Relation relation : relations) {
            System.out.println(relation);
        }
    }

    private void circle(int years, List<Relation> relations) {
        Human.Women women = Human.women();
        Human.Men men = Human.men();
        List<Human> humans = new ArrayList<>();
        humans.add(women);
        humans.add(men);
        Relation.of(women, men, humans).ifPresent(relations::add);
        int tick = 0;
        while (tick < years * 12) {
            humans.forEach(Human::tick);
            relations.forEach(Relation::tick);
            deleteDied(relations, humans);
            deleteDied(humans);
            createRelation(relations, humans);
            ++tick;
        }
    }

    private void createRelation(List<Relation> relations, List<Human> humans) {
        if (humans.size() >= 3) {
            int n = random.nextInt(0, humans.size());
            Human h1 = humans.remove(n);
            n = random.nextInt(0, humans.size());
            Human h2 = humans.remove(n);
            Optional<Relation> relation = Relation.of(h1, h2, humans);
            relation.ifPresent(relations::add);
            if (relation.isEmpty()) {
                humans.add(h1);
                humans.add(h2);
            }
        }
    }

    @SuppressWarnings("PMD.ForLoopCanBeForeach")
    private static void deleteDied(List<Relation> relations, List<Human> humans) {
        for (int i = 0; i < relations.size(); i++) {
            Relation relation = relations.get(i);
            if (relation.isDied()) {
                relations.remove(relation);
                humans.remove(relation.father);
                humans.remove(relation.mother);
            }
        }
    }

    @SuppressWarnings("PMD.ForLoopCanBeForeach")
    private static void deleteDied(List<Human> humans) {
        for (int i = 0; i < humans.size(); i++) {
            Human human = humans.get(i);
            if (human.isDied()) {
                humans.remove(human);
            }
        }
    }
}
