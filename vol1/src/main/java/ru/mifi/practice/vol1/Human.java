package ru.mifi.practice.vol1;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * Человек, стареющий помесячно. Иерархия здесь показана намеренно: {@code Men} и
 * {@code Women} отличаются только границами репродуктивного возраста и тем, кого
 * могут родить, поэтому общее живёт в базовом классе, а разное переопределяется.
 *
 * <p>Случайность приходит снаружи, а не из {@code Math.random()} — с одним и тем же
 * зерном популяция развивается одинаково, и поведение можно проверить тестом.
 */
public abstract sealed class Human {
    private static final int MONTHS = 12;
    private static final int ADULT = 20 * MONTHS;
    private static final int PREGNANCY = -9;
    private static final int OLD = 65;
    protected final Women mother;
    protected final Men father;
    protected final Object xyChromosome;
    protected final Object mitochondria;
    protected final Random random;
    private int ageMonth;

    protected Human(Women mother, Men father, Object xyChromosome, Object mitochondria,
                    int ageMonth, Random random) {
        this.mother = mother;
        this.father = father;
        this.xyChromosome = xyChromosome;
        this.mitochondria = mitochondria;
        this.ageMonth = ageMonth;
        this.random = Objects.requireNonNull(random, "Human cannot live without randomness");
    }

    protected Human(Women mother, Men father, int ageMonth) {
        this(
            Objects.requireNonNull(mother, "Human cannot be born without mother"),
            Objects.requireNonNull(father, "Human cannot be born without father"),
            father.xyChromosome, mother.mitochondria, ageMonth, mother.random);
    }

    public static Women women(Random random) {
        return new Women(new Object(), new Object(), ADULT, random);
    }

    public static Men men(Random random) {
        return new Men(new Object(), new Object(), ADULT, random);
    }

    public final int ageYear() {
        return ageMonth / MONTHS;
    }

    public final void tick() {
        ++ageMonth;
    }

    public boolean isReproductive() {
        return ageYear() > 14 && ageYear() < 40;
    }

    public abstract Optional<Human> mix(Human other);

    public boolean isDied() {
        return ageYear() > OLD;
    }

    protected final boolean isRandom() {
        return random.nextBoolean();
    }

    @Override
    public String toString() {
        return String.valueOf(ageYear());
    }

    public static final class Men extends Human {
        private Men(Women mother, Men father, int age) {
            super(mother, father, age);
        }

        private Men(Object xyChromosome, Object mitochondria, int age, Random random) {
            super(null, null, xyChromosome, mitochondria, age, random);
        }

        @Override
        public boolean isReproductive() {
            return ageYear() >= 16 && ageYear() < 40;
        }

        @Override
        public Optional<Human> mix(Human other) {
            if (isReproductive() && other.isReproductive() && other instanceof Women women && isRandom()) {
                if (isRandom()) {
                    return Optional.of(new Women(women, this, PREGNANCY));
                }
                return Optional.of(new Men(women, this, PREGNANCY));
            }
            return Optional.empty();
        }
    }

    public static final class Women extends Human {
        private Women(Women mother, Men father, int age) {
            super(mother, father, age);
        }

        private Women(Object xyChromosome, Object mitochondria, int age, Random random) {
            super(null, null, xyChromosome, mitochondria, age, random);
        }

        @Override
        public boolean isReproductive() {
            return ageYear() >= 14 && ageYear() < 35;
        }

        @Override
        public Optional<Human> mix(Human other) {
            if (isReproductive() && other.isReproductive() && other instanceof Men men && isRandom()) {
                if (isRandom()) {
                    return Optional.of(new Women(this, men, PREGNANCY));
                }
                return Optional.of(new Men(this, men, PREGNANCY));
            }
            return Optional.empty();
        }
    }
}
