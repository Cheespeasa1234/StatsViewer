package mainpanel;

import main.Constants;

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

    JPanel sideBar;

    MinecraftPlayer currentPlayer;
    ArrayList<MinecraftPlayer> players = new ArrayList<MinecraftPlayer>();

    void createTopPanel() {
        topPanel = new TopPanel(Constants.TOP_HEIGHT, () -> {
            this.load();
            topPanel.loadButton.setVisible(false);
        }, () -> {
            System.exit(0);
        });
    }

    void createBottomPanel() {
        bottomPanel = new BottomPanel(players);
    }

    public MainPanel() {

        createTopPanel();
        createBottomPanel();

        this.add(topPanel, BorderLayout.NORTH);
        this.add(bottomPanel, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(Constants.PREF_W, Constants.PREF_H));

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
