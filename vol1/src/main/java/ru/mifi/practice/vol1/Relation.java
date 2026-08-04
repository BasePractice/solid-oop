package ru.mifi.practice.vol1;

import java.util.List;
import java.util.Optional;

/**
 * Пара, способная дать потомство. Пара сама двигает время своим партнёрам — в общем
 * списке людей их нет, иначе они старели бы вдвое быстрее одиночек.
 */
public final class Relation {
    private static final int PAUSE = 6 * 12;
    final Human.Women mother;
    final Human.Men father;
    private final List<Human> humans;
    private int lastHuman;

    private Relation(List<Human> humans, Human.Women mother, Human.Men father) {
        this.humans = humans;
        this.mother = mother;
        this.father = father;
    }

    public static Optional<Relation> of(Human first, Human second, List<Human> humans) {
        if (!first.isReproductive() || !second.isReproductive()) {
            return Optional.empty();
        }
        if (first instanceof Human.Women women && second instanceof Human.Men men) {
            return Optional.of(new Relation(humans, women, men));
        }
        if (first instanceof Human.Men men && second instanceof Human.Women women) {
            return Optional.of(new Relation(humans, women, men));
        }
        return Optional.empty();
    }

    public boolean isReproductive() {
        return mother.isReproductive() && father.isReproductive();
    }

    public boolean isDied() {
        return mother.isDied() || father.isDied();
    }

    public void tick() {
        mother.tick();
        father.tick();
        if (lastHuman > 0) {
            --lastHuman;
        } else if (isReproductive()) {
            mother.mix(father).ifPresent(child -> {
                humans.add(child);
                lastHuman = PAUSE;
            });
        }
    }

    @Override
    public String toString() {
        return "Mother: " + mother + ", Father: " + father + ", Reproductive: " + isReproductive();
    }
}
