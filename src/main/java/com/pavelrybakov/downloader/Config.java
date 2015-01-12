package com.pavelrybakov.downloader;

import javax.validation.constraints.NotNull;
import java.io.File;

public class Config {

    private static final DownloadSpeed maxSpeedPerThread = DownloadSpeed.fromString("10m");
    private static final DownloadSpeed defaultMaxSpeed = DownloadSpeed.fromString("10m");
    private static final int defaultThreads = 1;
    private static final String defaultOutputDir = System.getProperty("user.dir") + File.separator + "out";

    private int threads;
    private DownloadSpeed maxSpeed;
    private String inputFile;
    private String outputDir;

    public Config() {
        this.setThreads(defaultThreads);
        this.setOutputDir(defaultOutputDir);
        this.setMaxSpeed(defaultMaxSpeed);
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    @NotNull
    public DownloadSpeed getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(@NotNull DownloadSpeed maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @NotNull
    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(@NotNull String inputFile) {
        this.inputFile = inputFile;
    }

    @NotNull
    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(@NotNull String outputDir) {
        this.outputDir = outputDir;
    }

    public int getBytesSpeedPerThread() {
        int threads = Math.max(getThreads(), 1);

        int maxBytesPerThread = maxSpeedPerThread.getBytes() / threads;
        int currentBytesPerThread = (int) Math.floor(getMaxSpeed().getBytes() / threads);

        return Math.min(maxBytesPerThread, currentBytesPerThread);
    }
}