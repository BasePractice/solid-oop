package ru.mifi.practice.vol6.security;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

/**
 * Превращение пароля в строку, которую не жалко положить на диск.
 *
 * <p>Внимание: здесь одна итерация SHA-256 без соли — этого достаточно, чтобы показать
 * устройство меню и репозитория, но категорически мало для настоящего хранения паролей.
 * Один и тот же пароль у двух пользователей даёт одинаковый хеш, а перебор по радужной
 * таблице занимает секунды.
 *
 * <p>Боевой вариант — медленная функция с солью на пользователя: PBKDF2 из JDK, bcrypt
 * или argon2.
 */
public interface Security {

    static Hash createHash() {
        return HashType.SHA256;
    }

    //TODO: Добавить соль на пользователя и медленную функцию (PBKDF2WithHmacSHA256).
    //      Не сделано сразу: требует поля соли в User и миграции users.json,
    //      а тема занятия — меню и репозиторий, не криптография.
    enum HashType implements Hash {
        SHA256(Hashing.sha256());

        private final HashFunction hashFunction;

        HashType(HashFunction hashFunction) {
            this.hashFunction = hashFunction;
        }

        @Override
        public String hash(String password) {
            return hashFunction.hashString(password, StandardCharsets.UTF_8).toString();
        }
    }

    @FunctionalInterface
    interface Hash {
        String hash(String password);
    }
}
