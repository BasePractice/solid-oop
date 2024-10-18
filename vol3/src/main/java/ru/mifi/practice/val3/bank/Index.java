package ru.mifi.practice.val3.bank;

import java.util.Objects;

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
            return Objects.hash(values);
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
