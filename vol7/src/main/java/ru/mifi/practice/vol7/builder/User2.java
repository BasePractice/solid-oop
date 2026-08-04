package ru.mifi.practice.vol7.builder;

import lombok.AccessLevel;
import lombok.Builder;

/**
 * Тот же строитель, что и в {@link User}, но сгенерированный lombok — руками не написано
 * ни строчки. Видно, что {@code toBuilder} даёт новый объект, а исходный не меняется.
 */
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
public final class User2 {
    private final String name;
    private final String password;

    @Override
    public String toString() {
        return name + "/" + password;
    }

    public static void main(String[] args) {
        User2 first = User2.builder()
            .name("Name1")
            .password("Password1")
            .build();
        User2 second = first.toBuilder()
            .name("Name2")
            .build();
        System.out.printf("%s -> %s%n", first, second);
    }
}
