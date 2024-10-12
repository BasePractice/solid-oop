package ru.mifi.practice.ui;

import ru.mifi.practice.room.Room;

import java.awt.*;

public interface Player {
    int x();

    int y();

    int stamina();

    int health();

    int staminaRechargeDelay();

    Point move();

    void render(Screen screen);

    void tick();

    State state();

    enum State {
        STAY,
        WALK
    }

    final class Default implements Player {
        private final Handler input;
        private final Room instance;
        private final int maxStamina = 10;
        private volatile boolean updated = false;
        private int stamina = 10;
        private int health = 10;
        private int staminaRecharge;
        private int staminaRechargeDelay = 40;
        private State state = State.STAY;
        private int x = 24;
        private int y = 24;

        public Default(Handler input, Room instance) {
            this.input = input;
            this.instance = instance;
        }

        @Override
        public int x() {
            return x;
        }

        @Override
        public int y() {
            return y;
        }

        @Override
        public int stamina() {
            return stamina;
        }

        @Override
        public int health() {
            return health;
        }

        @Override
        public int staminaRechargeDelay() {
            return staminaRechargeDelay;
        }

        @Override
        public Point move() {
            int xa = 0;
            int ya = 0;
            if (input.up.down) {
                ya--;
            }
            if (input.down.down) {
                ya++;
            }
            if (input.left.down) {
                xa--;
            }
            if (input.right.down) {
                xa++;
            }
            return new Point(xa, ya);
        }

        @Override
        public void render(Screen screen) {
            int xt = 0;
            int yt = 14;
            int xo = x - 8;
            int yo = y - 11;
            int flip1 = 0;
            int flip2 = (1 >> 3) & 1;
            int col = Color.get(-1, 100, 220, 532);
            screen.render(xo + 8 * flip1, yo + 0, xt + yt * 32, col, flip1);
            screen.render(xo + 8 - 8 * flip1, yo + 0, xt + 1 + yt * 32, col, flip1);
            screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col, flip2);
            screen.render(xo + 8 - 8 * flip2, yo + 8, xt + 1 + (yt + 1) * 32, col, flip2);
        }

        @Override
        public void tick() {
            updated = false;

            if (instance != null) {
//                Tile onTile = level.getTile(x >> 4, y >> 4);
//                if (onTile == Tile.stairsDown || onTile == Tile.stairsUp) {
//                    if (onStairDelay == 0) {
//                        changeLevel((onTile == Tile.stairsUp) ? 1 : -1);
//                        onStairDelay = 10;
//                        return;
//                    }
//                    onStairDelay = 10;
//                } else {
//                    if (onStairDelay > 0) {
//                        onStairDelay--;
//                    }
//                }
            }

            if (stamina <= 0 && staminaRechargeDelay == 0 && staminaRecharge == 0) {
                staminaRechargeDelay = 40;
            }

            if (staminaRechargeDelay > 0) {
                staminaRechargeDelay--;
            }

            if (staminaRechargeDelay == 0) {
                staminaRecharge++;
                while (staminaRecharge > 10) {
                    staminaRecharge -= 10;
                    if (stamina < maxStamina) {
                        stamina++;
                    }
                }
                updated = true;
            }

            Point move = move();
            int xa = move.x;
            int ya = move.y;

            if (staminaRechargeDelay % 2 == 0) {
                x += xa;
                y += ya;
                state = State.WALK;
//                if (xa > 0 || ya > 0) {
//                    stamina--;
//                    staminaRecharge = 0;
//                }
            } else {
                state = State.STAY;
            }
        }

        @Override
        public State state() {
            return state;
        }

        private void attack() {

        }

        private boolean isSwimming() {
            return false;
        }
    }
}
