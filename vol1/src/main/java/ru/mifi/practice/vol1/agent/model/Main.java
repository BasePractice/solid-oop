package ru.mifi.practice.vol1.agent.model;

/**
 * Три такта жизни аудитории — этого хватает, чтобы увидеть и обмен сообщениями,
 * и появление нового агента прямо во время работы среды.
 */
public final class Main {
    private static final int TICKS = 3;

    private Main() {
    }

    public static void main(String[] args) {
        Institute institute = new Institute();
        for (int i = 0; i < TICKS; i++) {
            System.out.printf("=== такт %d ===%n", i + 1);
            institute.tick();
        }
    }
}
