package ru.mifi.practice.vol0.inheritance;

/**
 * Наследование в действии: {@code print} принимает {@link In1}, но для {@link In2}
 * вызывается переопределённый метод — выбор происходит по типу объекта, а не по
 * типу переменной.
 */
public final class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        print("IN1", new In1("IN1"));
        print("IN2", new In2("IN2"));
    }

    private static void print(String prefix, In1 in) {
        System.out.println(prefix + ": " + in.getName());
    }
}
