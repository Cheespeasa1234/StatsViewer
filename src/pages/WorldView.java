package pages;

import main.Globals;
import main.Lib;
import main.ListPanel;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import player.Advancement;
import player.Item;
import player.MinecraftPlayer;
import player.World;

public class WorldView extends JPanel {
    JLabel status;
    JTabbedPane tabs;
    World world;

    private final int WIDTH = Globals.PREF_W - 250;

    public WorldView() {
        status = new JLabel("No world selected.");
        tabs = new JTabbedPane();
        this.add(status);
        this.add(tabs);
        this.setPreferredSize(new Dimension(Globals.PREF_W - 250, Globals.BOTTOM_HEIGHT));
    }

    public void setWorld(World world) {

        // reset everything
        this.world = world;
        tabs.removeAll();

		JPanel summaryPanel = new JPanel();
		summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
		summaryPanel.setPreferredSize(new Dimension(WIDTH, Globals.BOTTOM_HEIGHT - 100));

		JLabel worldName = new JLabel("World: " + world.name);
		worldName.setFont(Globals.FONT_PRIMARY.deriveFont(20f));
		summaryPanel.add(worldName);

		String[] difficulties = {"Peaceful", "Easy", "Normal", "Hard"};
		JLabel difficulty = new JLabel("Difficulty: " + difficulties[world.difficulty]);
		summaryPanel.add(difficulty);
		
		String[] gameTypes = {"Survival", "Creative", "Adventure", "Spectator"};
		JLabel gameType = new JLabel("Gamemode: " + gameTypes[world.gameType]);
		summaryPanel.add(gameType);

		JLabel time = new JLabel("Day: " + (world.time / 24000));
		summaryPanel.add(time);

		JLabel version = new JLabel("Version: " + world.version.name + " " + world.version.id);
		summaryPanel.add(version);

		tabs.addTab("Summary", summaryPanel);

        status.setText("Current World: " + world);
        status.setVisible(true);
    }
}
