package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import pages.BlankPanel;
import pages.MainPanel;

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
 * @see Lib
 * @version 1.0
 * @since 1.0
 * @author Nate Levison, February 2024
 */
public class StatsViewer extends JPanel implements KeyListener {

    File serverDirectory = null;

    ArrayList<JPanel> pages = new ArrayList<JPanel>();
    BlankPanel blankPanel;
    MainPanel statsPanel;
    DefaultListModel<String> listModel;

    private void convertFiles(File dir) {

        // parse the level.dat
        Path inFile = Paths.get(dir + "/" + Globals.OPEN_WORLD_NAME + "/level.dat");
        Path outFile = Paths.get(dir + Lib.getLocation() + "/level.json");
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

        Lib.convertNBT(
                inFile.toAbsolutePath().toString(),
                outFile.toAbsolutePath().toString());

        // parse the playerdata folder
        inFile = Paths.get(dir + "/" + Globals.OPEN_WORLD_NAME + "/playerdata");
        outFile = Paths.get(dir + Lib.getLocation() + "/playerdata");
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
            Lib.convertNBT(
                    inFilePath.toAbsolutePath().toString(),
                    outFilePath.toAbsolutePath().toString());

            System.out.println(inFilePath.toAbsolutePath().toString());
            System.out.println(outFilePath.toAbsolutePath().toString());
        }

        // make a copy of the stats files
        inFile = Paths.get(dir + "/" + Globals.OPEN_WORLD_NAME + "/stats");
        outFile = Paths.get(dir + Lib.getLocation() + "/stats");
        try {
            Lib.copyFolder(inFile, outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // make a copy of the advancements files
        inFile = Paths.get(dir + "/" + Globals.OPEN_WORLD_NAME + "/advancements");
        outFile = Paths.get(dir + Lib.getLocation() + "/advancements");
        try {
            Lib.copyFolder(inFile, outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

        Lib.setFontRecursively(this, Globals.FONT_PRIMARY);
        setPage(0);

    }

    public StatsViewer() {

        this.setFocusable(true);
        this.setBackground(Color.WHITE);
        this.addKeyListener(this);

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

        createPages();
    }

    private void addPage(JPanel page) {
        pages.add(page);
        page.setVisible(false);
        Lib.setFontRecursively(page, Globals.FONT_PRIMARY);
        this.add(page);
    }

    private void setPage(int idx) {
        for (int i = 0; i < pages.size(); i++) {
            if (i == idx)
                pages.get(i).setVisible(true);
            else
                pages.get(i).setVisible(false);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
