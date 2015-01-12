package com.pavelrybakov.downloader.tasks;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

public class DownloadTask {

    private final String uri;
    private final List<String> copyTo;
    private final int bytesPerSecond;

    public DownloadTask(@NotNull String uri, @NotNull List<String> copyTo, int bytesPerSecond) {
        this.uri = uri;
        this.copyTo = copyTo;
        this.bytesPerSecond = bytesPerSecond;
    }

    @Override
    public String toString() {
        return String.format("uri: %s", uri);
    }

    @NotNull
    public String getUri() {
        return uri;
    }

    @NotNull
    public List<String> getCopyTo() {
        return Collections.unmodifiableList(copyTo);
    }

    public int getBytesPerSecond() {
        return bytesPerSecond;
    }
}
