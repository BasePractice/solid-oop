package ru.mifi.practice.val3.bank;

public sealed interface Card {

    Holder holder();

    String number();

    Amount amount();

    boolean has(Amount amount);

    interface Mutant {
        void updateAmount(Amount amount);
    }

    final class Cred implements Card, Mutant {
        private final Bank bank;
        private final Holder holder;
        private final String number;
        private final float limit;
        private final Amount amount;

        Cred(Bank bank, Holder holder, String number, Currency currency, float limit) {
            this.bank = bank;
            this.holder = holder;
            this.number = number;
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
            if (amount.value() >= 0) {
                this.amount.plus(amount);
            } else {
                this.amount.minus(amount);
            }
        }
    }

    final class Debt implements Card, Mutant {
        private final Bank bank;
        private final Holder holder;
        private final String number;
        private final Amount amount;

        Debt(Bank bank, Holder holder, String number, Currency currency) {
            this.bank = bank;
            this.holder = holder;
            this.number = number;
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
            if (amount.value() >= 0) {
                this.amount.plus(amount);
            } else {
                this.amount.minus(amount);
            }
        }
    }
}
