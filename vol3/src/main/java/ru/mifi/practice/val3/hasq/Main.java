package ru.mifi.practice.val3.hasq;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ru.mifi.practice.val3.hasq.Chain.Detailed;
import static ru.mifi.practice.val3.hasq.Chain.Token;
import static ru.mifi.practice.val3.hasq.Chain.ValidateType;

/**
 * Строит цепочку, сохраняет её в файл, читает обратно и проверяет обе — записанная
 * и прочитанная должны сойтись.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws IOException {
        String tokenValue = "1000000000";
        Path directory = Files.createTempDirectory("hasq");
        Path file = directory.resolve(tokenValue + ".csv");
        Hash hash = Hash.DEFAULT;
        Chain chain = Chain.newChain(new Token(tokenValue, hash), "init")
            .add("init", "N1")
            .add("init", "N2")
            .add("init", "N3")
            .add("init", "N4")
            .add("init", "N5")
            .add("init", "N6");
        ChainFile stored = new ChainFile(file);
        stored.write(chain.root());
        Result<ValidateType, Detailed> restored = stored.read(hash).validate();
        System.out.printf("Файл:    %s%n", file);
        System.out.printf("Читано:  %s%n", restored);
        System.out.printf("В памяти: %s%n", chain.validate());
    }
}
