package ru.mifi.practice.val3.bank;

import java.util.Objects;

/**
 * Карта держателя. Кредитная отличается от дебетовой только тем, сколько можно
 * увести баланс в минус, поэтому проверка средств живёт в {@code has}, а не в вызывающем коде.
 */
public sealed interface Card {

    Holder holder();

    String number();

    Amount amount();

    boolean has(Amount amount);

    interface Mutant {
        void updateAmount(Amount amount);
    }

    final class Cred implements Card, Mutant {
        private final Holder holder;
        private final String number;
        private final float limit;
        private final Amount amount;

        Cred(Bank bank, Holder holder, String number, Currency currency, float limit) {
            this.holder = Objects.requireNonNull(holder, "Card cannot exist without holder");
            this.number = Objects.requireNonNull(number, "Card cannot exist without number");
            this.limit = limit;
            this.amount = Amount.create(currency, 0, bank);
        }

        @Override
        public Holder holder() {
            return holder;
        }

        @Override
        public String number() {
            return number;
        }

        @Override
        public Amount amount() {
            return amount;
        }

        @Override
        public boolean has(Amount amount) {
            return this.amount.hasLimit(amount, limit);
        }

        @Override
        public void updateAmount(Amount amount) {
            this.amount.plus(amount);
        }
    }

    final class Debt implements Card, Mutant {
        private final Holder holder;
        private final String number;
        private final Amount amount;

        Debt(Bank bank, Holder holder, String number, Currency currency) {
            this.holder = Objects.requireNonNull(holder, "Card cannot exist without holder");
            this.number = Objects.requireNonNull(number, "Card cannot exist without number");
            this.amount = Amount.create(currency, 0, bank);
        }

        @Override
        public Holder holder() {
            return holder;
        }

        @Override
        public String number() {
            return number;
        }

        @Override
        public Amount amount() {
            return amount;
        }

        @Override
        public boolean has(Amount amount) {
            return this.amount.has(amount);
        }

        @Override
        public void updateAmount(Amount amount) {
            this.amount.plus(amount);
        }
    }
}
