package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;
import ru.mifi.practice.ui.Handler;
import ru.mifi.practice.ui.Screen;
import ru.mifi.practice.ui.Tile;

import java.awt.Point;
import java.util.Optional;

import static ru.mifi.practice.ui.Color.get;

/**
 * Игрок. Клавиши превращаются в шаг, удар и применение предмета; сам шаг считает
 * базовый класс, потому что упереться в стену может любая подвижная сущность.
 *
 * <p>Выносливость тратится на каждый удар и восстанавливается с задержкой — иначе
 * тапком можно было бы махать без остановки.
 */
final class Player extends AbstractDynamicEntity implements Human {
    private static final int MAX_STAMINA = 10;
    private static final int BARE_LIGHT = 2;
    private final Handler input;
    private final Room room;
    private final Inventory inventory;
    private Item attackItem;
    private int walkDist = 0;
    private int dir = 0;
    private int hurtTime = 0;
    private int xKnockBack;
    private int yKnockBack;
    private int attackTime;
    private int attackDir;
    private int stamina = 10;
    private int staminaRecharge;
    private int staminaRechargeDelay = 40;
    private State state = State.STAY;

    Player(Handler input, Room room, Inventory inventory) {
        super(24, 24, 0, 1);
        this.input = input;
        this.room = room;
        this.inventory = inventory;
        this.health = 10;
    }

    @Override
    public Inventory inventory() {
        return inventory;
    }

    @Override
    public int stamina() {
        return stamina;
    }

    @Override
    public int staminaRechargeDelay() {
        return staminaRechargeDelay;
    }

    @Override
    public Point move() {
        knockBack();
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
        if (xa == 0 && ya == 0) {
            return new Point(0, 0);
        }
        walkDist++;
        dir = direction(xa, ya);
        if (!move(room, xa, ya)) {
            return new Point(0, 0);
        }
        return new Point(xa, ya);
    }

    private void knockBack() {
        if (xKnockBack < 0) {
            move2(room, -1, 0);
            xKnockBack++;
        }
        if (xKnockBack > 0) {
            move2(room, 1, 0);
            xKnockBack--;
        }
        if (yKnockBack < 0) {
            move2(room, 0, -1);
            yKnockBack++;
        }
        if (yKnockBack > 0) {
            move2(room, 0, 1);
            yKnockBack--;
        }
    }

    private static int direction(int xa, int ya) {
        if (ya > 0) {
            return 0;
        }
        if (ya < 0) {
            return 1;
        }
        return xa < 0 ? 2 : 3;
    }

    @Override
    public void render(Screen screen) {
        int xt = 0;

        int flip1 = (walkDist >> 3) & 1;
        int flip2 = (walkDist >> 3) & 1;

        if (dir == 1) {
            xt += 2;
        }
        if (dir > 1) {
            flip1 = 0;
            flip2 = (walkDist >> 4) & 1;
            if (dir == 2) {
                flip1 = 1;
            }
            xt += 4 + ((walkDist >> 3) & 1) * 2;
        }

        int xo = x - 8;
        int yo = y - 11;

        if (attackTime > 0 && attackDir == 1) {
            screen.render(xo + 0, yo - 4, 6 + 13 * 32, get(-1, 555, 555, 555), 0);
            screen.render(xo + 8, yo - 4, 6 + 13 * 32, get(-1, 555, 555, 555), 1);
            if (attackItem != null) {
                attackItem.renderIcon(screen, xo + 4, yo - 4);
            }
        }
        int col = get(-1, 100, 220, 532);
        if (hurtTime > 0) {
            col = get(-1, 555, 555, 555);
        }

        int yt = 14;
        if (inventory.active().orElse(null) instanceof Item.FurnitureItem) {
            yt += 2;
        }
        screen.render(xo + 8 * flip1, yo + 0, xt + yt * 32, col, flip1);
        screen.render(xo + 8 - 8 * flip1, yo + 0, xt + 1 + yt * 32, col, flip1);
        screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col, flip2);
        screen.render(xo + 8 - 8 * flip2, yo + 8, xt + 1 + (yt + 1) * 32, col, flip2);

        if (attackTime > 0 && attackDir == 2) {
            screen.render(xo - 4, yo, 7 + 13 * 32, get(-1, 555, 555, 555), 1);
            screen.render(xo - 4, yo + 8, 7 + 13 * 32, get(-1, 555, 555, 555), 3);
            if (attackItem != null) {
                attackItem.renderIcon(screen, xo - 4, yo + 4);
            }
        }
        if (attackTime > 0 && attackDir == 3) {
            screen.render(xo + 8 + 4, yo, 7 + 13 * 32, get(-1, 555, 555, 555), 0);
            screen.render(xo + 8 + 4, yo + 8, 7 + 13 * 32, get(-1, 555, 555, 555), 2);
            if (attackItem != null) {
                attackItem.renderIcon(screen, xo + 8 + 4, yo + 4);
            }
        }
        if (attackTime > 0 && attackDir == 0) {
            screen.render(xo + 0, yo + 8 + 4, 6 + 13 * 32, get(-1, 555, 555, 555), 2);
            screen.render(xo + 8, yo + 8 + 4, 6 + 13 * 32, get(-1, 555, 555, 555), 3);
            if (attackItem != null) {
                attackItem.renderIcon(screen, xo + 4, yo + 8 + 4);
            }
        }

        if (inventory.active().orElse(null) instanceof Item.FurnitureItem furniture) {
            furniture.update(x, yo);
            furniture.render(screen);
        }
    }

    @Override
    public int getLightRadius() {
        return Math.max(BARE_LIGHT, inventory.active().map(Item::getLightRadius).orElse(0));
    }

    @Override
    public void tick() {
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
                if (stamina < MAX_STAMINA) {
                    stamina++;
                }
            }
        }

        Point moved = move();
        state = moved.x == 0 && moved.y == 0 ? State.STAY : State.WALK;

        if (input.isAttacked() && stamina > 0) {
            stamina--;
            staminaRecharge = 0;
            attack();
            state = State.ATCK;
        }
        input.slot().ifPresent(inventory::select);
        if (input.isUsed()) {
            use();
        }
        if (attackTime > 0) {
            attackTime--;
        }
    }

    @Override
    public State state() {
        return state;
    }

    private void attack() {
        walkDist += 8;
        attackDir = dir;
        Optional<Item> active = inventory.active();
        attackItem = active.orElse(null);
        if (active.isPresent() && interactWith(active.get())) {
            return;
        }
        if (active.isEmpty() || active.get().canAttack()) {
            strike();
        }
    }

    private boolean interactWith(Item item) {
        attackTime = 10;
        Area area = area(12);
        if (interact(area)) {
            return true;
        }
        Point tile = target();
        if (!inside(tile)) {
            return false;
        }
        Tile at = room.getTile(tile.x, tile.y);
        return item.interactOn(at, room, tile.x, tile.y, this, attackDir)
            || at.interact(room, tile.x, tile.y, this, item, attackDir);
    }

    private void strike() {
        attackTime = 5;
        hurt(area(20));
        Point tile = target();
        if (inside(tile)) {
            room.getTile(tile.x, tile.y).hurt(room, tile.x, tile.y, this, random.nextInt(3) + 1, attackDir);
        }
    }

    private boolean use() {
        Area area = area(12);
        for (Entity entity : room.getEntities(area.x0(), area.y0(), area.x1(), area.y1())) {
            if (entity != this && entity.use(this, attackDir)) {
                return true;
            }
        }
        Point tile = target();
        return inside(tile) && room.getTile(tile.x, tile.y).use(room, tile.x, tile.y, this, attackDir);
    }

    private boolean interact(Area area) {
        for (Entity entity : room.getEntities(area.x0(), area.y0(), area.x1(), area.y1())) {
            if (entity != this && entity.interact(this, attackItem, attackDir)) {
                return true;
            }
        }
        return false;
    }

    private void hurt(Area area) {
        for (Entity entity : room.getEntities(area.x0(), area.y0(), area.x1(), area.y1())) {
            if (entity != this && reaches(entity)) {
                entity.hurt(this, damage(entity), attackDir);
            }
        }
    }

    private boolean reaches(Entity entity) {
        return inventory.active()
            .map(item -> item.reaches(entity))
            .orElseGet(() -> !(entity instanceof Bug bug) || !bug.flying());
    }

    private int damage(Entity entity) {
        return random.nextInt(3) + 1
            + inventory.active().map(item -> item.getAttackDamageBonus(entity)).orElse(0);
    }

    private boolean inside(Point tile) {
        return tile.x >= 0 && tile.y >= 0 && tile.x < room.width() && tile.y < room.height();
    }

    /**
     * Прямоугольник перед игроком, в который попадает удар или применение предмета.
     * Смещение вверх на два пикселя — рост персонажа: бьёт он не по земле у ног.
     */
    private Area area(int range) {
        int yo = -2;
        return switch (dir) {
            case 0 -> new Area(x - 8, y + 4 + yo, x + 8, y + range + yo);
            case 1 -> new Area(x - 8, y - range + yo, x + 8, y - 4 + yo);
            case 2 -> new Area(x - range, y - 8 + yo, x - 4, y + 8 + yo);
            default -> new Area(x + 4, y - 8 + yo, x + range, y + 8 + yo);
        };
    }

    private Point target() {
        int yo = -2;
        int r = 12;
        return switch (attackDir) {
            case 0 -> new Point(x >> 4, (y + r + yo) >> 4);
            case 1 -> new Point(x >> 4, (y - r + yo) >> 4);
            case 2 -> new Point((x - r) >> 4, (y + yo) >> 4);
            default -> new Point((x + r) >> 4, (y + yo) >> 4);
        };
    }

    private record Area(int x0, int y0, int x1, int y1) {

    }
}
