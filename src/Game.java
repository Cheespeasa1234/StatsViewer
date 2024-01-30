import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;

public class Game extends JPanel implements KeyListener {

    public static final int PREF_W = 800;
    public static final int PREF_H = 600;
    
    File serverDirectory = null;
    ArrayList<JPanel> pages = new ArrayList<JPanel>();

    public Game() {

        this.setFocusable(true);
        this.setBackground(Color.WHITE);
        this.addKeyListener(this);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        BlankPanel.FileChosenListener fileChosenListener = file -> { 
            serverDirectory = file;
        };

        BlankPanel blankPanel = new BlankPanel(fileChosenListener);
        this.add(blankPanel);
    
    }

    void addPage(JPanel page) {
        pages.add(page);
        page.setVisible(false);
        this.add(page);
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
        JPanel gamePanel = new Game();

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
