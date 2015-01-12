package com.pavelrybakov.downloader.tasks;

import com.pavelrybakov.downloader.Config;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.pavelrybakov.downloader.StringUtils.readLines;

public class DownloadTasks {

    private final Iterator<Map.Entry<String, List<String>>> tasksIterator;
    private final int bytesPerSecond;

    private DownloadTasks(@NotNull Map<String, List<String>> tasks, @NotNull Config config) {
        this.tasksIterator = tasks.entrySet().iterator();
        this.bytesPerSecond = config.getBytesSpeedPerThread();
    }

    @Nullable
    public synchronized DownloadTask next() {
        if (!tasksIterator.hasNext()) {
            return null;
        }

        Map.Entry<String, List<String>> entry = tasksIterator.next();

        return new DownloadTask(entry.getKey(), entry.getValue(), bytesPerSecond);
    }

    @NotNull
    public static DownloadTasks init(@NotNull Config config) throws IOException {
        return new DownloadTasks(initTasks(config), config);
    }

    @NotNull
    private static Map<String, List<String>> initTasks(@NotNull Config config) throws IOException {
        Map<String, List<String>> result = new HashMap<>();
        for (String line : readLines(config.getInputFile())) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(" ");
            String uri = parts[0];
            String output = parts.length < 2
                    ? config.getOutputDir() + File.separator + FilenameUtils.getBaseName(uri) + "." + FilenameUtils.getExtension(uri)
                    : config.getOutputDir() + File.separator + parts[1];

            if (!result.containsKey(uri)) {
                List<String> outputs = new ArrayList<>();
                outputs.add(output);
                result.put(uri, outputs);
            } else if (!result.get(uri).contains(output)) {
                result.get(uri).add(output);
            }
        }
        return result;
    }
}
