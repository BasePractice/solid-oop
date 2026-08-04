package ru.mifi.practice.vol8;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;

import java.util.Objects;

/**
 * Пользователь, умеющий записать себя в BSON и прочитаться обратно. Пароль наружу
 * не показывается — {@code toString} отдаёт только маску.
 */
public final class User {
    private final String name;
    private final String password;

    public User(String name, String password) {
        this.name = Objects.requireNonNull(name, "User cannot be created without name");
        this.password = Objects.requireNonNull(password, "User cannot be created without password");
    }

    public static User read(BsonReader reader) {
        reader.readStartDocument();
        String name = null;
        String password = null;
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            switch (reader.readName()) {
                case "name" -> name = reader.readString();
                case "password" -> password = reader.readString();
                default -> reader.skipValue();
            }
        }
        reader.readEndDocument();
        return new User(name, password);
    }

    public void write(BsonWriter writer) {
        writer.writeStartDocument();
        writer.writeString("name", name);
        writer.writeString("password", password);
        writer.writeEndDocument();
    }

    @Override
    public String toString() {
        return name + "/" + "*".repeat(password.length());
    }
}
