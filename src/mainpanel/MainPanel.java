package mainpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import main.MinecraftPlayer;

public class MainPanel extends JPanel {

    File server;
    TopPanel topPanel;
    BottomPanel bottomPanel;

    int PREF_W, PREF_H, topHeight, bottomHeight;

    JPanel sideBar;

    MinecraftPlayer currentPlayer;
    ArrayList<MinecraftPlayer> players = new ArrayList<MinecraftPlayer>();

    void createTopPanel() {
        topPanel = new TopPanel(PREF_W, PREF_H, topHeight, () -> {
            this.load();
            topPanel.loadButton.setVisible(false);
        }, () -> {
            System.exit(0);
        });
    }

    void createBottomPanel() {
        
        bottomPanel = new BottomPanel((idx) -> {
            currentPlayer = players.get(idx);
            bottomPanel.playerViewer.setPlayer(currentPlayer);
        });

    }

    public MainPanel(int PREF_W, int PREF_H) {

        this.PREF_W = PREF_W;
        this.PREF_H = PREF_H;
        this.topHeight = 100;
        this.bottomHeight = PREF_H - topHeight * 2;

        createTopPanel();
        createBottomPanel();

        this.add(topPanel, BorderLayout.NORTH);
        this.add(bottomPanel, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(PREF_W, PREF_H));

    }

    public void setFile(File server) {
        this.server = server;
        topPanel.statusLabel.setText("Server @ " + server.getAbsolutePath() + "...");
    }

    public void load() {
        
        long start = System.currentTimeMillis();
        File statsDirectory = new File(server.getAbsolutePath() + "/world/stats");
        File[] files = statsDirectory.listFiles();
        for (File statsFile : files) {
            try {
                MinecraftPlayer player = new MinecraftPlayer(statsFile);
                players.add(player);
                bottomPanel.listModel.addElement(player.UUID);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        long dif = System.currentTimeMillis() - start;
        topPanel.statusLabel.setText("Loaded in " + ((double) dif / 1000) + "s.");
    }
}
