package components;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;

import player.MinecraftPlayer;
import util.Globals;
import world.RegionParser;
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

	public JPanel createGenerationPanel(World world) throws IOException, Exception {
		JPanel generationPanel = new JPanel(new BorderLayout());

		File[] regions = world.regionFiles;

		WorldMapPanel worldMapPanel = new WorldMapPanel(world, regions[0].getName());
		JScrollPane scrollPane = new JScrollPane(worldMapPanel);
		
		DefaultListModel<String> regionListModel = new DefaultListModel<>();
		JList<String> regionList = new JList<>(regionListModel);
		regionList.getSelectionModel().addListSelectionListener(e -> {
            ListSelectionModel lsm = ((ListSelectionModel) e.getSource());
            if (lsm.isSelectionEmpty() || lsm.getValueIsAdjusting()) {
                return;
            }
            
			// get the selected region
			int idx = lsm.getMinSelectionIndex();
			RegionParser region = world.regions[idx];
			File f = region.fileConsumed;

			// set the world map panel to the selected region
			try {
				worldMapPanel.reset(world, f.getName());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        });

		for (File region : regions) {
			regionListModel.addElement(region.getName());
		}

		generationPanel.add(regionList, BorderLayout.WEST);

		generationPanel.add(scrollPane, BorderLayout.CENTER);
		return generationPanel;
	}

	public JPanel createSummaryPanel(World world) {
		ListPanel summaryPanel = new ListPanel(Globals.PREF_W - 250, Globals.BOTTOM_HEIGHT - 100, ListPanel.NO_OPTIONS,
				0);

		JLabel worldName = new JLabel("World: " + world.name);
		worldName.setFont(Globals.FONT_PRIMARY.deriveFont(20f));
		summaryPanel.addLabel(worldName);

		String[] difficulties = {
				"Peaceful", "Easy", "Normal", "Hard"
		};
		JLabel difficulty = new JLabel("Difficulty: " + difficulties[world.difficulty]);
		summaryPanel.addLabel(difficulty);
		String[] gameTypes = {
				"Survival", "Creative", "Adventure", "Spectator"
		};
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
		ListPanel gamerulesPanel = new ListPanel(WIDTH, Globals.BOTTOM_HEIGHT - 100, ListPanel.ALL_AZ_OPTIONS,
				ListPanel.SORT_AZ);
		// for every gamerule, add a label to the panel
		for (Map.Entry<String, String> entry : world.gamerules.entrySet()) {
			JLabel label = new JLabel(entry.getKey() + ": " + entry.getValue());
			gamerulesPanel.addLabel(label);
		}

		JPanel gamerulesContainer = new JPanel();
		gamerulesContainer.add(gamerulesPanel);
		return gamerulesContainer;
	}

	public void setWorld(World world) throws IOException, Exception {

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
