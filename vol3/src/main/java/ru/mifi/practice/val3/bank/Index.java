package ru.mifi.practice.val3.bank;

import java.util.Arrays;
import java.util.Objects;

/**
 * Составной ключ поиска. Сравнение и хеш считаются по содержимому — иначе два
 * одинаковых набора значений оказались бы разными ключами в {@code HashMap}.
 */
public interface Index {

    static Index createSearch(Object... values) {
        return new Search(values);
    }

    interface Get {
        Index index();
    }

    final class Search implements Index {
        private final Object[] values;

        private Search(Object[] values) {
            this.values = values;
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(values);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Search search)) {
                return false;
            }
            return Objects.deepEquals(values, search.values);
        }
    }
}
