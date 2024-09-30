package ru.mifi.practice.vol0.inheritance;

@SuppressWarnings("PMD.UseUtilityClass")
public final class Main {
    public static void main(final String[] args) {
        In1 in1 = new In1("IN1");
        In2 in2 = new In2("IN2");
        In1 in3 = in2;
        System.out.println("In1: " + in1.getName());
        System.out.println("In2: " + in2.getName());
        System.out.println("In3: " + in3.getName());
    }
}
