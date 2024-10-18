package ru.mifi.practice.val3.hasq;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Locale;

public interface Hash {
    Hash MD5 = new MessageDigestHash("MD5");
    Hash SHA1 = new MessageDigestHash("SHA1");
    Hash SHA256 = new MessageDigestHash("SHA256");
    Hash SHA512 = new MessageDigestHash("SHA512");
    Hash DEFAULT = SHA1;

    String hash(Object... args);

    final class MessageDigestHash implements Hash {
        private final String algorithm;

        private MessageDigestHash(String algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public String hash(Object... objects) {
            try {
                MessageDigest digest = MessageDigest.getInstance(algorithm);
                for (Object object : objects) {
                    digest.update(object.toString().getBytes());
                }
                byte[] bytes = digest.digest();
                return HexFormat.of().formatHex(bytes).toUpperCase(Locale.ROOT);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
