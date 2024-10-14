package ru.mifi.practice.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public final class Handler implements KeyListener {
    private final List<Key> keys = new ArrayList<>();
    public final CharKey character = new CharKey(keys);
    public final Key down = new Key(keys);
    public final Key left = new Key(keys);
    public final Key right = new Key(keys);
    public final Key attack = new Key(keys);
    public final Key escape = new Key(keys);
    public final Key backspace = new Key(keys);
    public final Key enter = new Key(keys);
    public final Key menu = new Key(keys);
    public final Key up = new Key(keys);

    public Handler(Model room) {
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

    public void keyPressed(KeyEvent ke) {
        toggle(ke, true);
    }

    public void keyReleased(KeyEvent ke) {
        toggle(ke, false);
    }

    private void toggle(KeyEvent ke, boolean pressed) {
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD8) {
            up.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD2) {
            down.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD4) {
            left.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD6) {
            right.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_W) {
            up.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_S) {
            down.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_A) {
            left.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_D) {
            right.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_UP) {
            up.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
            down.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
            left.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
            right.toggle(pressed);
        }

        if (ke.getKeyCode() == KeyEvent.VK_TAB) {
            menu.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_ALT) {
            menu.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_ALT_GRAPH) {
            menu.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
            attack.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
            attack.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD0) {
            attack.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_INSERT) {
            attack.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            menu.toggle(pressed);
            enter.toggle(pressed);
        }

        if (ke.getKeyCode() == KeyEvent.VK_X) {
            menu.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_C) {
            attack.toggle(pressed);
        }

        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            escape.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            backspace.toggle(pressed);
        }

        if (ke.getKeyChar() != 0xFFFF && ke.getKeyChar() >= 13) {
            character.ch = ke.getKeyChar();
            character.toggle(pressed);
        }

    }

    public void keyTyped(KeyEvent ke) {
    }

    public boolean isAttacked() {
        return attack.clicked;
    }

    public static sealed class Key {
        public boolean down;
        public boolean clicked;
        private int presses;
        private int absorbs;

        private Key(List<Key> keys) {
            keys.add(this);
        }

        void toggle(boolean pressed) {
            if (pressed != down) {
                down = pressed;
            }
            if (pressed) {
                presses++;
            }
        }

        public void tick() {
            if (absorbs < presses) {
                absorbs++;
                clicked = true;
            } else {
                clicked = false;
            }
        }
    }

    public final class CharKey extends Key {
        private char ch = 0;

        private CharKey(List<Key> keys) {
            super(keys);
        }

        @Override
        public void tick() {
            super.tick();
            if (!clicked) {
                ch = 0;
            }
        }

        public boolean hasCharacter() {
            return ch != 0;
        }
    }
}
