package com.pavelrybakov.downloader;

import com.pavelrybakov.downloader.statistics.Statistics;
import com.pavelrybakov.downloader.tasks.DownloadTask;
import com.pavelrybakov.downloader.tasks.DownloadTasks;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Downloader implements Runnable {

    private static final int perSecondIntervals = 10;

    private final DownloadTasks tasks;
    private final Statistics stats;

    public Downloader(@NotNull DownloadTasks tasks, @NotNull Statistics stats) {
        this.tasks = tasks;
        this.stats = stats;
    }

    public int download(DownloadTask task) {
        long now;
        int i;
        int bytesRead = 0;
        long period = TimeUnit.SECONDS.toNanos(1) / perSecondIntervals;

        int bufferBytesPerPeriod = task.getBytesPerSecond() / perSecondIntervals;

        BufferedInputStream bis = null;
        List<OutputStream> outputs = new ArrayList<>();

        try {
            bis = new BufferedInputStream(new URL(task.getUri()).openStream());

            for (String filePath : task.getCopyTo()) {
                outputs.add(new BufferedOutputStream(new FileOutputStream(filePath)));
            }

            byte[] buf = new byte[bufferBytesPerPeriod];
            now = System.nanoTime();
            while ((i = bis.read(buf)) != -1) {
                bytesRead += i;
                long diff = System.nanoTime() - now;

                for (OutputStream out : outputs) {
                    out.write(buf, 0, i);
                }

                if (period > diff) {
                    Thread.sleep(TimeUnit.MILLISECONDS.convert(period - diff, TimeUnit.NANOSECONDS));
                }
                now = System.nanoTime();
            }

            for (OutputStream out : outputs) {
                out.flush();
            }
        } catch (MalformedURLException e) {
            System.out.println(String.format("ERROR uri: %s", task.getUri()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                for (OutputStream out : outputs) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytesRead;
    }

    @Override
    public void run() {
        DownloadTask task;
        long startAt;
        while ((task = tasks.next()) != null) {
            System.out.println(String.format("Downloading %s", task));
            startAt = System.currentTimeMillis();
            int bytesRead = download(task);
            this.stats.logTask(task, bytesRead, startAt, System.currentTimeMillis());
        }

    }
}