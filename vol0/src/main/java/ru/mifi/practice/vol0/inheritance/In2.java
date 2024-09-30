package ru.mifi.practice.vol0.inheritance;

public class In2 extends In1 {
    public In2(String name) {
        super(name);
    }

    @Override
    public String getName() {
        return "In2." + super.getName();
    }
}
