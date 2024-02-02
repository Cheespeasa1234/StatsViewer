package mainpanel;

import main.Constants;
import player.Item;
import player.MinecraftPlayer;
import player.UsercachePlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MainPanel extends JPanel {

    File server;
    TopPanel topPanel;
    BottomPanel bottomPanel;

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
        bottomPanel = new BottomPanel();
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
        File playerDataDirectory = new File(server.getAbsolutePath() + "/.statsviewer/world/playerdata");
        File playerStatsDirectory = new File(server.getAbsolutePath() + "/.statsviewer/world/stats");
        System.out.println(playerDataDirectory.getAbsolutePath());
        System.out.println(playerStatsDirectory.getAbsolutePath());
        
        // get a list of cached user
        File usercacheFile = new File(server.getAbsolutePath() + "/usercache.json");
        List<UsercachePlayer> usercache = new ArrayList<>();
        if (usercacheFile.exists()) {
            try {
                Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
                BufferedReader br = new BufferedReader(new FileReader(usercacheFile));
                JsonArray usercacheJson = gson.fromJson(br, JsonArray.class);
                for (JsonElement usercacheElement : usercacheJson) {
                    usercache.add(gson.fromJson(usercacheElement, UsercachePlayer.class));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // create the player data
        File[] playerDataFiles = playerDataDirectory.listFiles();
        for (File playerFile : playerDataFiles) {
            File statsFile = new File(playerStatsDirectory.getAbsolutePath() + "/" + playerFile.getName().replace(".dat", ".json"));
            System.out.println("Reading stats file: " + statsFile.getAbsolutePath());
            try {
                Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

                MinecraftPlayer player = gson.fromJson(new BufferedReader(new FileReader(playerFile)), MinecraftPlayer.class);
                player.addStatsToMinecraftPlayer(statsFile, server);
                player.fixUUID();

                // get the name from the usercache
                for (UsercachePlayer usercachePlayer : usercache) {
                    if (usercachePlayer.UUID.equals(player.UUID)) {
                        player.name = usercachePlayer.name;
                    }
                }

                System.out.println("Created player: " + player.UUID + " with fileid: " + playerFile.getName());

                players.add(player);
                bottomPanel.players.add(player);
                bottomPanel.listModel.addElement(player.getName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        long dif = System.currentTimeMillis() - start;
        topPanel.statusLabel.setText("Loaded in " + ((double) dif / 1000) + "s.");

    }
}
