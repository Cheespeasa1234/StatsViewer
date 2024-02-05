package main;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;

public class Lib {

    /**
     * Sets the font of all children, and their children, and so on, of all elements, while preserving style
     * If an element has a font already, it just changes the name of the font, and preserves the size and style
     * If an element has no font, it gets the whole font.
     * @param container The swing component to modify
     * @param font The font to enforce
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
     * Splits a string into tokens, based on how JSON is formatted.
     * Splits by all commas, and colons only if the colon isn't in between quotes.
     * Examples: <ol>
        * <li><code>splitPressedJSON("\"minecraft:cobblestone\":5) = ["\"minecraft: cobblestone\"", "5"]</code></li>
        * <li><code>splitPressedJSON("\"minecraft:cobblestone\":5,"\"minecraft:ender_pearl\":18923, hello") = ["\"minecraft: cobblestone\"", "5", "\"minecraft:ender_pearl\"", "18923", "hello"]</code></li>
        * <li><code>splitPressedJSON("\"minecraft:cobblestone\":5, hello") = ["\"minecraft: cobblestone\"", "5", "hello"]</code></li>
     * </ol>
     */
    public static List<String> splitPressedJSON(String input) {
        List<String> result = new ArrayList<String>();

        Pattern pattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String match = matcher.group();
            if (!match.isEmpty()) {
                // if the match has a comma, get rid of that
                if (match.charAt(match.length() - 1) == ',') result.add(match.substring(0, match.length() - 1));
                else result.add(match);
            }
        }

        return result;
    }

    /**
     * Takes in a .dat file in NBT format, and converts it to a JSON file.
     * Uses the system's python instance to convert.
     * @param fileIn The full path of the file to convert
     * @param fileOut The full path of the location to put the new file
     * @return void
     */
    public static void convertNBT(String fileIn, String fileOut) {
        Lib.execute(
            Globals.PYTHON_INSTANCE,
            "src/format-stat.py",
            "-I",
            fileIn,
            fileOut
        );
    }

    public static void execute(String... args) {
        String joined = "";
        for (String arg : args) joined += arg + " ";

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

            if (stdout.length() != 0) System.out.println("stdout:\n" + stdout);
            if (stdout.length() != 0) System.out.println("stderr:\n" + stderr);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

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

    public static String doubleToString(double d) {
        DecimalFormat df = new DecimalFormat("#,###.##");
        return df.format(d);
    }

    public static void copyFolder(Path source, Path target) throws IOException {
        // Copy the folder and its contents recursively
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void copyTextToClipboard(String text) {
        java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(text);
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

}
