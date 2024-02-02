package main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import blankpanel.BlankPanel;
import mainpanel.MainPanel;


public class MainStatsViewer extends JPanel implements KeyListener {

    File serverDirectory = null;

    ArrayList<JPanel> pages = new ArrayList<JPanel>();
    BlankPanel blankPanel;
    MainPanel mainPanel;
    DefaultListModel<String> listModel;

    

    void convertFiles(File dir) {

        // parse the level.dat
        Path inFile = Paths.get(dir + "/world/level.dat");
        Path outFile = Paths.get(dir + "/.statsviewer/world/level.json");
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
                "python3",
                "src/de-nbt.py",
                inFile.toAbsolutePath().toString(),
                outFile.toAbsolutePath().toString());

        // parse the playerdata folder
        inFile = Paths.get(dir + "/world/playerdata");
        outFile = Paths.get(dir + "/.statsviewer/world/playerdata");
        if (!Files.exists(outFile)) {
            try {
                Files.createDirectories(outFile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // get every file in the playerdata folder that ends with .dat
        File[] files = inFile.toFile().listFiles((dir1, name) -> name.endsWith(".dat"));
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
                    "python3",
                    "src/de-nbt.py",
                    inFilePath.toAbsolutePath().toString(),
                    outFilePath.toAbsolutePath().toString());
        }

    }

    void createPages() {

        // Set the choosing panel
        blankPanel = new BlankPanel(Constants.FONT_PRIMARY, file -> {
            convertFiles(file);
            mainPanel.setFile(file);
            setPage(1);
        });
        
        // Set the main panel
        mainPanel = new MainPanel();
        
        addPage(blankPanel);
        addPage(mainPanel);

        Lib.setFontRecursively(this, Constants.FONT_PRIMARY);
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
        Lib.setFontRecursively(page, Constants.FONT_PRIMARY);
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
        return new Dimension(Constants.PREF_W, Constants.PREF_H);
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Minecraft Statistics Viewer");
        JPanel gamePanel = new MainStatsViewer();

        frame.getContentPane().add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println(Constants.PREF_W + " " + Constants.PREF_H);
        System.out.println(Constants.TOP_HEIGHT + " " + Constants.BOTTOM_HEIGHT);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
