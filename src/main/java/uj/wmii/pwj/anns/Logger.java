package uj.wmii.pwj.anns;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Logger {
    boolean initialized = false;

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized() {
        initialized = true;
    }

    public void deleteAllLogs() {
        Path here = FileSystems.getDefault().getPath(".");
        File logDir = here.resolve(".ysnp").resolve("Logs").toFile();

        if (!isInitialized() && logDir.exists()) {
            try (Stream<Path> walk = Files.walk(logDir.toPath())) {
                walk.sorted(Comparator.reverseOrder()) // Sort in reverse order (files before folders)
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                                System.out.println("Deleted log file: " + path.toAbsolutePath());
                            } catch (IOException e) {
                                System.err.println("Failed to delete: " + path + " - " + e.getMessage());
                            }
                        });
            } catch (IOException e) {
                System.err.println("Error during directory traversal: " + e.getMessage());
            }
        }
    }

    public void createDirectoryTree() {

        Path here = FileSystems.getDefault().getPath(".");
        File logDir = here.resolve(".ysnp").resolve("Logs").toFile();

        try {
            if (logDir.mkdirs()) {
                return;
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Could not create Logs directory.");
        }
        throw new RuntimeException("Could not create Logs directory.");

    }

    public void initialize() {
        deleteAllLogs();
        createDirectoryTree();
        setInitialized();
    }

    public void logError(Throwable error, int errors) {
        if (!isInitialized()) {
            try {
                initialize();
            }
            catch (Exception e) {
                System.err.println("Error during initialization.");
            }
        }
        Path logPath = FileSystems.getDefault().getPath(".").resolve(".ysnp").resolve("Logs");
        Path filePath = logPath.resolve(Integer.toString(errors) + ".txt");
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write("Message:\n");
            writer.write(error.toString() + "\n\n");
            writer.write("Stack trace:\n");
            writer.write(Arrays.stream(error.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n")));
        }
        catch (IOException e) {
            System.err.println("Failed to write Logs file.");
        }

    }
}
