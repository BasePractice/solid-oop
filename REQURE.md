# Разбор практикума pOOP

> **Статус на 04.08.2026.** По этому отчёту проведена правка: закрыты все 🔴, большая
> часть 🟠 и нарушения `RULE.md` — кроме мест, которые намеренно оставлены как
> демонстрация ООП (наследование, геттеры и сеттеры, три варианта синглтона,
> «плохой класс» `vol0/klass/Fly`). Модуль `dormitory` включён в сборку и очищен.
> `mvn -B clean package` на JDK 17 проходит для всех 14 модулей: 0 нарушений
> checkstyle, 0 нарушений PMD. Подавлений проверок осталось 2 из 20, оба с
> объяснением в docblock. Что осталось несделанным — в конце, в разделе
> [«Что не сделано»](#что-не-сделано).

**Как читалось:** статическое чтение всех исходников (10 модулей, ~6.5 тыс. строк Java 17),
всех `pom.xml`, CI, `checkstyle.xml`, `qodana.yaml`, `.gitignore` и README. Сборка не
запускалась — выводы о поведении сделаны из кода. Критерии качества — из `RULE.md`.

**Легенда:**

| Знак | Что значит |
|---|---|
| 🔴 | Код не работает или падает: пример нельзя показать студенту как есть |
| 🟠 | Логическая ошибка: работает, но неправильно, молча |
| 🟡 | Нарушение правил `RULE.md`, стиля или дидактики |

---

## Итог

Проект — это набор демонстраций, ни одна из которых не защищена тестом. В корневом
`pom.xml` подключены JUnit 5 и `junit-platform-suite`, `RULE.md` требует TDD и «Злых
тестов», а каталога `src/test` нет **ни в одном из десяти модулей**. Из-за этого CI,
который гоняет `mvn package`, зелёный всегда и не значит ничего.

Самый большой модуль — `dormitory` (2.6 тыс. строк) — вообще не перечислен в
`<modules>` корневого `pom.xml`: его не компилируют, не проверяет ни checkstyle, ни PMD.
Там же лежат самые грубые дефекты.

По коду найдено 9 мест, где пример гарантированно падает или читает мусор
(`vol1/Institute`, `vol7/FilteredList`, `vol8/Utils` + `readDouble`, `vol3/Record`,
`vol5/Main`, `dormitory/Room`), и около 40 логических ошибок и нарушений правил.
Показательно, что 20 `@SuppressWarnings` в 16 файлах глушат ровно те проверки, которые
указали бы на половину из них — это прямо противоречит требованию `RULE.md`
«доверяй проверяльщикам, а не подавляй их».

Отдельный дидактический пробел: примеры показывают «как надо», но почти нигде нет парного
«как не надо», а восемь модулей из десяти не имеют README — студенту не с чем сверяться.

---

## Часть 1. Чего не хватает

### 1.1. Инфраструктура

| Пробел | Факт | Как исправить |
|---|---|---|
| 🔴 Ни одного теста | `src/test/**` отсутствует во всех 10 модулях; JUnit 5 объявлен в [pom.xml:69-92](pom.xml:69) | Завести `src/test/java` в каждом модуле; начать с модулей, где есть чистая логика без I/O: `vol2` (Grid/State), `vol3` (Chain/Record/Amount), `vol8` (Utils) |
| 🔴 `dormitory` вне сборки | В [pom.xml:28-38](pom.xml:28) перечислены только `vol0`…`vol8` | Добавить `<module>dormitory</module>`, затем чинить всё, что вывалит checkstyle/PMD |
| 🟠 CI ничего не гарантирует | [.github/workflows/maven.yml:29](.github/workflows/maven.yml:29) — `mvn -B package`, тестов нет | Оставить `mvn -B verify`, добавить job с Qodana (файл `qodana.yaml` есть, но не используется), включить `maven-surefire-plugin` с явной версией |
| 🟠 Нет порога покрытия | — | Подключить `jacoco-maven-plugin` с `check`-целью; для учебного проекта разумно начать с 40% по строкам и поднимать |
| 🟡 Проверяльщики подавляются | 20 `@SuppressWarnings` в 16 файлах | См. [часть 3](#часть-3-нарушения-rulemd) — каждый снимается исправлением кода, а не аннотацией |
| 🟡 Артефакты пишутся в корень репозитория | `.gitignore` вынужден содержать `1000000000.csv` и `users.json` | Писать в `java.io.tmpdir` или в путь из аргумента; `RULE.md`: «Создавай объекты-фикстуры… используя временные директории» |
| 🟡 Нет `.mvn/wrapper` | — | Добавить Maven Wrapper, чтобы версия Maven была воспроизводимой |

### 1.2. Дидактика

| Пробел | Почему это важно для курса |
|---|---|
| 🟡 Нет парных контрпримеров | `vol0/klass/Fly` — образцовый «плохой класс» (изменяемые package-private поля, конструктор по умолчанию, `validate()`), но рядом нет `GoodFly` с одним конструктором и `final`-полями. Без сравнения студент не видит, что именно плохо |
| 🟡 README только у 2 модулей из 10 | Есть [vol2/README.adoc](vol2/README.adoc) и [dormitory/README.md](dormitory/README.md). Остальные восемь — без постановки задачи. Корневой [README.md](README.md) — бейдж и ссылка на презентацию |
| 🟡 Часть примеров нечем запустить | `vol1/agent` (нет `main`), `vol7/adapter`, `vol7/factory`, `vol7/proxy`, `vol7/decorator` — нет демонстрации. Студент видит интерфейсы, но не видит поведения |
| 🟡 Диаграмма только у `dormitory` | `model.puml`/`model.png` есть только там. Для `vol1/agent`, `vol3/hasq`, `vol6/menu` схема связей нужнее, чем в игре |
| 🟡 Нет объяснения, почему выбран паттерн | `vol7` показывает пять реализаций синглтона и три варианта билдера без разбора компромиссов. Нужен текст «когда какой и почему» |
| 🟡 Нет примеров на изменяемость/неизменяемость | `RULE.md` требует «создавай только неизменяемые объекты», но модуля, который бы показывал разницу на конкретной задаче, нет — а `vol3/Amount` учит ровно обратному |

---

## Часть 2. Ошибки по модулям

### vol0 — базовые понятия

**🟡 [vol0/Output.java:3](vol0/src/main/java/ru/mifi/practice/vol0/Output.java:3) — пустой интерфейс-мертвец.**
`public interface Output {}` — ни реализаций, ни использований. Удалить.

**🟡 [vol0/klass/Fly.java:11](vol0/src/main/java/ru/mifi/practice/vol0/klass/Fly.java:11) — конструктор по умолчанию создаёт невалидный объект.**
`Fly()` оставляет все поля `null`, а `validate()` потом это проверяет. Это «плохой класс» —
что и задумано, — но без парного «хорошего» урок не читается. Добавить рядом:

```java
public final class ValidFly {
    private final Wing left;
    private final Wing right;
    private final Leg[] legs;
    private final Trunk trunk;

    public ValidFly(Wing left, Wing right, Leg[] legs, Trunk trunk) {
        if (legs.length != 6) {
            throw new IllegalArgumentException("Fly needs exactly six legs, got " + legs.length);
        }
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
        this.legs = legs.clone();
        this.trunk = Objects.requireNonNull(trunk);
    }
}
```

Смысл: невалидное состояние недостижимо, `validate()` не нужен вовсе.

**🟡 [vol0/overriding/Cat.java:18](vol0/src/main/java/ru/mifi/practice/vol0/overriding/Cat.java:18) — перегрузка показана наполовину.**
`cat.mew((Object) "Tommy")` печатает одну строку. Чтобы студент увидел, что перегрузка
разрешается **на этапе компиляции**, нужно рядом показать `cat.mew("Tommy")` — тот же
объект, другой метод:

```java
Object target = "Tommy";
cat.mew("Tommy");   // mew(String)
cat.mew(target);    // mew(Object) — тип переменной, а не объекта
```

---

### vol1 — жизненный цикл и агенты

**🔴 [vol1/agent/model/Institute.java:105,139](vol1/src/main/java/ru/mifi/practice/vol1/agent/model/Institute.java:105) — демо падает на первом же тике.**
`Lector.tick()` и `Student.tick()` бросают `UnsupportedOperationException`, а
`Environment.Default.tick` ([Environment.java:98-101](vol1/src/main/java/ru/mifi/practice/vol1/agent/Environment.java:98))
обходит **всех** агентов. Значит `institute.tick()` падает всегда.

```java
// было
@Override
public void tick(Environment.Snapshot snapshot) {
    throw new UnsupportedOperationException();
}

// стало
@Override
public void tick(Environment.Snapshot snapshot) {
    transport.send(Messages.NOISE);
}
```

Дополнительно: у модуля нет `main` — запустить агентную модель нечем. Нужен
`ru.mifi.practice.vol1.agent.Main` с циклом на N тиков.

**🔴 [vol1/agent/Environment.java:87](vol1/src/main/java/ru/mifi/practice/vol1/agent/Environment.java:87) — `ConcurrentModificationException`.**
`receive` итерирует `listeners.forEach(...)`, а слушатель из
[Institute.java:39](vol1/src/main/java/ru/mifi/practice/vol1/agent/model/Institute.java:39)
вызывает `environment.register(...)` → `subscribe` → `listeners.add`. Модификация
`HashSet` во время обхода.

```java
// было
listeners.forEach(listener -> listener.onEvent(new EventMessage(message, this, source)));

// стало — снимок на время рассылки
for (Listener listener : List.copyOf(listeners)) {
    listener.onEvent(new EventMessage(message, this, source));
}
```

**🔴 [vol1/agent/Transport.java:28-29](vol1/src/main/java/ru/mifi/practice/vol1/agent/Transport.java:28) — отправитель всегда один и тот же.**
Лямбда не захватывает собственный `this`, поэтому `this` в `environment.receive(target, this, message)`
— это экземпляр `Transport.Factory.Default`, а не агент. В итоге проверки
`eventMessage.source() != this` в `Lector`/`Student` истинны всегда, и лектор реагирует
на собственный `NOISE`. Транспорт должен знать своего владельца:

```java
// стало — фабрика получает агента и связывает его с транспортом
interface Factory {
    Transport create(Environment environment, Object owner);

    final class Default implements Factory {
        @Override
        public Transport create(Environment environment, Object owner) {
            return (target, message) -> environment.receive(target, owner, message);
        }
    }
}
```

**🟠 [vol1/Earth.java:63-82](vol1/src/main/java/ru/mifi/practice/vol1/Earth.java:63) — удаление из списка внутри цикла по индексу.**
Обе перегрузки `deleteDied` делают `list.remove(...)` не сдвигая `i` — после удаления
элемент с этим индексом уже другой, и он пропускается. Каждый второй мертвец остаётся
в популяции.

```java
// было
for (int i = 0; i < humans.size(); i++) {
    Human human = humans.get(i);
    if (human.isDied()) {
        humans.remove(human);
    }
}

// стало
humans.removeIf(Human::isDied);
```

Для версии с двумя списками:

```java
relations.removeIf(relation -> {
    if (!relation.isDied()) {
        return false;
    }
    humans.remove(relation.father);
    humans.remove(relation.mother);
    return true;
});
```

Заодно снимаются два `@SuppressWarnings("PMD.ForLoopCanBeForeach")` — PMD ругался по делу.

**🟠 [vol1/Earth.java:33-39](vol1/src/main/java/ru/mifi/practice/vol1/Earth.java:33) — двойное старение.**
`women` и `men` кладутся и в `humans`, и в `Relation`. Затем в цикле идёт
`humans.forEach(Human::tick)` **и** `relations.forEach(Relation::tick)`, а
`Relation.tick()` ([Relation.java:38-39](vol1/src/main/java/ru/mifi/practice/vol1/Relation.java:38))
сам вызывает `mother.tick()`/`father.tick()`. Стартовая пара стареет вдвое быстрее
остальных. Владение временем должно быть в одном месте:

```java
// стало — Relation не тикает людей, только себя
public void tick() {
    if (isReproductive() && lastHuman <= 0) {
        mother.mix(father).ifPresent(child -> {
            humans.add(child);
            lastHuman = 6 * 12;
        });
    } else {
        --lastHuman;
    }
}
```

**🟠 [vol1/Relation.java:10,40,45](vol1/src/main/java/ru/mifi/practice/vol1/Relation.java:10) — первый ребёнок невозможен.**
`lastHuman` стартует с `0`, условие входа — `lastHuman < 0`. На первом тике `0 < 0` ложно,
и только `--lastHuman` в конце уводит счётчик в минус. Дальше счётчик декрементируется
безусловно и уходит в глубокий минус. Правильное условие — `lastHuman <= 0`, декремент —
только в `else` (см. фрагмент выше).

**🟠 [vol1/Human.java:22](vol1/src/main/java/ru/mifi/practice/vol1/Human.java:22) — NPE в делегирующем конструкторе.**
`this(mother, father, father.xyChromosome, mother.mitochondria, ageMonth)` разыменовывает
оба аргумента без проверки. `RULE.md`: «Отвергай возврат `null` и `null`-аргументы» и
«Предпочитай немедленный отказ молчаливому»:

```java
protected Human(Women mother, Men father, int ageMonth) {
    this(
        Objects.requireNonNull(mother, "Human cannot be born without mother"),
        Objects.requireNonNull(father, "Human cannot be born without father"),
        father.xyChromosome, mother.mitochondria, ageMonth
    );
}
```

**🟡 Два источника случайности.**
[Earth.java:9,16](vol1/src/main/java/ru/mifi/practice/vol1/Earth.java:9) — `new Random()`
с сидом от текущего времени; [Human.java:64](vol1/src/main/java/ru/mifi/practice/vol1/Human.java:64)
— `Math.random()`. Прогон невоспроизводим, тест написать нельзя. Передавать один `Random`
внутрь `Human` через конструктор — тогда тест с фиксированным сидом становится возможным
(`RULE.md`: «Тестируй конкурентность, повторяя нестабильные блоки», «используй фейки и стабы»).

**🟡 [vol1/agent/model/Institute.java:63-72](vol1/src/main/java/ru/mifi/practice/vol1/agent/model/Institute.java:63) — рефлексия.**
`klass().getConstructor(...).newInstance(...)` — прямой запрет `RULE.md` («Пропускай
проверку типов, приведение и рефлексию»). Заменяется функцией:

```java
private record Human(String name, BiFunction<String, Transport, Agent> maker)
    implements Agent.Factory {
    public Agent create(Transport transport) {
        return maker.apply(name, transport);
    }
}
// new Human("Петрович", Lector::new)
```

**🟡 Три пазла без контекста.**
`//TODO:` в [Institute.java:127,152,157](vol1/src/main/java/ru/mifi/practice/vol1/agent/model/Institute.java:127)
— пустые. `RULE.md` (Puzzle Driven Development) требует: что сделать, почему не сделано
сейчас, ссылка на issue.

---

### vol2 — умный муравей

**🔴 [vol2/clever-ant/.../Grid.java:87-93](vol2/clever-ant/src/main/java/ru/mifi/practice/vol2/ant/Grid.java:87) — `copy()` путает измерения.**
Массив объявлен `new Element[width][height]` ([Grid.java:57](vol2/clever-ant/src/main/java/ru/mifi/practice/vol2/ant/Grid.java:57)),
то есть первый индекс — `width`. А цикл идёт `x < height` и копирует `width` элементов.
На квадратной карте 32×32 (текущий `basic.txt`) это незаметно, на любой другой —
`ArrayIndexOutOfBoundsException` либо потеря части карты.

```java
// было
for (int x = 0; x < height; x++) {
    System.arraycopy(map[x], 0, n.map[x], 0, width);
}

// стало
for (int x = 0; x < width; x++) {
    System.arraycopy(map[x], 0, n.map[x], 0, height);
}
n.foods = foods;   // см. следующий пункт
```

**🟠 [vol2/clever-ant/.../Grid.java:98-102](vol2/clever-ant/src/main/java/ru/mifi/practice/vol2/ant/Grid.java:98) — верхняя строка недостижима.**
Ветка `UP`: `y--; if (y <= 0) { y = height - 1; }`. При `y == 1` получается `y == 0`,
условие `0 <= 0` срабатывает, и муравей телепортируется в самый низ. Строка `0` не
достигается движением вверх никогда. Должно быть `y < 0`:

```java
case UP: {
    --y;
    if (y < 0) {
        y = height - 1;
    }
    break;
}
```

**🟠 [vol2/clever-ant/.../Grid.java:54-64](vol2/clever-ant/src/main/java/ru/mifi/practice/vol2/ant/Grid.java:54) — копия теряет счётчик еды.**
`ToroidGrid(width, height)` создаёт сетку с `foods == 0`, а `copy()` его не переносит.
Сейчас это не проявляется только потому, что
[State.java:25](vol2/clever-ant/src/main/java/ru/mifi/practice/vol2/ant/State.java:25)
берёт `map.foods()` у **оригинала**, а не у копии. Любой, кто вызовет `copy().foods()`,
получит ноль. Исправление — в фрагменте выше.

**🟡 [vol2/clever-ant/.../Engine.java:33,40](vol2/clever-ant/src/main/java/ru/mifi/practice/vol2/ant/Engine.java:33) — магические константы.**
Путь `/basic.txt` и лимит `500` шагов зашиты в код. Вынести в параметры `all(Path map, int limit)` —
и тогда появится возможность прогонять несколько карт в тесте.

**🟡 Split package между модулями.**
`clever-ant` и `ant-pastor` объявляют классы в одном пакете `ru.mifi.practice.vol2.ant`,
но лежат в разных JAR. Это ломает JPMS и мешает объяснить, зачем нужен SPI. Реализации
должны жить в своём пакете, например `ru.mifi.practice.vol2.ant.pastor`.

**🟡 [vol2/ant-pastor/.../AntBasic.java:50-53](vol2/ant-pastor/src/main/java/ru/mifi/practice/vol2/ant/AntBasic.java:50) — состояние `S5` игнорирует сенсор.**
Идёт вперёд вслепую. Как «наивная стратегия» это законно, но нужен комментарий-docblock,
что это заведомо неоптимально, и тест, фиксирующий её результат — тогда следующая
реализация будет с чем сравнивать.

---

### vol3 — банк, хеш-цепочка, HTTP

#### hasq (цепочка)

**🔴 [vol3/hasq/Record.java:110](vol3/src/main/java/ru/mifi/practice/val3/hasq/Record.java:110) — NPE на предпоследней записи.**
`next.next.id` и `next.next.key.value()` разыменовываются без проверки. Как только
`validate()` доходит до записи, у которой `next != null`, но `next.next == null`, — падение.

```java
// стало
if (owner == null || next.next == null) {
    return Result.ok(ValidateType.SUCCESS);
}
hash = token.hash(String.valueOf(next.next.id), token.value(),
    next.next.key.value(), next.generator.value());
```

**🔴 [vol3/hasq/Record.java:54-56](vol3/src/main/java/ru/mifi/practice/val3/hasq/Record.java:54) — гарантированный выход за границы.**
Ветка `else` срабатывает при `parts.length != 6` и тут же обращается к `parts[5]`.
При `parts.length < 6` — `ArrayIndexOutOfBoundsException`.

```java
// стало — быстрый отказ с внятным сообщением
if (parts.length != 6) {
    throw new IllegalArgumentException(
        "Record line needs six fields, got " + parts.length);
}
```

**🟠 [vol3/hasq/Record.java:51-52](vol3/src/main/java/ru/mifi/practice/val3/hasq/Record.java:51) — сравниваем обрезанное, храним необрезанное.**
Локальные `generator`/`owner` получают `.trim()`, а в конструкторы передаются сырые
`parts[3]`/`parts[4]`. После чтения из файла хеши не сойдутся с вычисленными:

```java
// было
generator.isEmpty() ? null : new Generator(id, key, parts[3]),
owner.isEmpty() ? null : new Owner(id, parts[4])

// стало
generator.isEmpty() ? null : new Generator(id, key, generator),
owner.isEmpty() ? null : new Owner(id, owner)
```

**🟠 [vol3/hasq/Hash.java:28](vol3/src/main/java/ru/mifi/practice/val3/hasq/Hash.java:28) — хеш зависит от локали JVM.**
`object.toString().getBytes()` без кодировки берёт платформенную. Цепочка, записанная на
Windows-1251, не проверится на UTF-8-машине:

```java
digest.update(object.toString().getBytes(StandardCharsets.UTF_8));
```

**🟡 [vol3/hasq/Hash.java:12](vol3/src/main/java/ru/mifi/practice/val3/hasq/Hash.java:12) — `DEFAULT = SHA1`.**
Ставить сломанный алгоритм умолчанием в учебном примере — плохо. `DEFAULT = SHA256`.
MD5/SHA1 оставить, но с docblock'ом «только для демонстрации, не использовать».

**🟡 [vol3/hasq/Hash.java:32-34](vol3/src/main/java/ru/mifi/practice/val3/hasq/Hash.java:32) — `catch (Exception) → RuntimeException`.**
Ловится вообще всё. Ловить нужно `NoSuchAlgorithmException` и бросать `IllegalStateException`
с указанием алгоритма: `RULE.md` — «Сообщения об ошибках — одно предложение с контекстом».

**🟡 [vol3/hasq/Chain.java:88,104](vol3/src/main/java/ru/mifi/practice/val3/hasq/Chain.java:88) — `Key implements Hash`.**
Ключ — не хеш-функция; `Generator` — тоже. Это делается ради удобного делегирования
`hash(...)`, но ломает подстановку Лисков и раздувает интерфейс. Правильно — хранить
`Hash` полем и не выставлять его наружу.

**🟡 [vol3/hasq/ChainFile.java:10](vol3/src/main/java/ru/mifi/practice/val3/hasq/ChainFile.java:10) — утилитарный класс через `abstract`.**
`public abstract class` с двумя `static` методами. `RULE.md`: «Отвергай имена на "-ер" и
утилитарные классы». Превратить в объект: `new ChainFile(path).write(root)`.

**🟡 [vol3/hasq/Main.java:13](vol3/src/main/java/ru/mifi/practice/val3/hasq/Main.java:13) — пишет `1000000000.csv` в корень репозитория.**
Отсюда и строка в `.gitignore`. Писать в `Files.createTempDirectory(...)`.

**🟡 [vol3/hasq/Result.java:6,38](vol3/src/main/java/ru/mifi/practice/val3/hasq/Result.java:6) — `Error` создаётся всегда.**
`Result.ok(r)` конструирует `new Error<>(null, null)` — «успешный» результат несёт пустую
ошибку, и `error()` никогда не `null`. Разделить на два подтипа `Ok`/`Failure` sealed-интерфейса —
тогда `sealed` начнёт работать по назначению.

#### bank

**🟠 [vol3/bank/Card.java:53-58, 96-101](vol3/src/main/java/ru/mifi/practice/val3/bank/Card.java:53) — знак суммы теряется.**
При `amount.value() < 0` вызывается `this.amount.minus(amount)`, то есть вычитается
отрицательное число — баланс **растёт**. Списать деньги этим методом невозможно.

```java
// было
if (amount.value() >= 0) {
    this.amount.plus(amount);
} else {
    this.amount.minus(amount);
}

// стало — знак уже в самой сумме
this.amount.plus(amount);
```

**🟠 [vol3/bank/Bank.java:53](vol3/src/main/java/ru/mifi/practice/val3/bank/Bank.java:53) — дубликаты держателей не ловятся.**
`list.contains(holder)` сравнивает по ссылке: у `Holder.Default` нет `equals`/`hashCode`
([Holder.java:5](vol3/src/main/java/ru/mifi/practice/val3/bank/Holder.java:5)). Только что
созданный `new Holder.Default(...)` никогда не равен уже лежащему, и `IllegalStateException`
не бросится ни разу. Та же беда у `Map<Holder, List<Card>> cards`. Проще всего — сделать
`Holder.Default` записью:

```java
record Default(String firstName, String lastName, String middleName) implements Holder {
    @Override
    public Index index() {
        return Index.createSearch(firstName, lastName, middleName);
    }
}
```

**🟠 [vol3/bank/Bank.java:9-10,45](vol3/src/main/java/ru/mifi/practice/val3/bank/Bank.java:9) — изменяемое состояние в константах интерфейса и утечка `this`.**
`Up CENTROBANK` и `Bank SBER` — статические поля интерфейса с изменяемыми `HashMap` внутри.
Конструктор `Sber` кладёт `this` в `up.banks` до завершения инициализации
([Bank.java:45](vol3/src/main/java/ru/mifi/practice/val3/bank/Bank.java:45)) — недостроенный
объект уже виден снаружи. `RULE.md`: «Убирай… публичные статические литералы»,
«Допускай только присваивания в одном основном конструкторе». Регистрацию вынести
наружу: `up.register(new Sber(up))`.

**🟠 [vol3/bank/Amount.java:53-62](vol3/src/main/java/ru/mifi/practice/val3/bank/Amount.java:53) — сумма изменяема.**
`minus`/`plus` мутируют `value`. `RULE.md`: «Создавай только неизменяемые объекты».
Операции должны возвращать новую сумму:

```java
Amount plus(Amount amount);   // вместо void
Amount minus(Amount amount);
```

**🟡 [vol3/bank/Card.java:18,63](vol3/src/main/java/ru/mifi/practice/val3/bank/Card.java:18) — мёртвое поле `bank`.**
Присваивается в обоих конструкторах и не читается нигде. Удалить (в `Amount.create`
передаётся тот же `bank` как конвертер — этого достаточно).

**🟡 [vol3/bank/Controller.java:6-12](vol3/src/main/java/ru/mifi/practice/val3/bank/Controller.java:6) — заглушка.**
Печатает «Выберите действие: », настраивает разделитель и закрывает `Scanner`. Ничего не
читает. Либо доделать до рабочего меню (образец есть в `vol6/menu`), либо удалить —
недоделанный пример вреднее отсутствующего.

**🟡 [vol3/bank/Index.java:23-35](vol3/src/main/java/ru/mifi/practice/val3/bank/Index.java:23) — несогласованные `hashCode`/`equals`.**
`hashCode` через `Objects.hash(values)` (поэлементно, неглубоко), `equals` через
`Objects.deepEquals` (глубоко). Для вложенных массивов контракт нарушится.
Использовать `Arrays.deepHashCode(values)`.

#### cont (HTTP)

**🟠 [vol3/cont/http/Jdk.java:33-36](vol3/src/main/java/ru/mifi/practice/val3/cont/http/Jdk.java:33) и [Ok.java:32-35](vol3/src/main/java/ru/mifi/practice/val3/cont/http/Ok.java:32) — ошибки глушатся.**
`ex.printStackTrace()`, `System.out.println(response.body())` и `Optional.empty()` в конце.
Вызывающий не отличает «сервис вернул 404» от «сеть недоступна». `RULE.md`: «Предпочитай
немедленный отказ молчаливому». Плюс нет таймаутов — «Ограничивай каждое ожидание таймаутами»:

```java
public Jdk(Deserializer deserializer) {
    this.client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();
    this.deserializer = deserializer;
}

@Override
public <T> Optional<T> get(String url, Class<T> clazz) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(Duration.ofSeconds(10))
        .build();
    HttpResponse<String> response = client.send(request,
        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    if (response.statusCode() != 200) {
        throw new IOException("Service " + url + " answered " + response.statusCode());
    }
    return Optional.of(deserializer.deserialize(response.body(), clazz));
}
```

**🟠 [vol3/cont/sevices/OpenMeteo.java:27-30](vol3/src/main/java/ru/mifi/practice/val3/cont/sevices/OpenMeteo.java:27) — NPE при неполном ответе.**
`result.current` не проверяется; если API вернёт объект без `current` — падение внутри
`map`. Поля объявлены `String`, хотя API отдаёт числа — держится на снисходительности Gson.
Объявить `float`/`double` и проверить `current` на `null`.

**🟡 [vol3/cont/sevices/WhoIs.java:9](vol3/src/main/java/ru/mifi/practice/val3/cont/sevices/WhoIs.java:9) — открытый HTTP.**
`http://ipwho.is` → `https://ipwho.is`.

**🟡 [vol3/cont/business/DefaultLogic.java:20-45](vol3/src/main/java/ru/mifi/practice/val3/cont/business/DefaultLogic.java:20) — пример противоречит следующему уроку.**
Вложенный `Default` вручную зашивает `Jdk` + `GsonJson` — ровно ту проводку, которую
`vol4` затем делает через Dagger. Читая подряд, студент видит две несовместимые версии
одной идеи. Оставить только `record DefaultLogic(Location, Weather)` и собирать зависимости
снаружи (`vol3/cont/Main` или `vol4`).

**🟡 Опечатка в имени пакета: `sevices` → `services`.**
[vol3/src/main/java/ru/mifi/practice/val3/cont/sevices](vol3/src/main/java/ru/mifi/practice/val3/cont/sevices).
Заодно `val3`/`val4`/`val5` против `vol3`… в именах каталогов — смесь `vol`/`val` по всему
проекту. Привести к одному.

---

### vol4 — внедрение зависимостей (Dagger)

**🟡 [vol4/.../HttpOkModule.java:17](vol4/src/main/java/ru/mifi/practice/val4/poly/module/HttpOkModule.java:17), [HttpJdkModule.java:17](vol4/src/main/java/ru/mifi/practice/val4/poly/module/HttpJdkModule.java:17), [LogicDefaultModule.java:17](vol4/src/main/java/ru/mifi/practice/val4/poly/module/LogicDefaultModule.java:17) — `@Inject` на `@Provides`.**
Аннотация здесь ничего не значит: Dagger и так внедряет параметры `@Provides`-метода.
Студент решит, что она обязательна. Удалить.

**🟡 [vol4/.../BusinessComponent.java:13](vol4/src/main/java/ru/mifi/practice/val4/poly/BusinessComponent.java:13) — `HttpJdkModule` нигде не подключён.**
Модуль-альтернатива написан и мёртв. А ведь ровно в этом суть DI — подменить реализацию,
не трогая бизнес-логику. Показать это явно:

```java
@Singleton
@Component(modules = {ServiceDefaultModule.class, HttpJdkModule.class,
                      JsonGsonModule.class, LogicDefaultModule.class})
public interface JdkBusinessComponent extends BusinessComponent {
}
```

и в `Main` выбирать компонент по аргументу — тогда идея становится осязаемой.

**🟡 [vol4/.../Main.java:5](vol4/src/main/java/ru/mifi/practice/val4/poly/Main.java:5) — `DaggerBusinessComponent` появляется из ниоткуда.**
Класс генерируется annotation processor'ом ([vol4/pom.xml:38-45](vol4/pom.xml:38)). Без
README студент увидит «красный» класс в IDE и решит, что пример сломан. Нужен
`vol4/README.adoc` с объяснением и командой `mvn -pl vol4 compile`.

---

### vol5 — Spring Boot

**🔴 [vol5/.../Main.java:12,19-25](vol5/src/main/java/ru/mifi/practice/val5/Main.java:12) — слушатель не вызывается никогда.**
`@SpringBootApplication` стоит на `public abstract class`. Абстрактный класс не может
стать бином, значит и приватный `@EventListener(ApplicationReadyEvent.class)` не будет
вызван — ссылка на Swagger UI в лог не попадёт.

```java
// было
@SpringBootApplication
public abstract class Main {

// стало
@SpringBootApplication
public class Main {
    @EventListener(ApplicationReadyEvent.class)
    public void ready(ApplicationReadyEvent event) {
        log.info("http://127.0.0.1:{}/swagger-ui/index.html",
            event.getApplicationContext().getEnvironment().getProperty("server.port"));
    }
}
```

Заодно снимается `@SuppressWarnings({"PMD.UnusedPrivateMethod", "PMD.CloseResource"})` —
PMD жаловался справедливо.

**🟠 [vol5/.../mapper/VersionMapper.java](vol5/src/main/java/ru/mifi/practice/val5/mapper/VersionMapper.java) и [dto/VersionDto.java](vol5/src/main/java/ru/mifi/practice/val5/mapper/dto/VersionDto.java) — урок про MapStruct не доведён.**
Маппер объявлен, DTO объявлен, а контроллер
([VersionController.java:28](vol5/src/main/java/ru/mifi/practice/val5/controller/VersionController.java:28))
отдаёт `VersionModel` напрямую. Маппер не вызывается нигде — отсюда и
`@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})` на DTO. Довести:

```java
@GetMapping
public ResponseEntity<VersionDto> version() {
    return ResponseEntity.ok(VersionMapper.DEFAULT.toVersionDto(VersionModel.current()));
}
```

**🟠 [vol5/.../dto/VersionDto.java:11-17](vol5/src/main/java/ru/mifi/practice/val5/mapper/dto/VersionDto.java:11) — только сеттеры.**
Ни одного геттера. Даже если маппер подключить, Jackson сериализует пустой объект `{}`.
`RULE.md` вообще запрещает и сеттеры, и геттеры — здесь просится `record`:

```java
public record VersionDto(String version, LocalDateTime releaseDate) {
}
```

MapStruct 1.6 умеет собирать records через конструктор.

**🟡 [vol5/.../model/VersionModel.java:13,16](vol5/src/main/java/ru/mifi/practice/val5/model/VersionModel.java:13) — валидация декоративная.**
`@NotBlank`/`@NotNull` стоят, но `spring-boot-starter-validation` не подключён
([vol5/pom.xml](vol5/pom.xml)), а `@Valid` нигде не используется. Аннотации не делают
ничего — студент решит, что валидация «включается сама».

**🟡 [vol5/.../controller/VersionController.java:15](vol5/src/main/java/ru/mifi/practice/val5/controller/VersionController.java:15) — `@Slf4j` без единого лога.**
Убрать аннотацию либо добавить лог запроса.

**🟡 Ни одного теста веб-слоя.**
Это модуль, где тест пишется в три строки и приносит максимум пользы:

```java
@WebMvcTest(VersionController.class)
final class VersionControllerTest {
    @Test
    void doesNotAnswerWithoutVersionField(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/version")).andExpect(jsonPath("$.version").value("v1"));
    }
}
```

---

### vol6 — консольное приложение с аутентификацией

**🔴 [vol6/.../storege/FileStorage.java:33-34](vol6/src/main/java/ru/mifi/practice/vol6/storege/FileStorage.java:33) — NPE на пустом файле.**
`gson.fromJson(...)` возвращает `null`, если файл пуст или содержит `null`, и это `null`
уходит в `repository.addAll(list)` → `items.forEach` → NPE. Плюс `JsonSyntaxException`
не ловится вовсе, а `IOException` глушится пустым `catch` с
`@SuppressWarnings("PMD.EmptyCatchBlock")`.

```java
// стало
@Override
public <T> RepositoryMutant<T, String> read(RepositoryMutant<T, String> repository) {
    if (!Files.exists(users)) {
        return repository;
    }
    try {
        List<T> list = gson.fromJson(Files.readString(users, StandardCharsets.UTF_8),
            repository.listType());
        repository.addAll(Objects.requireNonNullElse(list, List.of()));
    } catch (IOException | JsonSyntaxException e) {
        throw new IllegalStateException("Cannot read users from " + users, e);
    }
    return repository;
}
```

**🟠 [vol6/.../menu/Context.java:69](vol6/src/main/java/ru/mifi/practice/vol6/menu/Context.java:69) — повторный вход молча игнорируется.**
`session.compareAndExchange(null, session)` записывает только если там было `null`.
Если пользователь A уже вошёл, вход пользователя B ничего не меняет:
`AuthenticationMenu.accept` ([AuthenticationMenu.java:20](vol6/src/main/java/ru/mifi/practice/vol6/menu/AuthenticationMenu.java:20))
вызывает `putSession` при успехе, но чистит сессию только при **неудаче**. Пользователь
видит «вход выполнен», а работает под чужим аккаунтом.

```java
// было
public void putSession(Authentication.Session session) {
    this.session.compareAndExchange(null, session);
}

// стало
public void putSession(Authentication.Session session) {
    this.session.set(session);
}
```

**🟠 [vol6/.../menu/Menu.java:79](vol6/src/main/java/ru/mifi/practice/vol6/menu/Menu.java:79) — `System.exit(0)` посреди меню.**
`Main` оборачивает `Context` в try-with-resources
([Main.java:20-22](vol6/src/main/java/ru/mifi/practice/vol6/Main.java:20)), но `System.exit`
убивает JVM до `close()`. Спасает только shutdown hook — то есть два механизма сохранения
делают одно и то же (см. следующий пункт). Выходить надо возвратом из цикла:

```java
if ("exit".equals(in)) {
    context.exit();
    return;
}
```

**🟠 [vol6/.../Main.java:32-36](vol6/src/main/java/ru/mifi/practice/vol6/Main.java:32) — двойная запись хранилища.**
Регистрируется shutdown hook `storage.write(repository)` **и** возвращается `onExit` с тем
же вызовом. При нормальном выходе файл пишется дважды. Оставить что-то одно — hook надёжнее.

**🟠 [vol6/.../menu/DeleteRegistrationMenu.java:19](vol6/src/main/java/ru/mifi/practice/vol6/menu/DeleteRegistrationMenu.java:19) — удаление не сохраняется.**
`repository.delete(...)` меняет только `HashMap` в памяти. Запись случится при выходе через
`exit`, но если процесс убить — удалённый пользователь вернётся. Либо писать сразу, либо
явно задокументировать модель «сохранение при выходе».

**🟠 [vol6/.../security/Security.java:15,22-24](vol6/src/main/java/ru/mifi/practice/vol6/security/Security.java:15) — SHA-256 без соли.**
`Hashing.sha256().hashString(password, UTF_8)` — одна итерация, без соли. Радужная таблица
вскрывает такое мгновенно, и учебный пример закрепляет неверную привычку. Нужен
`PBKDF2WithHmacSHA256` (в JDK) или BCrypt/Argon2, соль на пользователя, счётчик итераций
в модели `User`.

**🟡 [vol6/.../transport/Input.java:7,17,22](vol6/src/main/java/ru/mifi/practice/vol6/transport/Input.java:7) — синглтон в интерфейсе и закрытие `System.in`.**
`Input DEFAULT = new Standard()` — публичный статический литерал (запрет `RULE.md`), а
`close()` закрывает `System.in` навсегда. То же у
[Output.java:6](vol6/src/main/java/ru/mifi/practice/vol6/transport/Output.java:6).
Создавать `Input`/`Output` в `Main` и передавать в `Context` — заодно появится возможность
подставить фейк в тест.

**🟡 [vol6/.../menu/Context.java:10](vol6/src/main/java/ru/mifi/practice/vol6/menu/Context.java:10) — `Context implements Output, Input`.**
Контекст сессии, ввод и вывод — три роли в одном классе (нарушение разделения интерфейсов
и SRP). Держать `Input`/`Output` полями, наружу отдавать только то, что нужно меню.

**🟡 [vol6/.../repository/UserRepositoryInMemory.java:14](vol6/src/main/java/ru/mifi/practice/vol6/repository/UserRepositoryInMemory.java:14) — `TypeToken`.**
Guava `TypeToken` — рефлексия, запрещённая `RULE.md`. И `Repository.listType()`
([Repository.java:13](vol6/src/main/java/ru/mifi/practice/vol6/repository/Repository.java:13))
протаскивает деталь сериализации в интерфейс хранилища. Сериализацию должен знать
`FileStorage`, а не репозиторий.

**🟡 [vol6/.../storege/Storage.java:10](vol6/src/main/java/ru/mifi/practice/vol6/storege/Storage.java:10) — метод мутирует аргумент и возвращает его же.**
`read(repository)` возвращает тот самый объект, который получил. Либо `void read(...)`,
либо возвращать новый репозиторий. `RULE.md`: «разделяй команды и запросы».

**🟡 [vol6/.../menu/AbstractMenu.java:5-6](vol6/src/main/java/ru/mifi/practice/vol6/menu/AbstractMenu.java:5) — наследование вместо композиции.**
`abstract class` с `protected` полем. `RULE.md`: «Компонуй объекты вместо наследования
реализации». Достаточно интерфейса `Menu.Item { String name(); void accept(Context); }`.

**🟡 Опечатка в имени пакета: `storege` → `storage`.**

---

### vol7 — паттерны проектирования

**🔴 [vol7/.../decorator/FilteredList.java:24-30](vol7/src/main/java/ru/mifi/practice/vol7/decorator/FilteredList.java:24) — `add` падает всегда.**
`throw` стоит **после** `if`, а не в `else`. Элемент, прошедший фильтр, добавляется — и
тут же летит `UnsupportedOperationException`. Не прошедший — просто исключение. Метод
неработоспособен при любом входе.

```java
// было
@Override
public void add(int index, T element) {
    if (predicate.test(element)) {
        decorated.add(index, element);
    }
    throw new UnsupportedOperationException();
}

// стало
@Override
public void add(int index, T element) {
    if (!predicate.test(element)) {
        throw new IllegalArgumentException("Element " + element + " does not match filter");
    }
    decorated.add(index, element);
    modCount++;
}
```

Отдельно: `modCount` из `AbstractList` не инкрементируется — итератор не заметит изменений
и не бросит `ConcurrentModificationException`, когда должен. И класс не `final`
([FilteredList.java:7](vol7/src/main/java/ru/mifi/practice/vol7/decorator/FilteredList.java:7)),
хотя `RULE.md` требует «делай каждый класс `final`». Плюс `implements List<T>` избыточен —
`AbstractList` уже его реализует.

**🔴 [vol7/.../factory/impl/ConcreteEmployeeFactory.java:10,15](vol7/src/main/java/ru/mifi/practice/vol7/factory/impl/ConcreteEmployeeFactory.java:10) — фабрика возвращает `null`.**
Оба метода — `return null;`. `RULE.md`: «Отвергай возврат `null`». Паттерн «фабрика»
показан пустой оболочкой: нет ни `Employee`-реализации, ни `Director`-реализации, ни `main`.

```java
public final class ConcreteEmployeeFactory implements EmployeeFactory {
    @Override
    public Employee createEmployee(String name) {
        return new Staff(name);
    }

    @Override
    public Director createDirector(String name) {
        return new Chief(name);
    }

    private record Staff(String name) implements Employee { }

    private record Chief(String name) implements Director { }
}
```

Плюс интерфейсы `Employee` и `Director`
([Employee.java:3](vol7/src/main/java/ru/mifi/practice/vol7/factory/Employee.java:3))
пустые — у сотрудника должно быть хотя бы `name()`, иначе паттерн не на чем показать.

**🟠 [vol7/.../Main.java:13](vol7/src/main/java/ru/mifi/practice/vol7/Main.java:13) — вызов `finalize()`.**
`finalize()` объявлен deprecated в Java 9 и **удалён** в Java 18 — пример перестанет
компилироваться при обновлении JDK. Он ещё и обёрнут в
`@SuppressWarnings("PMD.AvoidCatchingThrowable")`. Если цель — показать освобождение
ресурсов, современный ответ — `AutoCloseable` и `java.lang.ref.Cleaner`:

```java
public final class Destructor implements AutoCloseable {
    private static final Cleaner CLEANER = Cleaner.create();
    private final Cleaner.Cleanable cleanable;

    public Destructor(Runnable release) {
        this.cleanable = CLEANER.register(this, release);
    }

    @Override
    public void close() {
        cleanable.clean();
    }
}
```

**🟠 [vol7/.../observer/User.java:3](vol7/src/main/java/ru/mifi/practice/vol7/observer/User.java:3) и [PrintObserver.java:3-4](vol7/src/main/java/ru/mifi/practice/vol7/observer/PrintObserver.java:3) — `java.util.Observable` deprecated с Java 9.**
Наследование от `Observable` ещё и съедает единственную возможность наследования.
Показать собственный слушатель или `PropertyChangeSupport`:

```java
public final class User {
    private final String name;
    private final List<Consumer<String>> listeners = new ArrayList<>();

    public void subscribe(Consumer<String> listener) {
        listeners.add(listener);
    }

    public void password(String password) {
        listeners.forEach(listener -> listener.accept(password));
    }
}
```

**🟡 [vol7/.../builder/User.java:52-60](vol7/src/main/java/ru/mifi/practice/vol7/builder/User.java:52) и [User2.java:11-19](vol7/src/main/java/ru/mifi/practice/vol7/builder/User2.java:11) — демо ничего не показывает.**
Оба `main` собирают объект, присваивают переменной и завершаются. Нет ни вывода, ни
`toString()` — на экране пусто. Добавить печать и показать, что `toBuilder()` даёт
**новый** объект, а исходный не изменился. `User2` ещё и не `final`.

**🟡 [vol7/.../adapter/Int1.java](vol7/src/main/java/ru/mifi/practice/vol7/adapter/Int1.java) / [Int2.java](vol7/src/main/java/ru/mifi/practice/vol7/adapter/Int2.java) — адаптер ничего не адаптирует.**
Оба интерфейса объявляют одинаковый `String getName()`, а `Adapter` просто делегирует.
Смысл паттерна — примирить **несовместимые** сигнатуры. Нужны разные формы, например
`String getName()` против `Optional<String> title()`, и осмысленные имена вместо `Int1`/`Int2`.

**🟡 [vol7/.../singletone/Singletone3.java:3-16](vol7/src/main/java/ru/mifi/practice/vol7/singletone/Singletone3.java:3) — enum-синглтон завёрнут в лишний класс.**
`@SuppressWarnings("PMD.UseUtilityClass")` + пустой маркер `Impl`. Enum-синглтон
самодостаточен; обёртка только мешает. Плюс три варианта синглтона даны без разбора,
какой когда уместен, — а именно это и есть содержание урока.

**🟡 [vol7/.../proxy/SimpleProxy.java:11-19](vol7/src/main/java/ru/mifi/practice/vol7/proxy/SimpleProxy.java:11) — динамический прокси без демонстрации.**
`Proxy.newProxyInstance` + приведение к `Simple` — рефлексия и каст, оба запрещены `RULE.md`.
Для учебного примера достаточно статического прокси (`class LoggingSimple implements Simple`),
а динамический показать отдельно с оговоркой, чем он платит.

**🟡 [vol7/pom.xml:20](vol7/pom.xml:20) — lombok в `compile`-scope.**
Попадает в runtime-classpath. Должен быть `<scope>provided</scope>`, как в
[vol5/pom.xml](vol5/pom.xml).

**🟡 Опечатка в имени пакета: `singletone` → `singleton`.**

---

### vol8 — бинарная сериализация и потоки

**🔴 [vol8/.../streaming/Utils.java:14-23](vol8/src/main/java/ru/mifi/practice/vol8/streaming/Utils.java:14) — знаковое расширение портит любое число.**
`byte` в Java знаковый. `int ch0 = buffer[3 + offset];` для байта `0x80` даёт `-128`,
а не `128`. Любое значение, где хоть один байт ≥ `0x80`, читается неверно. `readInt32`
используется в `BsonInputStream.readInt32()` → `readString()` → разбор длины строк и
документов BSON. То же в `readInt16`.

```java
// было
int ch0 = buffer[3 + offset];
int ch1 = buffer[2 + offset];
int ch2 = buffer[1 + offset];
int ch3 = buffer[0 + offset];
return (ch0 << 24) + (ch1 << 16) + (ch2 << 8) + ch3;

// стало
return ((buffer[3 + offset] & 0xFF) << 24)
    | ((buffer[2 + offset] & 0xFF) << 16)
    | ((buffer[1 + offset] & 0xFF) << 8)
    | (buffer[0 + offset] & 0xFF);
```

Аналогично `readInt16`:

```java
return ((buffer[1 + offset] & 0xFF) << 8) | (buffer[0 + offset] & 0xFF);
```

**🔴 [vol8/.../streaming/Bson.java:162-168](vol8/src/main/java/ru/mifi/practice/vol8/streaming/Bson.java:162) — `readDouble` возвращает не double.**
Метод читает 8 байт и отдаёт результат `Utils.readInt64(...)` — целое, приведённое к
`double`. Битовое представление IEEE-754 не декодируется, значение получается
бессмысленным.

```java
// было
return Utils.readInt64(bytes, 0);

// стало
return Double.longBitsToDouble(Utils.readInt64(bytes, 0));
```

**🔴 [vol8/.../streaming/Bson.java:147-152](vol8/src/main/java/ru/mifi/practice/vol8/streaming/Bson.java:147) — `offset` игнорируется.**
Сигнатура `readBytes(byte[] bytes, int offset, int length)`, а внутри
`stream.read(bytes, 0, length)` — всегда с нуля. Плюс результат `read` не проверяется:
`InputStream` вправе вернуть меньше запрошенного, и часть буфера останется мусором.
То же в [Bson.java:142-145](vol8/src/main/java/ru/mifi/practice/vol8/streaming/Bson.java:142).

```java
// стало
@Override
public void readBytes(byte[] bytes, int offset, int length) throws IOException {
    ensureAvailable(length);
    stream.readNBytes(bytes, offset, length);
}
```

**🟠 [vol8/.../streaming/Bson.java:212-216](vol8/src/main/java/ru/mifi/practice/vol8/streaming/Bson.java:212) — `readObjectId` без проверки доступности.**
Все соседние методы вызывают `ensureAvailable(...)`, этот — нет. Добавить `ensureAvailable(12)`.

**🟠 [vol8/.../streaming/JsonB.java:74-78](vol8/src/main/java/ru/mifi/practice/vol8/streaming/JsonB.java:74) — во вложенном `catch` печатается не то исключение.**
Ловится `IOException e`, а печатается `ex` из внешнего блока. Настоящая причина закрытия
потока теряется.

```java
// было
} catch (IOException e) {
    ex.printStackTrace();
}

// стало
} catch (IOException e) {
    ex.addSuppressed(e);
}
```

**🟠 [vol8/.../streaming/JsonB.java:46-47](vol8/src/main/java/ru/mifi/practice/vol8/streaming/JsonB.java:46) — пароль БД в исходнике.**
`"jdbc:postgresql://localhost:5432/bson", "postgres", "postgres"` — прямо в коде.
Даже для учебного примера это неверная привычка: брать из системных свойств или переменных
окружения; для теста — Testcontainers.

**🟠 [vol8/.../streaming/JsonB.java:38-41,90-91](vol8/src/main/java/ru/mifi/practice/vol8/streaming/JsonB.java:38) — вечная блокировка при падении читателя.**
`PipedInputStream`/`PipedOutputStream` без таймаута: если поток-читатель умрёт до
`pis.close()`, писатель заблокируется на `generator.write...` навсегда. `generator` и `pos`
не в try-with-resources; `new Thread(...)` без имени и без ожидания завершения. `RULE.md`:
«Ограничивай каждое ожидание таймаутами». Заменить голый `Thread` на `ExecutorService` с
`Future.get(timeout)`.

**🟡 [vol8/.../streaming/Utils.java:5-6,40](vol8/src/main/java/ru/mifi/practice/vol8/streaming/Utils.java:5) — утилитарный класс, `writeInt64` не static.**
`@UtilityClass` (lombok) поверх `final class` со статикой — двойное объявление того, что
`RULE.md` запрещает в принципе. `writeInt64` объявлен без `static` — держится только на
трансформации lombok. Превратить в объект `LittleEndian`, оборачивающий `byte[]`.

**🟡 [vol8/User.java:23-24,47-49](vol8/src/main/java/ru/mifi/practice/vol8/User.java:23) — пароль печатается в открытом виде.**
Пример про сериализацию, но заодно учит логировать секреты. Переименовать поле в
что-то нейтральное или печатать маску.

**🟡 [vol8/User.java:53-57](vol8/src/main/java/ru/mifi/practice/vol8/User.java:53) — `default`-ветка может зациклиться.**
На неизвестном `BsonType` вызывается `readName()` и печатается, но значение не читается —
позиция в потоке не двигается, `reading` остаётся `true`. Ветка должна вызывать
`reader.skipValue()`.

**🟡 [vol8/.../streaming/Bson.java:218](vol8/src/main/java/ru/mifi/practice/vol8/streaming/Bson.java:218) — пазл без контекста.**
`//FIXME: Переписать на потоковое чтение строки` — по `RULE.md` пазл должен содержать
что сделать, почему не сделано сейчас и ссылку на issue.

---

### dormitory — игровая модель

> Модуль не включён в `<modules>` корневого `pom.xml`, поэтому ни компилятор в CI, ни
> checkstyle, ни PMD его не видят. Все дефекты ниже — «невидимые» для сборки.

**🔴 [dormitory/.../room/Room.java:20](dormitory/src/main/java/ru/mifi/practice/room/Room.java:20) — генератор карты пишет не туда.**
`data.tiles[x * y] = Tile.GRASS.id();` — индекс как **произведение** координат вместо
`x + y * width`. Заполняется малая часть массива (индексы вида `x*y` для 24×24 покрывают
~130 ячеек из 576), остальное остаётся нулями.

```java
// было
data.tiles[x * y] = Tile.GRASS.id();

// стало
data.tiles[x + y * width] = Tile.GRASS.id();
```

**🔴 [dormitory/.../ui/Model.java:398-407](dormitory/src/main/java/ru/mifi/practice/ui/Model.java:398) — падение маскируется и превращается в NPE.**
`selfUpdate()` оборачивает загрузку `/icons.png` в `catch (Exception) { ex.printStackTrace(); }`,
а следующей же строкой вызывает `screen.width()`. Если ресурс не прочитался, `screen`
остался `null` → NPE вместо внятного сообщения. Плюс `/icons.png` читается **дважды** подряд.

```java
// стало
private void selfUpdate() {
    ...
    SpriteSheet sheet = sheet("/icons.png");
    this.screen = new Screen.Default(width(), height(), sheet);
    this.lightScreen = new Screen.Default(width(), height(), sheet);
    this.font = new Font.Default(screen);
    this.distance = Math.max(screen.width() / 2, (screen.height() - 8) / 2);
}

private static SpriteSheet sheet(String name) {
    try (InputStream stream = Model.class.getResourceAsStream(name)) {
        return new SpriteSheet(ImageIO.read(
            Objects.requireNonNull(stream, "Missing sprite sheet " + name)));
    } catch (IOException e) {
        throw new IllegalStateException("Cannot load sprite sheet " + name, e);
    }
}
```

**🟠 [dormitory/.../room/Room.java:25-26](dormitory/src/main/java/ru/mifi/practice/room/Room.java:25) — параметр `name` игнорируется.**
`DEFAULT_FACTORY = (name, input) -> new Default("r00m", input, ...)` — переданное имя
выбрасывается, всегда получается `"r00m"`. Передавать `name`.

**🟠 [dormitory/.../ui/Tile.java:12-17,25-28](dormitory/src/main/java/ru/mifi/practice/ui/Tile.java:12) — глобальный реестр в конструкторе.**
`public static Tile[] TILES` и пять `public static Tile` — публичные изменяемые статические
поля (запрет `RULE.md`), а конструктор регистрирует себя в этом массиве и бросает
`RuntimeException("Duplicate tile ids!")`. Порядок статической инициализации становится
частью контракта. Плюс id `4` пропущен (`DIRT` = 5), значит `TILES[4] == null`, и любой
`room.getTile(...)`, попавший на этот id, даёт NPE в `.connectsToGrass`
([Tile.java:228](dormitory/src/main/java/ru/mifi/practice/ui/Tile.java:228)).
Заменить на `enum` либо на неизменяемую `Map<Byte, Tile>`, собираемую явно.

**🟠 [dormitory/.../ui/Screen.java:105](dormitory/src/main/java/ru/mifi/practice/ui/Screen.java:105) — приведение типа.**
`int[] oPixels = ((Default) screen).pixels;` — если передать другую реализацию `Screen`,
будет `ClassCastException`. `RULE.md`: «Пропускай проверку типов, приведение и рефлексию».
Добавить в интерфейс `int pixel(int i)` (он уже есть) и читать через него.

**🟠 [dormitory/.../room/Room.java:82](dormitory/src/main/java/ru/mifi/practice/room/Room.java:82) — raw-массив генериков.**
`Set<Entity>[] result = new Set[width * height];` — unchecked. Заменить на
`List<Set<Entity>>` или `Map<Integer, Set<Entity>>`.

**🟠 [dormitory/.../room/Room.java:101](dormitory/src/main/java/ru/mifi/practice/room/Room.java:101) — record с изменяемыми массивами.**
`record Data(int width, int height, byte[] tiles, byte[] data)` — `record` даёт иллюзию
неизменяемости, а содержимое массивов правится снаружи. Нужны защитные копии в
компактном конструкторе.

**🟡 [dormitory/.../ui/Model.java:37-76](dormitory/src/main/java/ru/mifi/practice/ui/Model.java:37) — god object.**
`Default extends Canvas implements Runnable, Model`, ~20 полей, 410 строк. `RULE.md`:
«Делай каждый класс `final`, с одним-четырьмя атрибутами», SRP. Разделить как минимум на
`GameLoop` (тайминг), `Renderer` (отрисовка), `Window` (Swing) и `Session` (состояние партии).

**🟡 [dormitory/.../ui/Model.java:74](dormitory/src/main/java/ru/mifi/practice/ui/Model.java:74) — утечка `this` из конструктора.**
`new Handler(this)` вызывает `room.addHandler(this)` до завершения конструктора — недостроенный
объект уже подписан на события клавиатуры.

**🟡 [dormitory/.../ui/Tile.java:140-157,162-167,195-212,259-283](dormitory/src/main/java/ru/mifi/practice/ui/Tile.java:140) — закомментированный код.**
Четыре крупных блока. По `RULE.md` (Puzzle Driven Development) это должны быть пазлы
с описанием и ссылкой на issue, а не мёртвый текст.

**🟡 [dormitory/.../entity/Player.java:12](dormitory/src/main/java/ru/mifi/practice/entity/Player.java:12) — `@SuppressWarnings({"PMD.EmptyControlStatement", "PMD.UnusedPrivateMethod"})`.**
Пустые управляющие конструкции и неиспользуемые методы — реальные дефекты, а не ложные
срабатывания.

---

## Часть 3. Нарушения `RULE.md`

| Правило `RULE.md` | Где нарушено | Как привести |
|---|---|---|
| «Исправляй нарушения стиля, доверяя проверяльщикам, а не подавляя их» | 20 `@SuppressWarnings` в 16 файлах | Каждое снимается исправлением: `PMD.UseUtilityClass` → объект вместо статики; `EmptyCatchBlock` → обработка; `UnusedPrivateField` → удалить поле; `NonThreadSafeSingleton` → enum-синглтон |
| «Отвергай… утилитарные классы» | 12 `public abstract class` с одним `static main`; `vol8/Utils`; `vol3/ChainFile` | `abstract` тут — обход PMD. Для `main` завести обычный `final class` с приватным конструктором либо, лучше, объект-приложение с методом `run()` |
| «Убирай статические методы, публичные статические литералы, сеттеры и геттеры» | `vol3/Bank.CENTROBANK`, `vol3/Bank.SBER`, `vol3/Hash.MD5…DEFAULT`, `vol6/Input.DEFAULT`, `vol6/Output.DEFAULT`, `dormitory/Tile.TILES`, `dormitory/Model.NAME`, `dormitory/EntityFactory.DEFAULT`, `vol5/VersionDto` (сеттеры) | Передавать через конструктор; для DTO — `record` |
| «Создавай только неизменяемые объекты» | `vol3/Amount` (`plus`/`minus` мутируют), `vol0/Fly`, `dormitory/Room.Data`, `dormitory/Handler.Key` (public изменяемые поля) | Возвращать новый объект вместо мутации; защитные копии массивов |
| «Отвергай возврат `null` и `null`-аргументы» | `vol7/ConcreteEmployeeFactory` (2×`return null`), `vol1/Human` (конструкторы с `null`), `vol3/Result.ok` (`new Error<>(null, null)`) | `Optional`, отдельные типы, `Objects.requireNonNull` с сообщением |
| «Пропускай проверку типов, приведение и рефлексию» | `vol1/Institute` (`getConstructor`), `vol6/UserRepositoryInMemory` (`TypeToken`), `vol7/SimpleProxy` (`Proxy` + каст), `dormitory/Screen.overlay` (каст) | Функции-фабрики (`Lector::new`), статический прокси, метод в интерфейсе |
| «Делай каждый класс `final`, с одним-четырьмя атрибутами» | `vol0/Fly`, `vol7/FilteredList`, `vol7/User2`, `dormitory/Tile`; по числу полей — `dormitory/Model.Default` (~20), `dormitory/Player` (14), `vol3/Record` (8) | `final` + декомпозиция |
| «Компонуй объекты вместо наследования реализации» | `vol6/AbstractMenu`, `dormitory/AbstractDynamicEntity`, `dormitory/AbstractStaticEntity`, `vol7/observer/User extends Observable` | Интерфейс + делегирование |
| «Предпочитай немедленный отказ молчаливому» | 7 `printStackTrace`; `catch { //Ignore }` в `vol6/FileStorage`; `Optional.empty()` вместо ошибки в `vol3/Jdk`, `vol3/Ok` | Пробрасывать исключение с контекстом |
| «Сообщения об ошибках и логи — одно предложение с контекстом» | `"No such ant"`, `"Duplicate tile ids!"`, `"Not implement yet"`, `throw new UnsupportedOperationException()` без текста | Указывать, что именно и где не сошлось; убрать `!` |
| «Ограничивай каждое ожидание таймаутами» | `vol3/Jdk`, `vol3/Ok` (нет таймаутов), `vol8/JsonB` (piped-потоки без ограничения) | `connectTimeout`/`timeout`, `Future.get(timeout)` |
| «Всегда практикуй TDD» + «Злые тесты» | Тестов нет во всех 10 модулях | См. часть 4 |
| «Используй временные директории» | `vol3/hasq/Main` → `1000000000.csv`, `vol6/FileStorage` → `users.json` в CWD | `Files.createTempDirectory`, `@TempDir` в тестах |
| «Пазлы: что сделать, почему не сейчас, ссылка на issue» | 3 пустых `//TODO:` в `vol1/Institute`, `//FIXME:` в `vol8/Bson` | Дописать контекст |
| «Пропускай инлайн-комментарии; перед классами — docblock о назначении» | Docblock есть только у `vol2/Ant.Step` и `vol8/JsonB`; инлайн-комментарии — по всему `dormitory` | Добавить docblock'и классам, вычистить закомментированный код |

---

## Часть 4. Дорожная карта

### Шаг 1. Починить то, что не работает (🔴)

Порядок — по соотношению «эффект/усилие»:

1. `vol8/Utils.readInt32`/`readInt16` — маскирование `& 0xFF` (2 строки, ломает весь BSON-разбор).
2. `vol8/Bson.readDouble` — `Double.longBitsToDouble` (1 строка).
3. `vol8/Bson.readBytes` — учесть `offset`, использовать `readNBytes` (2 строки).
4. `vol7/FilteredList.add` — `else` вместо последовательного `throw` (3 строки).
5. `vol7/ConcreteEmployeeFactory` — убрать `return null`, дописать реализации.
6. `dormitory/Room.DEFAULT_GENERATOR` — `x + y * width` (1 строка).
7. `vol3/Record.validate` / `Record.from` — проверки на `null` и длину массива.
8. `vol5/Main` — снять `abstract`, сделать слушатель публичным.
9. `vol1/Institute` — реализовать `tick()`, снять `ConcurrentModificationException`, поправить источник события в `Transport.Factory`.
10. `vol6/FileStorage.read` — обработать `null` от Gson.

### Шаг 2. Ввести тесты (по одному модулю за раз)

Начинать с чистой логики без I/O — там «Злые тесты» пишутся буквально:

```java
// vol8: один тест — одно поведение, одно утверждение, негативное сообщение
final class UtilsTest {
    @Test
    void doesNotSignExtendHighBytes() {
        MatcherAssert.assertThat(
            "readInt32 corrupts values with a high bit set",
            Utils.readInt32(new byte[]{0, 0, 0, (byte) 0x80}, 0),
            Matchers.equalTo(0x80000000)
        );
    }
}
```

Очередь: `vol8/Utils` → `vol2/Grid`+`State` → `vol3/Chain`+`Record`+`Amount` →
`vol6/Authentication`+`UserRepositoryInMemory` → `vol5` (`@WebMvcTest`) →
`vol1/Earth` (после того как источник случайности станет один и внедряемым).

По `RULE.md`: одно утверждение на тест последним оператором, никаких `setUp`/`tearDown`
и общих констант, Hamcrest-матчеры, сообщения формулируются негативно («cannot», «dont» —
без апострофов), имя теста читается предложением.

### Шаг 3. Инфраструктура

1. Добавить `dormitory` в `<modules>` корневого `pom.xml` и исправить то, что вывалят
   checkstyle и PMD.
2. Заменить в CI `mvn -B package` на `mvn -B verify`; добавить job с Qodana
   (`qodana.yaml` уже лежит в репозитории и не используется).
3. Подключить `jacoco-maven-plugin`; порог — 40% по строкам с ростом по модулям.
4. Перенести запись файлов из CWD в `java.io.tmpdir`; убрать `1000000000.csv` и
   `users.json` из `.gitignore`.
5. Добавить Maven Wrapper.

### Шаг 4. Снять подавления

Идти по списку из 20 `@SuppressWarnings`: каждое либо снимается исправлением кода
(большинство), либо — если срабатывание действительно ложное — сопровождается
однострочным объяснением почему. Сейчас ни у одного объяснения нет.

### Шаг 5. Дидактика

1. README на каждый модуль: постановка задачи, как запустить, что смотреть.
2. Парные контрпримеры: к `vol0/Fly` — `ValidFly`; к `vol3/Amount` — неизменяемая версия;
   к `vol6/Security` — хеширование с солью рядом с текущим и объяснением разницы.
3. Демонстрации для `vol1/agent`, `vol7/adapter`, `vol7/factory`, `vol7/proxy`,
   `vol7/decorator` — у каждого паттерна должен быть работающий `main` или тест.
4. Разбор компромиссов в `vol7`: почему пять синглтонов и когда какой.
5. Довести `vol4` до подмены реализации (второй `@Component` на `HttpJdkModule`) —
   иначе смысл DI не показан.
6. Довести `vol5` до использования маппера и включить валидацию.
7. Привести имена к единому виду: `val3`/`val4`/`val5` → `vol3`/`vol4`/`vol5`,
   `sevices` → `services`, `storege` → `storage`, `singletone` → `singleton`.

---

## Что не сделано

Осознанно оставлено как есть — это демонстрации ООП, ради которых модули и написаны:

| Место | Почему оставлено |
|---|---|
| [vol0/klass/Fly.java](vol0/src/main/java/ru/mifi/practice/vol0/klass/Fly.java) | «Плохой класс» — изменяемые package-private поля, конструктор по умолчанию, `validate()`. В этом и урок |
| [vol0/inheritance](vol0/src/main/java/ru/mifi/practice/vol0/inheritance) | Наследование и геттер — предмет занятия |
| [vol5/VersionDto.java](vol5/src/main/java/ru/mifi/practice/val5/mapper/dto/VersionDto.java) | JavaBean с сеттерами — с ним работает MapStruct; добавлены только недостающие геттеры |
| [vol7/observer/User.java](vol7/src/main/java/ru/mifi/practice/vol7/observer/User.java) | `Observable` устарел с Java 9, но паттерн разбирается именно в классическом виде; в docblock сказано, чем это плохо |
| [vol7/singletone/Singletone1.java](vol7/src/main/java/ru/mifi/practice/vol7/singletone/Singletone1.java) | Двойная проверка блокировки — единственное оставшееся подавление PMD, объяснено в docblock |
| [vol6/AbstractMenu.java](vol6/src/main/java/ru/mifi/practice/vol6/menu/AbstractMenu.java) | Наследование реализации, показанное намеренно |
| Иерархия сущностей `dormitory` | `AbstractDynamicEntity`/`AbstractStaticEntity` — итоговая демонстрация наследования в курсе |

Не сделано и требует отдельного решения:

1. **Тестов по-прежнему нет.** Это самый крупный оставшийся пробел: правки проверены
   запуском демонстраций, а не автотестами. Порядок, в котором их разумно заводить,
   описан в [дорожной карте](#шаг-2-ввести-тесты-по-одному-модулю-за-раз).
2. **Пароли в `vol6` хешируются SHA-256 без соли.** Оставлен пазл в
   [Security.java](vol6/src/main/java/ru/mifi/practice/vol6/security/Security.java)
   с описанием, что нужно (PBKDF2 + соль на пользователя) и почему не сделано сразу:
   меняется модель `User` и формат `users.json`.
3. **CI не усилен.** `mvn -B package` в
   [maven.yml](.github/workflows/maven.yml) не заменён на `verify`, job с Qodana не заведён,
   покрытие не измеряется — всё это имеет смысл только вместе с тестами.
4. **`vol4` не показывает подмену реализации.** `HttpJdkModule` остался неподключённым:
   второй `@Component` — это уже изменение программы занятия, а не исправление ошибки.
5. **Опечатки в именах пакетов** (`val3`/`val4`/`val5` вместо `vol`, `sevices`,
   `storege`, `singletone`) не тронуты: переименование задевает каждый импорт и
   разойдётся с презентацией.
6. **Контрпримеры «как надо» рядом с «как не надо»** не добавлены — это расширение
   учебного материала, а не правка кода.
