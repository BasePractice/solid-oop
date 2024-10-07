package ru.mifi.practice.vol0.inheritance;

@SuppressWarnings("PMD.UseUtilityClass")
public final class Main {
    public static void main(final String[] args) {
        In1 in1 = new In1("IN1");
        In2 in2 = new In2("IN2");
        print("IN1", in1);
        print("IN2", in2);
    }

    private static void print(String prefix, In1 in) {
        System.out.println(prefix + ": " + in.getName());
    }
}
