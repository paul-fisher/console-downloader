package com.pavelrybakov.downloader;

import javax.validation.constraints.NotNull;
import java.util.NoSuchElementException;

public enum ByteUnit {

    BYTE("b", 1),
    KILO_BYTE("k", 1024),
    MEGA_BYTE("m", 1024 * 1024),
    GIGA_BYTE("g", 1024 * 1024 * 1024);

    public final String id;
    public final int bytes;

    ByteUnit(@NotNull String id, int bytes) {
        this.id = id;
        this.bytes = bytes;
    }

    @NotNull
    public static ByteUnit get(@NotNull String id) {
        for (ByteUnit size : values()) {
            if (size.id.equalsIgnoreCase(id)) {
                return size;
            }
        }
        throw new NoSuchElementException(String.format("No speed size with %s suffix", id));
    }

}