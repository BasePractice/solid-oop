package ru.mifi.practice.vol2.ant;

import java.io.IOException;

@SuppressWarnings("PMD.UseUtilityClass")
public final class Main {
    public static void main(String[] args) throws IOException {
        Engine engine = new Engine();
        engine.all();
    }
}
