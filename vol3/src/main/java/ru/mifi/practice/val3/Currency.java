package ru.mifi.practice.val3;

import java.util.Locale;

public interface Currency {
    String getName();

    enum Standard implements Currency {
        USD, EUR, RUB;

        @Override
        public String getName() {
            return name().toUpperCase(Locale.ROOT);
        }
    }

    interface Converter {
        Amount convert(Amount amount, Currency to);
    }
}
