package ru.mifi.practice.vol6.menu;

import ru.mifi.practice.vol6.transport.Input;
import ru.mifi.practice.vol6.transport.Output;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Узел дерева меню. Лист выполняет действие, ветка показывает список потомков.
 * Выход не убивает процесс, а гасит флаг в контексте — тогда вложенные циклы
 * разворачиваются сами и ресурсы закрываются штатно.
 */
public final class Menu {
    private final String text;
    private final Menu parent;
    private final Consumer<Context> action;
    private final List<Menu> subMenus;

    private Menu(String text, Menu parent, List<Menu> subMenus, Consumer<Context> action) {
        this.text = text;
        this.parent = parent;
        this.subMenus = subMenus;
        this.action = action;
    }

    private Menu(String text, Menu parent) {
        this(text, parent, new ArrayList<>(), null);
    }

    private Menu(String text, Menu parent, Consumer<Context> action) {
        this(text, parent, new ArrayList<>(), action);
    }

    public static Menu root() {
        return new Menu(null, null);
    }

    public static Context defaultContext() {
        return new Context(Output.standard(), Input.standard());
    }

    private static void printLine(Context context) {
        context.println("---------------------");
    }

    public Menu addSub(String text, Consumer<Context> action) {
        return addSub(new Menu(text, this, action));
    }

    public Menu addSub(Menu menu) {
        subMenus.add(menu);
        return menu;
    }

    public void select(Context context) {
        if (action == null) {
            selectedSubMenu(context);
        } else {
            action.accept(context);
        }
    }

    private void selectedSubMenu(Context context) {
        boolean select = true;
        while (select && context.running()) {
            print(context);
            String in = read(context);
            if (in == null || "exit".equals(in)) {
                context.stop();
                select = false;
            } else if (parent != null && "up".equals(in)) {
                select = false;
            } else {
                choose(context, in);
            }
        }
    }

    private void print(Context context) {
        printLine(context);
        context.print();
        printLine(context);
        for (int i = 0; i < subMenus.size(); i++) {
            context.println("%4d: %s", i + 1, subMenus.get(i).text);
        }
        context.println("exit: Выход");
        if (parent != null) {
            context.println("up  : Наверх");
        }
        printLine(context);
        context.print("> ");
    }

    private String read(Context context) {
        try {
            return context.inputString();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    private void choose(Context context, String in) {
        int index = index(in);
        if (index < 0 || index >= subMenus.size()) {
            context.errorln("Не верный номер: %s", in);
        } else {
            subMenus.get(index).select(context);
        }
    }

    private int index(String in) {
        try {
            return Integer.parseInt(in.trim()) - 1;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
