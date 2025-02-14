package com.shade.platform.model.util;

import com.shade.util.NotNull;
import com.shade.util.Nullable;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.function.Function;
import java.util.prefs.Preferences;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public final class IOUtils {
    private static final NumberFormat UNIT_FORMAT = new DecimalFormat("#.## ");
    private static final String[] UNIT_NAMES = {"B", "kB", "mB", "gB", "tB", "pB", "eB"};

    private IOUtils() {
        // prevents instantiation
    }

    @NotNull
    public static <T> T getNotNull(@NotNull Preferences preferences, @NotNull String key, @NotNull Function<String, ? extends T> mapper) {
        return mapper.apply(Objects.requireNonNull(preferences.get(key, null)));
    }

    @NotNull
    public static String getNotNull(@NotNull Preferences preferences, @NotNull String key) {
        return getNotNull(preferences, key, Function.identity());
    }

    @NotNull
    public static Preferences[] children(@NotNull Preferences node) {
        return Stream.of(unchecked(node::childrenNames))
            .map(node::node)
            .toArray(Preferences[]::new);
    }

    @Nullable
    public static <T> T getNullable(@NotNull Preferences preferences, @NotNull String key, @NotNull Function<String, ? extends T> mapper) {
        final String value = preferences.get(key, null);
        if (value != null) {
            return mapper.apply(value);
        } else {
            return null;
        }
    }

    @Nullable
    public static String getNullable(@NotNull Preferences preferences, @NotNull String key) {
        return getNullable(preferences, key, Function.identity());
    }

    @NotNull
    public static Reader newCompressedReader(@NotNull Path path) throws IOException {
        if (isCompressed(path)) {
            return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path.toFile())), StandardCharsets.UTF_8));
        } else {
            return Files.newBufferedReader(path);
        }
    }

    public static boolean isCompressed(@NotNull Path path) {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            final ByteBuffer buffer = IOUtils.readExact(channel, 2);
            final int magic = buffer.getShort() & 0xffff;
            return magic == GZIPInputStream.GZIP_MAGIC;
        } catch (IOException ignored) {
            return false;
        }
    }

    @NotNull
    public static ByteBuffer readExact(@NotNull ReadableByteChannel channel, int capacity) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN);
        channel.read(buffer);
        return buffer.position(0);
    }

    @NotNull
    public static byte[] getBytesExact(@NotNull ByteBuffer buffer, int size) {
        final byte[] bytes = new byte[size];
        buffer.get(bytes);
        return bytes;
    }

    @NotNull
    public static String getString(@NotNull ByteBuffer buffer, int length) {
        return new String(getBytesExact(buffer, length), StandardCharsets.UTF_8);
    }

    @NotNull
    public static BigInteger getUInt128(@NotNull ByteBuffer buffer) {
        final byte[] data = new byte[16];
        buffer.slice().order(ByteOrder.BIG_ENDIAN).get(data);
        buffer.position(buffer.position() + 16);
        return new BigInteger(1, data);
    }

    public static void putUInt128(@NotNull ByteBuffer buffer, @NotNull BigInteger value) {
        final byte[] data = value.toByteArray();
        if (data.length > 16) {
            throw new IllegalArgumentException("The number is too big: " + value);
        }
        buffer.slice().order(ByteOrder.BIG_ENDIAN).put(data);
        buffer.position(buffer.position() + 16);
    }

    @NotNull
    public static byte[] toByteArray(@NotNull int... src) {
        final byte[] dst = new byte[src.length * 4];
        for (int i = 0; i < src.length; i++) {
            dst[i * 4] = (byte) (src[i] & 0xff);
            dst[i * 4 + 1] = (byte) (src[i] >> 8 & 0xff);
            dst[i * 4 + 2] = (byte) (src[i] >> 16 & 0xff);
            dst[i * 4 + 3] = (byte) (src[i] >> 24 & 0xff);
        }
        return dst;
    }

    @NotNull
    public static byte[] toByteArray(@NotNull long[] src) {
        final byte[] dst = new byte[src.length * 8];
        for (int i = 0; i < src.length; i++) {
            dst[i * 8] = (byte) (src[i] & 0xff);
            dst[i * 8 + 1] = (byte) (src[i] >>> 8 & 0xff);
            dst[i * 8 + 2] = (byte) (src[i] >>> 16 & 0xff);
            dst[i * 8 + 3] = (byte) (src[i] >>> 24 & 0xff);
            dst[i * 8 + 4] = (byte) (src[i] >>> 32 & 0xff);
            dst[i * 8 + 5] = (byte) (src[i] >>> 40 & 0xff);
            dst[i * 8 + 6] = (byte) (src[i] >>> 48 & 0xff);
            dst[i * 8 + 7] = (byte) (src[i] >>> 56 & 0xff);
        }
        return dst;
    }

    public static void put(@NotNull byte[] dst, int index, int value) {
        dst[index] = (byte) (value & 0xff);
        dst[index + 1] = (byte) (value >> 8 & 0xff);
        dst[index + 2] = (byte) (value >> 16 & 0xff);
        dst[index + 3] = (byte) (value >> 24 & 0xff);
    }

    public static void put(@NotNull byte[] dst, int index, long value) {
        dst[index] = (byte) (value & 0xff);
        dst[index + 1] = (byte) (value >>> 8 & 0xff);
        dst[index + 2] = (byte) (value >>> 16 & 0xff);
        dst[index + 3] = (byte) (value >>> 24 & 0xff);
        dst[index + 4] = (byte) (value >>> 32 & 0xff);
        dst[index + 5] = (byte) (value >>> 40 & 0xff);
        dst[index + 6] = (byte) (value >>> 48 & 0xff);
        dst[index + 7] = (byte) (value >>> 56 & 0xff);
    }

    public static long toLong(@NotNull byte[] src, int index) {
        return
            (long) (src[index] & 0xff) |
                (long) (src[index + 1] & 0xff) << 8 |
                (long) (src[index + 2] & 0xff) << 16 |
                (long) (src[index + 3] & 0xff) << 24 |
                (long) (src[index + 4] & 0xff) << 32 |
                (long) (src[index + 5] & 0xff) << 40 |
                (long) (src[index + 6] & 0xff) << 48 |
                (long) (src[index + 7] & 0xff) << 56;
    }

    public static int alignUp(int value, int to) {
        return (value + to - 1) / to * to;
    }

    public static int wrapAround(int index, int max) {
        return (index % max + max) % max;
    }

    public static <T> int indexOf(@NotNull T[] array, @NotNull T value) {
        for (int i = 0; i < array.length; i++) {
            if (value.equals(array[i])) {
                return i;
            }
        }

        return -1;
    }

    @NotNull
    public static String formatSize(long size) {
        double result = size;
        int unit = 0;

        while (result >= 1024 && unit < UNIT_NAMES.length - 1) {
            result /= 1024;
            unit += 1;
        }

        return UNIT_FORMAT.format(result) + UNIT_NAMES[unit];
    }

    public static <T, E extends Throwable> T unchecked(@NotNull ThrowableSupplier<T, E> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public interface ThrowableSupplier<T, E extends Throwable> {
        T get() throws E;
    }
}
