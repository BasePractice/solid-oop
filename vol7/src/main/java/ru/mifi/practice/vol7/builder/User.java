package ru.mifi.practice.vol7.builder;

import java.util.Objects;

public final class User {
    private final String name;
    private final String password;

    private User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(name, password);
    }

    public static final class Builder {
        private String name;
        private String password;

        private Builder() {

        }

        private Builder(String name, String password) {
            this.name = name;
            this.password = password;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public User build() {
            Objects.requireNonNull(name, "Name cannot be null");
            Objects.requireNonNull(password, "Password cannot be null");
            return new User(name, password);
        }
    }

    @Override
    public String toString() {
        return name + "/" + password;
    }

    public static void main(String[] args) {
        User first = User.builder()
            .withName("Name1")
            .withPassword("Password1")
            .build();
        User second = first.toBuilder()
            .withName("Name2")
            .build();
        System.out.printf("%s -> %s%n", first, second);
    }
}
