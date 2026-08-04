package ru.mifi.practice.vol7.singletone;

/**
 * Синглтон с двойной проверкой блокировки. Показан намеренно, хотя это самый дорогой
 * и самый ошибкоопасный из трёх вариантов: без {@code volatile} другой поток увидел бы
 * ссылку на ещё недостроенный объект. Проверки PMD подавлены осознанно — они ругаются
 * ровно на те приёмы, которые здесь и разбираются.
 *
 * <p>В реальном коде брать {@link Singletone2} (ленивый, через класс-держатель)
 * или {@link Singletone3} (на enum).
 */
@SuppressWarnings({"PMD.AvoidUsingVolatile", "PMD.NonThreadSafeSingleton"})
public final class Singletone1 {
    private static volatile Singletone1 instance;

    private Singletone1() {
    }

    public static Singletone1 getInstance() {
        if (instance == null) {
            synchronized (Singletone1.class) {
                if (instance == null) {
                    instance = new Singletone1();
                }
            }
        }
        return instance;
    }
}
