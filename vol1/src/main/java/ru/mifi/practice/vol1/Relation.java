package ru.mifi.practice.vol1;

import java.util.List;
import java.util.Optional;

public final class Relation {
    final Human.Women mother;
    final Human.Men father;
    private final List<Human> humans;
    private int lastHuman = 0;

    private Relation(List<Human> humans, Human.Women mother, Human.Men father) {
        this.humans = humans;
        this.mother = mother;
        this.father = father;
    }

    public static Optional<Relation> of(Human h1, Human h2, List<Human> humans) {
        if (h1 instanceof Human.Women w
                && h2 instanceof Human.Men m && h1.isReproductive() && h2.isReproductive()) {
            return Optional.of(new Relation(humans, w, m));
        } else if (h1 instanceof Human.Men m
                && h2 instanceof Human.Women w && h1.isReproductive() && h2.isReproductive()) {
            return Optional.of(new Relation(humans, w, m));
        }
        return Optional.empty();
    }

    public boolean isReproductive() {
        return this.mother.isReproductive() && this.father.isReproductive();
    }

    public boolean isDied() {
        return mother.isDied() || father.isDied();
    }

    public void tick() {
        mother.tick();
        father.tick();
        if (isReproductive() && lastHuman < 0) {
            Optional<Human> human = mother.mix(father);
            human.ifPresent(humans::add);
            human.ifPresent(h -> lastHuman = 6 * 12);
        }
        --lastHuman;
    }

    @Override
    public String toString() {
        return "Mother: " + mother.toString() + ", Father: " + father.toString() + ", Reproductive: " + isReproductive();
    }
}
