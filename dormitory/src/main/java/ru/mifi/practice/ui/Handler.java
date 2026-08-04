package ru.mifi.practice.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Клавиатура. Каждая клавиша помнит не только «нажата ли она сейчас», но и число
 * нажатий: игровой цикл идёт быстрее, чем человек отпускает клавишу, и без счётчика
 * одно нажатие сработало бы десятки раз подряд.
 *
 * <p>Раскладка задана таблицей, а не цепочкой сравнений: на одно действие приходится
 * несколько клавиш, и добавить ещё одну — это добавить строчку.
 */
public final class Handler implements KeyListener {
    private final List<Key> keys = new ArrayList<>();
    public final Key up = new Key(keys);
    public final Key down = new Key(keys);
    public final Key left = new Key(keys);
    public final Key right = new Key(keys);
    public final Key attack = new Key(keys);
    public final Key menu = new Key(keys);
    private final Map<Integer, Key> layout = new HashMap<>();

    public Handler(Model room) {
        bind(up, KeyEvent.VK_W, KeyEvent.VK_UP, KeyEvent.VK_NUMPAD8);
        bind(down, KeyEvent.VK_S, KeyEvent.VK_DOWN, KeyEvent.VK_NUMPAD2);
        bind(left, KeyEvent.VK_A, KeyEvent.VK_LEFT, KeyEvent.VK_NUMPAD4);
        bind(right, KeyEvent.VK_D, KeyEvent.VK_RIGHT, KeyEvent.VK_NUMPAD6);
        bind(attack, KeyEvent.VK_SPACE, KeyEvent.VK_CONTROL, KeyEvent.VK_C, KeyEvent.VK_NUMPAD0);
        bind(menu, KeyEvent.VK_ENTER, KeyEvent.VK_TAB, KeyEvent.VK_X, KeyEvent.VK_ALT);
        room.addHandler(this);
    }

    public void releaseAll() {
        for (Key key : keys) {
            key.down = false;
        }
    }

    public void tick() {
        for (Key key : keys) {
            key.tick();
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        toggle(event, true);
    }

    @Override
    public void keyReleased(KeyEvent event) {
        toggle(event, false);
    }

    @Override
    public void keyTyped(KeyEvent event) {
        //Символ берётся из keyPressed вместе с кодом клавиши, отдельное событие не нужно
    }

    public boolean isAttacked() {
        return attack.clicked;
    }

    public boolean isUsed() {
        return menu.clicked;
    }

    private void bind(Key key, int... codes) {
        for (int code : codes) {
            layout.put(code, key);
        }
    }

    private void toggle(KeyEvent event, boolean pressed) {
        Key key = layout.get(event.getKeyCode());
        if (key != null) {
            key.toggle(pressed);
        }
    }

    /**
     * Состояние одной клавиши. {@code down} — удерживается прямо сейчас, {@code clicked} —
     * в этом такте засчитано ровно одно новое нажатие.
     */
    public static final class Key {
        public boolean down;
        public boolean clicked;
        private int presses;
        private int absorbs;

        private Key(List<Key> keys) {
            keys.add(this);
        }

        void toggle(boolean pressed) {
            down = pressed;
            if (pressed) {
                presses++;
            }
        }

        void tick() {
            clicked = absorbs < presses;
            if (clicked) {
                absorbs++;
            }
        }
    }
}
