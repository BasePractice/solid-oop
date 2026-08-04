package ru.mifi.practice.vol7.proxy;

import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * Тот же заместитель, но собранный в рантайме через {@link Proxy}. Класс-заместитель
 * здесь не пишется руками — его генерирует JDK по списку интерфейсов, поэтому одним
 * обработчиком накрывается любой интерфейс. На этом держится AOP в Spring и Hibernate.
 *
 * <p>Цена — рефлексия и приведение типа: компилятор уже не проверяет, что подмена
 * возвращает подходящий тип, ошибка вылезет только при вызове. Когда интерфейс один
 * и известен заранее, {@link SimpleProxy} безопаснее.
 */
public final class DynamicProxy {
    private final Simple origin;

    public DynamicProxy(Simple origin) {
        this.origin = Objects.requireNonNull(origin, "Proxy cannot stand for nothing");
    }

    public Simple proxy() {
        return (Simple) Proxy.newProxyInstance(
            Simple.class.getClassLoader(),
            new Class<?>[]{Simple.class},
            (proxy, method, args) -> {
                if ("doSomething".equals(method.getName())) {
                    return 1;
                }
                return method.invoke(origin, args);
            });
    }
}
