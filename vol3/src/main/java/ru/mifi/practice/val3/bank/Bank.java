package ru.mifi.practice.val3.bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public sealed interface Bank extends Currency.Converter {
    Up CENTROBANK = new Up();
    Bank SBER = new Sber(CENTROBANK);

    Holder createHolder(String firstName, String lastName, String middleName);

    List<Holder> search(String firstName, String lastName, String middleName);

    String getName();

    final class Up implements Currency.Converter {
        private final Map<String, Bank> banks = new HashMap<>();

        private Up() {

        }

        public List<Bank> banks() {
            return new ArrayList<>(banks.values());
        }

        @Override
        public Amount convert(Amount amount, Currency to) {
            if (amount.currency().equals(to)) {
                return Amount.create(to, amount.value(), this);
            }
            throw new IllegalArgumentException("Not implement yet");
        }
    }

    final class Sber implements Bank {
        private final Map<Index, List<Holder>> holders = new HashMap<>();
        private final Map<Holder, List<Card>> cards = new HashMap<>();
        private final Up up;

        Sber(Up up) {
            this.up = up;
            up.banks.put(getName(), this);
        }

        @Override
        public Holder createHolder(String firstName, String lastName, String middleName) {
            Holder holder = new Holder.Default(firstName, lastName, middleName);
            Index index = holder.index();
            List<Holder> list = holders.computeIfAbsent(index, k -> new ArrayList<>());
            if (list.contains(holder)) {
                throw new IllegalStateException("Holder already exists");
            }
            list.add(holder);
            cards.put(holder, new ArrayList<>());
            return holder;
        }

        @Override
        public List<Holder> search(String firstName, String lastName, String middleName) {
            Index index = Index.createSearch(firstName, lastName, middleName);
            return holders.getOrDefault(index, List.of());
        }

        @Override
        public String getName() {
            return "sber";
        }

        @Override
        public Amount convert(Amount amount, Currency to) {
            return up.convert(amount, to);
        }
    }
}
