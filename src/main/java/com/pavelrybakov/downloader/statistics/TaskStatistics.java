package com.pavelrybakov.downloader.statistics;

import com.pavelrybakov.downloader.tasks.DownloadTask;

import javax.validation.constraints.NotNull;

public class TaskStatistics {

    public final DownloadTask task;
    public final long threadId;

    private int bytesRead;
    private long startAt;
    private long finishAt;

    public TaskStatistics(DownloadTask task) {
        this.task = task;
        this.threadId = Thread.currentThread().getId();
    }

    public int getBytesRead() {
        return bytesRead;
    }

    @NotNull
    public TaskStatistics setBytesRead(int bytesRead) {
        this.bytesRead = bytesRead;
        return this;
    }

    public long getStartAt() {
        return startAt;
    }

    @NotNull
    public TaskStatistics setStartAt(long startAt) {
        this.startAt = startAt;
        return this;
    }

    public long getFinishAt() {
        return finishAt;
    }

    @NotNull
    public TaskStatistics setFinishAt(long finishAt) {
        this.finishAt = finishAt;
        return this;
    }
}
