package ru.mifi.practice.vol1.agent;

import static ru.mifi.practice.vol1.agent.Transport.*;

public interface Event {

    record EventMessage(Message message, Object target, Object source) implements Event {}

    @FunctionalInterface
    interface Listener {
        void onEvent(Event event);
    }
}
