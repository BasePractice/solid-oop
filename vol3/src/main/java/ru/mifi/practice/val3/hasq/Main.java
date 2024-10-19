package ru.mifi.practice.val3.hasq;

import java.io.IOException;
import java.nio.file.Path;

import static ru.mifi.practice.val3.hasq.Chain.Detailed;
import static ru.mifi.practice.val3.hasq.Chain.Token;
import static ru.mifi.practice.val3.hasq.Chain.ValidateType;

public abstract class Main {
    public static void main(String[] args) throws IOException {
        String tokenValue = "1000000000";
        Path filePath = Path.of(tokenValue + ".csv");
        Hash hash = Hash.DEFAULT;
        Chain chain = Chain.newChain(new Token(tokenValue, hash), "init")
            .add("init", "N1")
            .add("init", "N2")
            .add("init", "N3")
            .add("init", "N4")
            .add("init", "N5")
            .add("init", "N6");
        ChainFile.write(filePath, chain.root());
        Chain read = ChainFile.read(filePath, hash);
        Result<ValidateType, Detailed> validated = read.validate();
        System.out.println(validated);
        validated = chain.validate();
        System.out.println(validated);
    }
}
