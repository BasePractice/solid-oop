package ru.mifi.practice.vol1.agent;

import static ru.mifi.practice.vol1.agent.Environment.Snapshot;

public interface Using {
    void tick(Snapshot snapshot);
}
