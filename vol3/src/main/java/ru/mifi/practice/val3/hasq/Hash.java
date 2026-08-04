package ru.mifi.practice.val3.hasq;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

/**
 * Отпечаток набора значений. MD5 и SHA1 оставлены, чтобы было с чем сравнивать,
 * но по умолчанию берётся SHA-256: первые два давно взломаны на коллизии и для
 * защиты цепочки не годятся.
 */
public interface Hash {
    Hash MD5 = new MessageDigestHash("MD5");
    Hash SHA1 = new MessageDigestHash("SHA1");
    Hash SHA256 = new MessageDigestHash("SHA-256");
    Hash SHA512 = new MessageDigestHash("SHA-512");
    Hash DEFAULT = SHA256;

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
                    digest.update(object.toString().getBytes(StandardCharsets.UTF_8));
                }
                return HexFormat.of().formatHex(digest.digest()).toUpperCase(Locale.ROOT);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("Algorithm " + algorithm + " is unknown to this JVM", e);
            }
        }

    }
}
