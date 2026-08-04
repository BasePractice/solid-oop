package ru.mifi.practice.val3.bank;

/**
 * Небольшой прогон банка: заводим держателя, ищем его по ФИО и убеждаемся, что
 * повторная регистрация того же человека отклоняется.
 */
public final class Controller {
    private final Bank bank;

    public Controller(Bank bank) {
        this.bank = bank;
    }

    public static void main(String[] args) {
        Bank.Up centrobank = new Bank.Up();
        Bank sber = new Bank.Sber(centrobank);
        centrobank.register(sber);
        new Controller(sber).run();
    }

    public void run() {
        Holder holder = bank.createHolder("Иван", "Петров", "Сергеевич");
        System.out.printf("Заведён: %s%n", holder);
        System.out.printf("Найдено: %s%n", bank.search("Иван", "Петров", "Сергеевич"));
        try {
            bank.createHolder("Иван", "Петров", "Сергеевич");
            System.out.println("Дубликат прошёл — так быть не должно");
        } catch (IllegalStateException ex) {
            System.out.printf("Дубликат отклонён: %s%n", ex.getMessage());
        }
    }
}
