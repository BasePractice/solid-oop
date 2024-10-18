package ru.mifi.practice.val3;

import static ru.mifi.practice.val3.Currency.Converter;

public interface Amount {

    static Amount create(Currency currency, double amount, Converter converter) {
        return new Default(converter, currency, amount);
    }

    Currency currency();

    double value();

    boolean hasLimit(Amount amount, double limit);

    default boolean has(Amount amount) {
        return hasLimit(amount, 0);
    }

    void minus(Amount amount);

    void plus(Amount amount);

    final class Default implements Amount {
        private final Converter converter;
        private final Currency currency;
        private double value;

        private Default(Converter converter, Currency currency, double value) {
            this.converter = converter;
            this.currency = currency;
            this.value = value;
        }

        @Override
        public Currency currency() {
            return currency;
        }

        @Override
        public double value() {
            return value;
        }

        @Override
        public boolean hasLimit(Amount amount, double limit) {
            Amount convert = converter.convert(amount, currency);
            return this.value + limit > convert.value();
        }

        @Override
        public void minus(Amount amount) {
            Amount convert = converter.convert(amount, currency);
            this.value -= convert.value();
        }

        @Override
        public void plus(Amount amount) {
            Amount convert = converter.convert(amount, currency);
            this.value += convert.value();
        }
    }
}
