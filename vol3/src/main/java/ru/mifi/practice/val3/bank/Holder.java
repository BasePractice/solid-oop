package ru.mifi.practice.val3.bank;

public sealed interface Holder extends Index.Get {

    final class Default implements Holder {
        private final String firstName;
        private final String lastName;
        private final String middleName;
        private final Index searchIndex;

        public Default(String firstName, String lastName, String middleName) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.middleName = middleName;
            this.searchIndex = Index.createSearch(firstName, lastName, middleName);
        }


        @Override
        public Index index() {
            return searchIndex;
        }

        @Override
        public String toString() {
            return firstName + " " + lastName + " " + middleName;
        }
    }
}
