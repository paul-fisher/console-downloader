package com.pavelrybakov.downloader;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.NoSuchElementException;

public class DownloadSpeed implements Comparable {

    private final int value;
    private final ByteUnit byteUnit;
    private int bytes;

    public DownloadSpeed(int value, @NotNull ByteUnit byteUnit) {
        this.value = value;
        this.byteUnit = byteUnit;
    }

    public DownloadSpeed(int value) {
        this(value, ByteUnit.BYTE);
    }

    public int getBytes() {
        if (bytes == 0) {
            bytes = value * byteUnit.bytes;
        }
        return bytes;
    }

    @NotNull
    public static DownloadSpeed fromString(@NotNull String speed) {
        if (speed.length() < 2) {
            return new DownloadSpeed(Integer.parseInt(speed));
        }
        String postfix = speed.substring(speed.length() - 1);

        ByteUnit byteUnit = ByteUnit.BYTE;
        String value = speed;
        try {
            byteUnit = ByteUnit.get(postfix);
            value = speed.substring(0, speed.length() - 1);
        } catch (NoSuchElementException ignored) {}

        return new DownloadSpeed(Integer.parseInt(value), byteUnit);
    }

    @Override
    public String toString() {
        return String.format("value: %d, unit: %s", value, byteUnit.name());
    }

    public boolean moreThan(@Nullable DownloadSpeed speed) {
        return this.compareTo(speed) == 1;
    }

    public boolean lessThan(@Nullable DownloadSpeed speed) {
        return !moreThan(speed);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return 1;
        }
        if (o == this) {
            return 0;
        }
        if (o instanceof DownloadSpeed) {
            return this.getBytes() > ((DownloadSpeed)o).getBytes() ? 1 : -1;
        }
        return -1;
    }
}