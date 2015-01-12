package com.pavelrybakov.downloader.statistics;

import com.pavelrybakov.downloader.tasks.DownloadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Statistics {

    private final List<TaskStatistics> statistics;

    public Statistics() {
        this.statistics = Collections.synchronizedList(new ArrayList<TaskStatistics>());
    }

    public TaskStatistics logTask(DownloadTask task, int bytesRead, long startAt, long finishAt) {
        TaskStatistics taskStat = new TaskStatistics(task)
                .setBytesRead(bytesRead)
                .setStartAt(startAt)
                .setFinishAt(finishAt);

        statistics.add(taskStat);

        return taskStat;
    }

    @Override
    public String toString() {
        int totalBytesRead = 0;
        long startTime = 0;
        long finishTime = 0;
        synchronized (statistics) {
            for (TaskStatistics stat : statistics) {
                totalBytesRead += stat.getBytesRead();
                if (startTime == 0 || stat.getStartAt() < startTime) {
                    startTime = stat.getStartAt();
                }
                if (stat.getFinishAt() > finishTime) {
                    finishTime = stat.getFinishAt();
                }
            }
        }
        return String.format("Bytes downloaded: %d\nSeconds spent: %d", totalBytesRead, (int)((finishTime - startTime) / 1000));
    }
}
