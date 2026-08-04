package ru.mifi.practice.vol8;

import org.bson.AbstractBsonWriter;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonReader;
import ru.mifi.practice.vol8.streaming.Bson;

import java.io.ByteArrayOutputStream;

/**
 * Круговой рейс объекта через BSON: пользователь пишется в бинарный поток и читается
 * из него обратно, чтобы было видно, что запись и разбор симметричны.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (var bsonOutput = Bson.newOutput(output);
             AbstractBsonWriter writer = new BsonBinaryWriter(bsonOutput)) {
            new User("bob", "password").write(writer);
            writer.flush();
        }
        try (BsonReader reader = new BsonBinaryReader(Bson.newInput(output.toByteArray()))) {
            System.out.println(User.read(reader));
        }
    }
}
