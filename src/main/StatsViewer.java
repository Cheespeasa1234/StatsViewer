package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import util.AssetGlobals;
import util.DataParsing;
import util.Globals;
import util.Utility;

/**
 * The main class for the Minecraft Statistics Viewer
 * It also contains the methods for creating the JFrame and JPanel for the
 * program
 * Keeps track of open directory, pages, and the list model
 * 
 * @see DependencyChecker
 * @see BlankPanel
 * @see MainPanel
 * @see Globals
 * @see Utility
 * @version 1.0
 * @since 1.0
 * @author Nate Levison, February 2024
 */
public class StatsViewer extends JPanel implements KeyListener {

    private ArrayList<JPanel> pages = new ArrayList<JPanel>(); // list of pages
    private BlankPanel blankPanel; // the panel for choosing the directory
    private MainPanel statsPanel; // the panel for viewing the statistics

    /**
     * Converts the files in the given directory to JSON format
     * 
     * @param dir the directory to convert
     * @throws IllegalArgumentException if the file is null, does not exist, or is not a directory
     * @throws IllegalArgumentException if there is an error creating the file
     * @throws IllegalArgumentException if the file is null
     */
    private void convertFiles(File dir) {

        if (dir == null) {
            throw new IllegalArgumentException("File cannot be null");
        } else if (!dir.exists()) {
            throw new IllegalArgumentException("File must exist");
        } else if (!dir.isDirectory()) {
            throw new IllegalArgumentException("File must be a directory");
        }

        // parse the level.dat
        Path inFile = Paths.get(dir + "/" + Globals.OPEN_WORLD_NAME + "/level.dat");
        Path outFile = Paths.get(dir + Utility.getSpecialLocation() + "/level.json");
        System.out.println("Trying to convert level.dat to level.json");
        System.out.println("inFile: " + inFile.toAbsolutePath().toString());
        System.out.println("outFile: " + outFile.toAbsolutePath().toString());
        if (!Files.exists(outFile)) {
            try {
                Files.createDirectories(outFile.getParent());
                Files.createFile(outFile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        DataParsing.convertNBT(
                inFile.toAbsolutePath().toString(),
                outFile.toAbsolutePath().toString());

        // parse the playerdata folder
        inFile = Paths.get(dir + "/" + Globals.OPEN_WORLD_NAME + "/playerdata");
        outFile = Paths.get(dir + Utility.getSpecialLocation() + "/playerdata");
        if (!Files.exists(outFile)) {
            try {
                Files.createDirectories(outFile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // get every file in the playerdata folder that ends with .dat
        File[] allFiles = inFile.toFile().listFiles();
        ArrayList<File> files = new ArrayList<File>();
        for (File file : allFiles) {
            if (file.getName().endsWith(".dat")) {
                files.add(file);
            }
        }

        for (File file : files) {
            Path inFilePath = file.toPath();
            Path outFilePath = Paths
                    .get(outFile.toAbsolutePath().toString() + "/" + file.getName().replace(".dat", ".json"));
            if (!Files.exists(outFilePath)) {
                try {
                    Files.createFile(outFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            DataParsing.convertNBT(
                    inFilePath.toAbsolutePath().toString(),
                    outFilePath.toAbsolutePath().toString());

            System.out.println(inFilePath.toAbsolutePath().toString());
            System.out.println(outFilePath.toAbsolutePath().toString());
        }

        // make a copy of the stats files
        inFile = Paths.get(dir + "/" + Globals.OPEN_WORLD_NAME + "/stats");
        outFile = Paths.get(dir + Utility.getSpecialLocation() + "/stats");
        try {
            Utility.copyFolder(inFile, outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // make a copy of the advancements files
        inFile = Paths.get(dir + "/" + Globals.OPEN_WORLD_NAME + "/advancements");
        outFile = Paths.get(dir + Utility.getSpecialLocation() + "/advancements");
        try {
            Utility.copyFolder(inFile, outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the pages for the program, and makes them visible
     * Also enforces fonts and the look and feel
     */
    private void createPages() {

        // Set the choosing panel
        blankPanel = new BlankPanel(Globals.FONT_PRIMARY, file -> {
            System.out.println("File chosen: " + file.getAbsolutePath());
            convertFiles(file);
            statsPanel.setFile(file);
            setPage(1);
        });

        // Set the main panel
        statsPanel = new MainPanel();

        addPage(blankPanel);
        addPage(statsPanel);

        Utility.setFontRecursively(this, Globals.FONT_PRIMARY);
        setPage(0);

    }

    /**
     * Constructor for the StatsViewer class
     * Sets UI look and feel, creates pages, and adds key listener
     */
    public StatsViewer() {

        this.setFocusable(true);
        this.setBackground(Color.WHITE);
        this.addKeyListener(this);

        // Set the look and feel
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Set the recents file
        File recentsFile = new File(Globals.RECENTS_FILE_DIRECTORY);
        if (!recentsFile.exists()) {
            try {
                recentsFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed to open recents file.");
                e.printStackTrace();
            }
        }

        // set the icon assets
        File structureIcons = new File("src/assets/icons/struct/minecraft");
        for (File icon : structureIcons.listFiles()) {
            AssetGlobals.structureIcons.put("minecraft:" + icon.getName().replace(".png", ""),
                    new ImageIcon(icon.getAbsolutePath()));
        }

        createPages();
    }

    /**
     * Adds a page to the list of pages
     * 
     * @param page the page to add
     * @throws IllegalArgumentException if the page is null
     * @throws IllegalArgumentException if the page is already in the list
     */
    private void addPage(JPanel page) {
        if (page == null) {
            throw new IllegalArgumentException("Page cannot be null");
        } else if (pages.contains(page)) {
            throw new IllegalArgumentException("Page already in the list");
        }

        pages.add(page);
        page.setVisible(false);
        Utility.setFontRecursively(page, Globals.FONT_PRIMARY);
        this.add(page);
    }

    /**
     * Sets the page at the given index to be visible
     * 
     * @param idx the index of the page to set visible
     * @throws IllegalArgumentException if the index is out of bounds
     */
    private void setPage(int idx) {
        if (idx < 0 || idx >= pages.size()) {
            throw new IllegalArgumentException("Index out of bounds");
        }

        for (int i = 0; i < pages.size(); i++) {
            if (i == idx)
                pages.get(i).setVisible(true);
            else
                pages.get(i).setVisible(false);
        }
    }

    @Override public void keyPressed(KeyEvent e) {}

    @Override public void keyReleased(KeyEvent e) {}

    @Override public void keyTyped(KeyEvent e) {}

    /* METHODS FOR CREATING JFRAME AND JPANEL */

    public Dimension getPreferredSize() {
        return new Dimension(Globals.PREF_W, Globals.PREF_H);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Minecraft Statistics Viewer");
        JPanel gamePanel = new StatsViewer();

        frame.getContentPane().add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException, Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
