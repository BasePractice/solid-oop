package ru.mifi.practice.vol7.singletone;

/**
 * Синглтон на enum — самый дешёвый из трёх. Единственность гарантирует сама JVM:
 * константа enum создаётся один раз при загрузке класса, потокобезопасно и без
 * блокировок, а рефлексия и десериализация второй экземпляр создать не могут.
 * Снаружи виден только интерфейс, поэтому enum можно заменить, не трогая клиентов.
 */
public final class Singletone3 {
    private Singletone3() {
    }

    public static Impl getInstance() {
        return Single.INSTANCE;
    }

    public interface Impl {

        String name();
    }

    private enum Single implements Impl {
        INSTANCE
    }
}
