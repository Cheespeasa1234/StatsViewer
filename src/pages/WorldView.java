package pages;

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
import util.Globals;
import util.Lib;
import world.World;

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

	public JPanel createGenerationPanel(World world) {
		return null;
	}

	public JPanel createSummaryPanel(World world) {
		ListPanel summaryPanel = new ListPanel(Globals.PREF_W - 250, Globals.BOTTOM_HEIGHT - 100, ListPanel.NO_OPTIONS, 0);

		JLabel worldName = new JLabel("World: " + world.name);
		worldName.setFont(Globals.FONT_PRIMARY.deriveFont(20f));
		summaryPanel.addLabel(worldName);
		
		String[] difficulties = {"Peaceful", "Easy", "Normal", "Hard"};
		JLabel difficulty = new JLabel("Difficulty: " + difficulties[world.difficulty]);
		summaryPanel.addLabel(difficulty);
		String[] gameTypes = {"Survival", "Creative", "Adventure", "Spectator"};
		JLabel gameType = new JLabel("Gamemode: " + gameTypes[world.gameType]);
		summaryPanel.addLabel(gameType);
		JLabel time = new JLabel("Day: " + (world.time / 24000));
		summaryPanel.addLabel(time);
		JLabel version = new JLabel("Version: " + world.version.name + " " + world.version.id);
		summaryPanel.addLabel(version);
		JLabel seed = new JLabel("Seed: " + world.worldGenSettings.seed);
		summaryPanel.addLabel(seed);
		JPanel summaryContainer = new JPanel();
		summaryContainer.add(summaryPanel);

		return summaryContainer;
	}

	public JPanel createGameRulesPanel(World world) {
		ListPanel gamerulesPanel = new ListPanel(WIDTH, Globals.BOTTOM_HEIGHT - 100, ListPanel.ALL_AZ_OPTIONS, ListPanel.SORT_AZ);
		// for every gamerule, add a label to the panel
		for (Map.Entry<String, String> entry : world.gamerules.entrySet()) {
			JLabel label = new JLabel(entry.getKey() + ": " + entry.getValue());
			gamerulesPanel.addLabel(label);
		}

		JPanel gamerulesContainer = new JPanel();
		gamerulesContainer.add(gamerulesPanel);
		return gamerulesContainer;
	}

    public void setWorld(World world) {

        // reset everything
        this.world = world;
        tabs.removeAll();

		
		tabs.addTab("Summary", createSummaryPanel(world));
		tabs.addTab("Gamerules", createGameRulesPanel(world));
		tabs.addTab("Generation", createGenerationPanel(world));

        status.setText("Current World: " + world);
        status.setVisible(true);
    }
}
