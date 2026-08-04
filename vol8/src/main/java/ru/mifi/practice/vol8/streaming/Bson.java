package ru.mifi.practice.vol8.streaming;

import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import org.bson.BsonSerializationException;
import org.bson.ByteBuf;
import org.bson.io.BsonInput;
import org.bson.io.BsonInputMark;
import org.bson.io.BsonOutput;
import org.bson.io.OutputBuffer;
import org.bson.types.ObjectId;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

public interface Bson {

    static BsonInput newInput(byte[] bytes) {
        return newInput(new ByteArrayInputStream(bytes));
    }

    static BsonInput newInput(InputStream stream) {
        return new BsonInputStream(stream);
    }

    static BsonOutput newOutput(OutputStream stream) {
        return new BsonOutputStream(stream);
    }

    final class BsonOutputStream extends OutputBuffer {
        private final File temporaryFile;
        private final RandomAccessFile raf;
        private final OutputStream stream;

        @SneakyThrows
        private BsonOutputStream(OutputStream stream) {
            this.temporaryFile = File.createTempFile(UUID.randomUUID().toString(), ".stream");
            this.raf = new RandomAccessFile(temporaryFile, "rwd");
            this.stream = stream;
        }

        @SneakyThrows
        @Override
        public void close() {
            raf.close();
            try (stream; var inputStream = new FileInputStream(temporaryFile)) {
                ByteStreams.copy(inputStream, stream);
            } finally {
                Files.deleteIfExists(temporaryFile.toPath());
            }
        }

        @Override
        public int pipe(OutputStream out) throws IOException {
            throw new UnsupportedOperationException("BsonOutputStream pipe not supported");
        }

        @Override
        public List<ByteBuf> getByteBuffers() {
            throw new UnsupportedOperationException("BsonOutputStream getByteBuffers not supported");
        }

        @SneakyThrows
        @Override
        public int getPosition() {
            return (int) raf.getFilePointer();
        }

        @SneakyThrows
        @Override
        public int getSize() {
            return (int) raf.length();
        }

        @Override
        public void truncateToPosition(int newPosition) {
            throw new UnsupportedOperationException("BsonOutputStream truncateToPosition not supported");
        }

        @SneakyThrows
        @Override
        public void writeBytes(byte[] bytes, int offset, int length) {
            raf.write(bytes, offset, length);
        }

        @SneakyThrows
        @Override
        public void writeByte(int value) {
            raf.write(value);
        }

        @SneakyThrows
        @Override
        protected void write(int position, int value) {
            long lastPosition = getPosition();
            raf.seek(position);
            raf.write(value);
            raf.seek(lastPosition);
        }
    }

    final class BsonInputStream implements BsonInput {
        private static final int READ_LIMIT = 4096;
        private static final String[] ONE_BYTE_ASCII_STRINGS = new String[Byte.MAX_VALUE + 1];

        static {
            for (int b = 0; b < ONE_BYTE_ASCII_STRINGS.length; b++) {
                ONE_BYTE_ASCII_STRINGS[b] = String.valueOf((char) b);
            }
        }

        private final PosBufferedInputStream stream;

        private BsonInputStream(InputStream stream) {
            this.stream = new PosBufferedInputStream(stream);
        }

        @Override
        public int getPosition() {
            return stream.getPosition();
        }

        @SneakyThrows
        @Override
        public byte readByte() {
            ensureAvailable(1);
            return (byte) stream.read();
        }

        @Override
        public void readBytes(byte[] bytes) {
            readBytes(bytes, 0, bytes.length);
        }

        @SneakyThrows
        @Override
        public void readBytes(byte[] bytes, int offset, int length) {
            ensureAvailable(length);
            int read = stream.readNBytes(bytes, offset, length);
            if (read != length) {
                throw new BsonSerializationException(
                    format("While decoding a BSON document %d bytes were required, but only %d were read",
                        length, read));
            }
        }

        @Override
        public long readInt64() {
            return new LittleEndian(next(8)).int64();
        }

        @Override
        public double readDouble() {
            return new LittleEndian(next(8)).real();
        }

        @Override
        public int readInt32() {
            return new LittleEndian(next(4)).int32();
        }

        private byte[] next(int length) {
            byte[] bytes = new byte[length];
            readBytes(bytes);
            return bytes;
        }

        @Override
        public String readString() {
            int size = readInt32();
            if (size <= 0) {
                throw new BsonSerializationException(format("While decoding a BSON string found a size that is not a positive number: %d",
                    size));
            }
            ensureAvailable(size);
            return readString(size);
        }

        private String readString(final int size) {
            if (size == 2) {
                byte asciiByte = readByte();               // if only one byte in the string, it must be ascii.
                byte nullByte = readByte();                // read null terminator
                if (nullByte != 0) {
                    throw new BsonSerializationException("Found a BSON string that is not null-terminated");
                }
                if (asciiByte < 0) {
                    return StandardCharsets.UTF_8.newDecoder().replacement();
                }
                return ONE_BYTE_ASCII_STRINGS[asciiByte];  // this will throw if asciiByte is negative
            } else {
                byte[] bytes = new byte[size - 1];
                readBytes(bytes);
                byte nullByte = readByte();
                if (nullByte != 0) {
                    throw new BsonSerializationException("Found a BSON string that is not null-terminated");
                }
                return new String(bytes, StandardCharsets.UTF_8);
            }
        }

        @Override
        public ObjectId readObjectId() {
            byte[] bytes = new byte[12];
            readBytes(bytes);
            return new ObjectId(bytes);
        }

        //TODO: Читать cstring потоково, без mark/reset. Сейчас строка ограничена READ_LIMIT
        //      байтами буфера, потому что позиция ищется проходом вперёд с последующим откатом.
        //      Не сделано сразу: требует собственного буфера вместо BufferedInputStream.
        @SneakyThrows
        @Override
        public String readCString() {
            int mark = getPosition();
            stream.mark(READ_LIMIT);
            skipCString();
            int size = getPosition();
            stream.reset();
            return readString(size - mark);
        }

        @Override
        public void skipCString() {
            boolean checkNext = true;
            while (checkNext) {
                if (!hasRemaining()) {
                    throw new BsonSerializationException("Found a BSON string that is not null-terminated");
                }
                checkNext = readByte() != 0;
            }
        }

        @SneakyThrows
        @Override
        public void skip(int numBytes) {
            stream.skip(numBytes);
        }

        @Override
        public BsonInputMark getMark(int readLimit) {
            stream.mark(readLimit);
            return this::rewind;
        }

        @SneakyThrows
        private void rewind() {
            stream.reset();
        }

        @SneakyThrows
        @Override
        public boolean hasRemaining() {
            return stream.available() > 0;
        }

        @SneakyThrows
        @Override
        public void close() {
            stream.close();
        }

        @SneakyThrows
        private void ensureAvailable(final int bytesNeeded) {
            if (stream.available() < bytesNeeded) {
                throw new BsonSerializationException(format("While decoding a BSON document %d bytes were required, "
                    + "but only %d remain", bytesNeeded, stream.available()));
            }
        }

        private static final class PosBufferedInputStream extends BufferedInputStream {

            private PosBufferedInputStream(InputStream in) {
                super(in);
            }

            int getPosition() {
                return pos;
            }
        }
    }
}
