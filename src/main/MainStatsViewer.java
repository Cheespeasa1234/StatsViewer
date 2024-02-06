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

public class MainStatsViewer extends JPanel implements KeyListener {

    File serverDirectory = null;

    ArrayList<JPanel> pages = new ArrayList<JPanel>();
    BlankPanel blankPanel;
    MainPanel statsPanel;
    DefaultListModel<String> listModel;

    void convertFiles(File dir) {

        // parse the level.dat
        Path inFile = Paths.get(dir + Globals.STATS_VIEWER_DIRECTORY + Globals.worldName + "/level.dat");
        Path outFile = Paths.get(dir + Globals.STATS_VIEWER_DIRECTORY + Globals.worldName + "/level.json");
        if (!Files.exists(outFile)) {
            try {
                Files.createDirectories(outFile.getParent());
                Files.createFile(outFile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        Lib.execute(
                Globals.PYTHON_INSTANCE,
                "src/de-nbt.py",
                inFile.toAbsolutePath().toString(),
                outFile.toAbsolutePath().toString());

        // parse the playerdata folder
        inFile = Paths.get(dir + "/" + Globals.worldName + "/playerdata");
        outFile = Paths.get(dir + Globals.STATS_VIEWER_DIRECTORY + Globals.worldName + "/playerdata");
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
            Path outFilePath = Paths.get(outFile.toAbsolutePath().toString() + "/" + file.getName().replace(".dat", ".json"));
            if (!Files.exists(outFilePath)) {
                try {
                    Files.createFile(outFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            Lib.execute(
                    Globals.PYTHON_INSTANCE,
                    "src/de-nbt.py",
                    inFilePath.toAbsolutePath().toString(),
                    outFilePath.toAbsolutePath().toString());

            System.out.println(inFilePath.toAbsolutePath().toString());
            System.out.println(outFilePath.toAbsolutePath().toString());
        }


        // make a copy of the stats files
        inFile = Paths.get(dir + "/" + Globals.worldName + "/stats");
        outFile = Paths.get(dir + Globals.STATS_VIEWER_DIRECTORY + Globals.worldName + "/stats");
        try {
            Lib.copyFolder(inFile, outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // make a copy of the advancements files
        inFile = Paths.get(dir + "/" + Globals.worldName + "/advancements");
        outFile = Paths.get(dir + Globals.STATS_VIEWER_DIRECTORY + Globals.worldName + "/advancements");
        try {
            Lib.copyFolder(inFile, outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void createPages() {

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

    public MainStatsViewer() {

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

    void addPage(JPanel page) {
        pages.add(page);
        page.setVisible(false);
        Lib.setFontRecursively(page, Globals.FONT_PRIMARY);
        this.add(page);
    }

    void setPage(int idx) {
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

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Minecraft Statistics Viewer");
        JPanel gamePanel = new MainStatsViewer();

        setApplicationIcon(frame);

        frame.getContentPane().add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void setApplicationIcon(JFrame frame) {
        // Load the icon image from a file or resource
        ImageIcon icon = createImageIcon("icon.png");

        // Set the icon for the JFrame
        if (icon != null) {
            System.out.println("Icon set");
            frame.setIconImage(icon.getImage());
        }
    }

    private static ImageIcon createImageIcon(String path) {
        URL imageURL = MainStatsViewer.class.getResource(path);
        if (imageURL != null) {
            System.out.println(imageURL.toString());
            return new ImageIcon(imageURL);
        } else {
            System.err.println("Resource not found: " + path);
            return null;
        }
    }

    public static void main(String[] args) {

        DependencyChecker.checkDependencies();

        System.out.println(Globals.PREF_W + " " + Globals.PREF_H);
        System.out.println(Globals.TOP_HEIGHT + " " + Globals.BOTTOM_HEIGHT);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
