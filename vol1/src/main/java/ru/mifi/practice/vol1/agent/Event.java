package ru.mifi.practice.vol1.agent;


public interface Event {

    @FunctionalInterface
    interface Listener {
        void onEvent(Event event);
    }

    record EventMessage(Transport.Message message, Object target, Object source) implements Event {
    }
}
