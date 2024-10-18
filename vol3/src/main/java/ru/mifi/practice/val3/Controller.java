package ru.mifi.practice.val3;

import java.util.Scanner;

public abstract class Controller {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Выберите действие: ");
            scanner.useDelimiter("\n");
        }
    }
}
