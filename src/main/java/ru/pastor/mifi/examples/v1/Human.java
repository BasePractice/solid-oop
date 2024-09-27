package ru.pastor.mifi.examples.v1;

import java.util.Optional;

public abstract sealed class Human {
    protected final Women mother;
    protected final Men father;
    protected final Object yChromosome;
    protected final Object mitochondria;
    private int ageMonth;

    protected Human(Women mother, Men father,
                    Object yChromosome, Object mitochondria, int ageMonth) {
        this.mother = mother;
        this.father = father;
        this.yChromosome = yChromosome;
        this.mitochondria = mitochondria;
        this.ageMonth = ageMonth;
    }

    protected Human(Women mother, Men father, int ageMonth) {
        this(mother, father, father.yChromosome, mother.mitochondria, ageMonth);
    }

    protected Human(Women mother, Object yChromosome, int ageMonth) {
        this(mother, null, yChromosome, mother.mitochondria, ageMonth);
    }

    protected Human(Men father, Object mitochondria, int ageMonth) {
        this(null, father, father.yChromosome, mitochondria, ageMonth);
    }

    protected Human(Object yChromosome, Object mitochondria, int ageMonth) {
        this(null, null, yChromosome, mitochondria, ageMonth);
    }

    public static Women women() {
        return new Women(new Object(), new Object(), 20 * 12);
    }

    public static Men men() {
        return new Men(new Object(), new Object(), 20 * 12);
    }

    public int ageYear() {
        return ageMonth / 12;
    }

    public void tick() {
        ++ageMonth;
    }

    public boolean isReproductive() {
        return ageYear() > 14 && ageYear() < 40;
    }

    public abstract Optional<Human> mix(Human other);

    public boolean isDied() {
        return ageYear() > 65;
    }

    protected final boolean isRandom() {
        return Math.round(Math.random()) == 1;
    }

    @Override
    public String toString() {
        return "" + ageYear();
    }

    public static final class Men extends Human {
        private Men(Women mother, Men father, int age) {
            super(mother, father, age);
        }

        private Men(Object yChromosome, Object mitochondria, int age) {
            super(yChromosome, mitochondria, age);
        }

        @Override
        public boolean isReproductive() {
            return ageYear() >= 16 && ageYear() < 40;
        }

        @Override
        public Optional<Human> mix(Human other) {
            if (isReproductive() && other.isReproductive() && other instanceof Women women && isRandom()) {
                if (isRandom()) {
                    return Optional.of(new Women(women, this, -9));
                }
                return Optional.of(new Men(women, this, -9));
            }
            return Optional.empty();
        }
    }

    public static final class Women extends Human {

        private Women(Women mother, Men father, int age) {
            super(mother, father, age);
        }

        private Women(Object yChromosome, Object mitochondria, int age) {
            super(yChromosome, mitochondria, age);
        }

        @Override
        public boolean isReproductive() {
            return ageYear() >= 14 && ageYear() < 35;
        }

        @Override
        public Optional<Human> mix(Human other) {
            if (isReproductive() && other.isReproductive() && other instanceof Men men && isRandom()) {
                if (isRandom()) {
                    return Optional.of(new Women(this, men, -9));
                }
                return Optional.of(new Men(this, men, -9));
            }
            return Optional.empty();
        }
    }
}
