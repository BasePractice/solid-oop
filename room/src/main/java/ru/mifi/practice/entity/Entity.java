package ru.mifi.practice.entity;

public interface Entity {

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

}
