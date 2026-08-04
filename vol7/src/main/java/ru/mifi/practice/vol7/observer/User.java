package ru.mifi.practice.vol7.observer;

import java.util.Observable;

/**
 * Наблюдаемый пользователь: смена пароля рассылается подписчикам. Здесь намеренно
 * показан классический {@link Observable} из JDK — с сеттером и наследованием
 * реализации, как это делалось до Java 9. Оба приёма стоят дорого: наследование
 * съедает единственный базовый класс, а {@code Observable} объявлен устаревшим.
 */
public final class User extends Observable {
    private final String name;
    private String password;

    public User(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        setChanged();
        notifyObservers(this.password);
    }

    @Override
    public String toString() {
        return name;
    }

    public static void main(String[] args) {
        User user = new User("user");
        user.addObserver(new PrintObserver());
        user.setPassword("password");
    }
}
