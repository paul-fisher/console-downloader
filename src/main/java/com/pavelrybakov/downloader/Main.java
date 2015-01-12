package com.pavelrybakov.downloader;

import com.pavelrybakov.downloader.statistics.Statistics;
import com.pavelrybakov.downloader.tasks.DownloadTasks;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Config config = prepareConfig(args);
        if (config == null) {
            return;
        }

        DownloadTasks tasks;
        try {
            tasks = DownloadTasks.init(config);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Statistics stat = new Statistics();

        List<Thread> pool = new ArrayList<>();
        for (int i = 0; i < config.getThreads(); i++) {
            Thread thread = new Thread(new Downloader(tasks, stat));
            pool.add(thread);
            thread.start();
        }

        for (Thread thread : pool) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done");
        System.out.println(String.format("Stat:\n%s", stat));
    }

    @Nullable
    private static Config prepareConfig(String[] args) {
        Config config = new Config();

        if (args.length == 0) {
            String jarName = "*.jar";
            try {
                jarName = FilenameUtils.getBaseName(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()) + ".jar";
            } catch (URISyntaxException ignored) {}
            System.out.println(String.format("USAGE: java -jar %s [-n <threads>] [-l <max download speed>] [-f <input file>] [-o <output dir>]", jarName));
            System.out.println(String.format("Input file format:\n<url1> <local file1 to save>\n<url2> <local file2 to save>\n..."));
            return null;
        }

        if (args.length == 1) {
            config.setInputFile(args[0]);
        }

        if (args.length > 1) {
            for (int i = 0; i < args.length; i += 2) {
                if (args.length < i + 1) {
                    throw new IllegalArgumentException(String.format("Illegal arguments count, parameter value expected for %s", args[i]));
                }

                String param = args[i];
                switch (param) {
                    case "-n":
                        try {
                            config.setThreads(Integer.parseInt(args[i + 1]));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(String.format("%s is invalid/out of range number", args[i+1]));
                        }
                        break;
                    case "-l":
                        try {
                            config.setMaxSpeed(DownloadSpeed.fromString(args[i + 1]));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(String.format("%s is invalid/out of range number", args[i+1]));
                        }
                        break;
                    case "-f":
                        config.setInputFile(args[i+1]);
                        break;
                    case "-o":
                        config.setOutputDir(args[i+1]);
                        break;
                }
            }
        }

        validate(config);

        return config;
    }

    private static void validate(@NotNull Config config) {
        if (config.getInputFile() == null || config.getInputFile().isEmpty()) {
            throw new IllegalArgumentException("No input file specified");
        }
        File input = new File(config.getInputFile());
        if (!input.exists()) {
            throw new IllegalArgumentException(String.format("Input file %s not exists", config.getInputFile()));
        }

        if (config.getOutputDir() == null || config.getOutputDir().isEmpty()) {
            throw new IllegalArgumentException("No output dir specified");
        }

        File dir = new File(config.getOutputDir());
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalArgumentException(String.format("Cannot create directory at %s", config.getOutputDir()));
        }

        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(String.format("Output directory parameter is not directory: %s", config.getOutputDir()));
        }
    }
}