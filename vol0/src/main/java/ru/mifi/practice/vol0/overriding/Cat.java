package ru.mifi.practice.vol0.overriding;

public final class Cat {
    public void mew() {
        System.out.println("cat mew");
    }

    public void mew(Object target) {
        System.out.println("cat mew to " + target);
    }

    public void mew(String target) {
        System.out.println("cat mew to " + target);
    }

    public static void main(String[] args) {
        Cat cat = new Cat();
        cat.mew((Object)"Tommy");
    }
}
