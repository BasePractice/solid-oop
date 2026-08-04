package ru.mifi.practice.val3.cont;

import ru.mifi.practice.val3.cont.business.DefaultLogic;
import ru.mifi.practice.val3.cont.deserialize.GsonJson;
import ru.mifi.practice.val3.cont.http.Jdk;
import ru.mifi.practice.val3.cont.sevices.OpenMeteo;
import ru.mifi.practice.val3.cont.sevices.WhoIs;

/**
 * Сборка вручную: здесь и только здесь известно, что JSON разбирает Gson, а ходит
 * в сеть клиент из JDK. В vol4 ровно эта проводка отдаётся Dagger — а сама
 * {@link DefaultLogic} не меняется ни на строчку.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Factory<Http> http = () -> new Jdk(new GsonJson());
        BusinessLogic logic = new DefaultLogic(
            new WhoIs(http.create()),
            new OpenMeteo(http.create()));
        logic.execute();
    }
}
