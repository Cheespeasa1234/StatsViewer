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


public class StatsViewer extends JPanel implements KeyListener {

    public static final int PREF_W = 800;
    public static final int PREF_H = 600;

    File serverDirectory = null;

    ArrayList<JPanel> pages = new ArrayList<JPanel>();
    BlankPanel blankPanel;
    MainPanel mainPanel;
    DefaultListModel<String> listModel;

    Font font = new Font("Helvetica", Font.PLAIN, 16);

    void convertFiles(File dir) {

        // parse the level.json
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

        // convert out of NBT data
        Lib.getInstance().execute(
                "python3",
                "src/de-nbt.py",
                inFile.toAbsolutePath().toString(),
                outFile.toAbsolutePath().toString());

    }

    void createPages() {

        // Set the choosing panel
        blankPanel = new BlankPanel(this.font, PREF_W, PREF_H, file -> {
            convertFiles(file);
            mainPanel.setFile(file);
            setPage(1);
        });
        
        // Set the main panel
        mainPanel = new MainPanel(PREF_W, PREF_H);
        
        addPage(blankPanel);
        addPage(mainPanel);

        Lib.getInstance().setFontRecursively(this, this.font);
        setPage(0);

    }

    public StatsViewer() {

        this.setFocusable(true);
        this.setBackground(Color.WHITE);
        this.addKeyListener(this);

        Constants.PREF_W = PREF_W;
        Constants.PREF_H = PREF_H;


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
        Lib.getInstance().setFontRecursively(page, this.font);
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
        return new Dimension(PREF_W, PREF_H);
    }

    public static void createAndShowGUI() {
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
