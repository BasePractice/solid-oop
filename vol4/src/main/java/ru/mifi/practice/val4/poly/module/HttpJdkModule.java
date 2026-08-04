package ru.mifi.practice.val4.poly.module;

import dagger.Module;
import dagger.Provides;
import ru.mifi.practice.val3.cont.Deserializer;
import ru.mifi.practice.val3.cont.Factory;
import ru.mifi.practice.val3.cont.Http;
import ru.mifi.practice.val3.cont.http.Jdk;

import javax.inject.Singleton;

@Module
public final class HttpJdkModule {
    @Provides
    @Singleton
    public Factory<Http> provideHttp(Factory<Deserializer> deFactory) {
        return () -> new Jdk(deFactory.create());
    }
}
