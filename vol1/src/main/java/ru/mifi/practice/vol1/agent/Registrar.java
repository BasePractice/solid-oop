package ru.mifi.practice.vol1.agent;

import static ru.mifi.practice.vol1.agent.Agent.Factory;
import static ru.mifi.practice.vol1.agent.Agent.Iterator;

public interface Registrar {
    void register(Factory factory);

    void register(Iterator iterator);
}
