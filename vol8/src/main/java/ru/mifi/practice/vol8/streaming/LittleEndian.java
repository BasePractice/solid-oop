package ru.mifi.practice.vol8.streaming;

/**
 * Целое число, записанное от младшего байта к старшему — в этом порядке BSON хранит
 * длины документов, строк и числовые поля.
 */
final class LittleEndian {
    private final byte[] bytes;

    LittleEndian(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    int int32() {
        require(4);
        return (octet(3) << 24) | (octet(2) << 16) | (octet(1) << 8) | octet(0);
    }

    long int64() {
        require(8);
        long value = 0;
        for (int i = 7; i >= 0; i--) {
            value = (value << 8) | octet(i);
        }
        return value;
    }

    double real() {
        return Double.longBitsToDouble(int64());
    }

    private int octet(int index) {
        return bytes[index] & 0xFF;
    }

    private void require(int size) {
        if (bytes.length < size) {
            throw new IllegalArgumentException(
                "Little endian value needs " + size + " bytes, buffer holds " + bytes.length);
        }
    }
}
