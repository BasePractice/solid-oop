package ru.mifi.practice.entity;

import ru.mifi.practice.ui.Screen;

import java.util.UUID;

public interface Entity {

    UUID id();

    int x();

    int y();

    int z();

    default int getLightRadius() {
        return 0;
    }

    default void render(Screen screen) {

    }

    interface Static extends Entity {

    }

    interface Dynamic extends Entity {
        /**
         * Метод действия над объектом
         */
        void tick();
    }

    interface Bug extends Dynamic {

    }

    interface Human extends Dynamic {

    }

    record Data(int index, Entity entity) {

    }

}
