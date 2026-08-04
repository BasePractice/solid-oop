package ru.mifi.practice.val3.bank;

/**
 * Держатель карт. Реализация — record, поэтому {@code equals} и {@code hashCode}
 * сравнивают по ФИО, а не по ссылке: без этого банк не заметил бы повторную
 * регистрацию одного и того же человека.
 */
public sealed interface Holder extends Index.Get {

    record Default(String firstName, String lastName, String middleName) implements Holder {
        @Override
        public Index index() {
            return Index.createSearch(firstName, lastName, middleName);
        }

        @Override
        public String toString() {
            return firstName + " " + lastName + " " + middleName;
        }
    }
}
