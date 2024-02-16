package util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 * This class contains various utility methods used throughout the program
 * It contains methods for setting the font of all components, converting NBT
 * files to JSON, and more
 * It also contains methods for copying files and folders, formatting numbers,
 * and more
 * 
 * @see main.StatsViewer
 * @see util.Globals
 * @see main.DependencyChecker
 * @see main.StatsViewer
 * @author Nate Levison, February 2024
 */
public class Utility {

    public static String formatEpoch(long epochMs) {
        Instant instant = Instant.ofEpochMilli(epochMs);
        String dateString = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(instant);
        return dateString;
    }

    public static String getSpecialLocation() {
        return Globals.STATS_VIEWER_DIRECTORY + "/" + Globals.OPEN_WORLD_NAME;
    }

    /**
     * Sets the font of all children, and their children, and so on, of all
     * elements, while preserving style
     * If an element has a font already, it just changes the name of the font, and
     * preserves the size and style
     * If an element has no font, it gets the whole font.
     * 
     * @param container
     * The swing component to modify
     * @param font
     * The font to enforce
     * @return void
     */
    public static void setFontRecursively(Container container, Font font) {
        for (Component component : container.getComponents()) {
            if (component instanceof Container) {
                setFontRecursively((Container) component, font);
            }
            if (component instanceof JComponent) {
                JComponent comp = (JComponent) component;
                Font f = comp.getFont();
                if (f == null) {
                    comp.setFont(font);
                } else {
                    comp.setFont(new Font(font.getName(), comp.getFont().getStyle(), comp.getFont().getSize()));
                }
            }
        }
    }

    /**
     * Executes a given command in a new process, and prints the stdout and stderr
     * 
     * @param args
     * The command to execute
     * @return void
     */
    public static void execute(String... args) {
        String joined = "";
        for (String arg : args)
            joined += arg + " ";

        String out = null;
        try {
            Process p = Runtime.getRuntime().exec(joined.trim());
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String stdout = "";
            while ((out = stdInput.readLine()) != null)
                stdout += out + "\n";

            String stderr = "";
            while ((out = stdError.readLine()) != null)
                stderr += out + "\n";

            if (stdout.length() != 0)
                System.out.println("stdout:\n" + stdout);
            if (stdout.length() != 0)
                System.out.println("stderr:\n" + stderr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the contents of a file into a string
     * 
     * @param filePath
     * The full path of the file to read
     * @return The contents of the file as a string
     */
    public static String fileToString(String filePath) {
        String out = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.FileInputStream(filePath)));
            String line;
            while ((line = reader.readLine()) != null) {
                out += line;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Formats a given double into a human-readable format
     * Used for the statistics tabs, to format very large numbers
     * 
     * @param d
     * The double to format
     * @return The formatted string
     */
    public static String doubleToString(double d) {
        DecimalFormat df = new DecimalFormat("#,###.##");
        return df.format(d);
    }

    /**
     * Copies the full contents of a folder to another folder
     * Recursively copies all files and subdirectories
     * 
     * @param source
     * The full path of the source folder
     * @param target
     * The full path of the target folder
     * @return void
     * @throws IOException
     * If the source or target folder cannot be accessed
     */
    public static void copyFolder(Path source, Path target) throws IOException {
        // Copy the folder and its contents recursively
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }

            @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Copies the given text to the system clipboard
     * 
     * @param text
     * The text to copy
     * @return void
     */
    public static void copyTextToClipboard(String text) {
        java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(text);
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static int getSecondsSince(String datetime) {
        LocalDateTime inputDateTime = LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime currentDateTime = LocalDateTime.now();
        return (int) ChronoUnit.SECONDS.between(inputDateTime, currentDateTime);
    }

    /**
     * Returns a human-readable string representing the time difference between the
     * input datetime and the current datetime
     * The datetime is as given in the advancements files, in the format "yyyy-MM-dd
     * HH:mm:ss -500"
     * 
     * @param datetime
     * The datetime to compare
     * @return The time difference as a string
     */
    public static String getTimeSince(String datetime) {
        // Parse the input datetime string
        LocalDateTime inputDateTime = LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Calculate the time difference
        LocalDateTime currentDateTime = LocalDateTime.now();
        long years = ChronoUnit.YEARS.between(inputDateTime, currentDateTime);
        long months = ChronoUnit.MONTHS.between(inputDateTime, currentDateTime);
        long days = ChronoUnit.DAYS.between(inputDateTime, currentDateTime);
        long hours = ChronoUnit.HOURS.between(inputDateTime, currentDateTime);
        long minutes = ChronoUnit.MINUTES.between(inputDateTime, currentDateTime);
        long seconds = ChronoUnit.SECONDS.between(inputDateTime, currentDateTime);

        // Choose the appropriate unit for the time difference
        if (years > 0) {
            return years + " year(s) ago";
        } else if (months > 0) {
            return months + " month(s) ago";
        } else if (days > 0) {
            return days + " day(s) ago";
        } else if (hours > 0) {
            return hours + " hour(s) ago";
        } else if (minutes > 0) {
            return minutes + " minute(s) ago";
        } else {
            return seconds + " second(s) ago";
        }
    }

    /**
     * Saves a list of directories to the recent directories file
     * Used to later display the recent directories in the file menu
     * 
     * @see readRecentDirectories
     * @param directories
     * The list of directories to save
     * @return void
     */
    public static void saveRecentDirectories(List<String> directories) {
        try {
            Path filePath = Paths.get(Globals.RECENTS_FILE_DIRECTORY);
            Files.write(filePath, directories);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }

    /**
     * Reads the recent directories from the recent directories file
     * Used to later display the recent directories in the file menu
     * 
     * @see saveRecentDirectories
     * @return The list of recent directories
     */
    public static List<String> readRecentDirectories() {
        List<String> directories = new ArrayList<>();
        try {
            Path filePath = Paths.get(Globals.RECENTS_FILE_DIRECTORY);
            if (Files.exists(filePath)) {
                directories = Files.readAllLines(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
        return directories;
    }

    /**
     * Adds a directory to the list of recent directories
     * Used to later display the recent directories in the file menu
     * 
     * @see readRecentDirectories
     * @see saveRecentDirectories
     * @param dir
     * The directory to add
     * @return void
     */
    public static void addRecent(String dir) {
        List<String> recent = readRecentDirectories();
        if (recent.contains(dir)) {
            recent.remove(dir);
        }
        recent.add(0, dir);
        if (recent.size() > 5) {
            recent.remove(5);
        }
        saveRecentDirectories(recent);
    }

}
