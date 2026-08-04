package ru.mifi.practice.val3.bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Банк держит своих клиентов и их карты и умеет конвертировать суммы, обращаясь
 * за курсом к вышестоящему банку. Регистрация в {@link Up} вынесена наружу
 * конструктора: пока объект не достроен, показывать его никому нельзя.
 */
public sealed interface Bank extends Currency.Converter {

    Holder createHolder(String firstName, String lastName, String middleName);

    List<Holder> search(String firstName, String lastName, String middleName);

    String getName();

    final class Up implements Currency.Converter {
        private final Map<String, Bank> banks = new HashMap<>();

        public List<Bank> banks() {
            return new ArrayList<>(banks.values());
        }

        public void register(Bank bank) {
            banks.put(bank.getName(), bank);
        }

        @Override
        public Amount convert(Amount amount, Currency to) {
            if (amount.currency().equals(to)) {
                return Amount.create(to, amount.value(), this);
            }
            throw new IllegalArgumentException(
                "Conversion from " + amount.currency().getName() + " to " + to.getName() + " is not implemented");
        }
    }

    final class Sber implements Bank {
        private final Map<Index, List<Holder>> holders = new HashMap<>();
        private final Map<Holder, List<Card>> cards = new HashMap<>();
        private final Up up;

        public Sber(Up up) {
            this.up = Objects.requireNonNull(up, "Bank cannot work without upstream");
        }

        @Override
        public Holder createHolder(String firstName, String lastName, String middleName) {
            Holder holder = new Holder.Default(firstName, lastName, middleName);
            List<Holder> found = holders.computeIfAbsent(holder.index(), key -> new ArrayList<>());
            if (found.contains(holder)) {
                throw new IllegalStateException("Holder " + holder + " already exists in " + getName());
            }
            found.add(holder);
            cards.put(holder, new ArrayList<>());
            return holder;
        }

        @Override
        public List<Holder> search(String firstName, String lastName, String middleName) {
            return List.copyOf(holders.getOrDefault(
                Index.createSearch(firstName, lastName, middleName), List.of()));
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
