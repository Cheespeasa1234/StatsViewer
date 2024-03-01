package components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import main.DialogManager;
import util.Globals;
import world.Region;
import world.World;

/**
 * Wrapper class for the World object.
 * Displays the world's information in a tabbed pane.
 */
public class WorldView extends JPanel {

	private JLabel status;
	private JTabbedPane tabs;
	private WorldMapPanel worldMapPanel;
	private HashMap<String, Region> regions;

	private static final int WIDTH = Globals.PREF_W - 250;

	/**
	 * Create the WorldView. Everything is null, you need to call setWorld() to set the world.
	 * @see #setWorld(World)
	 */
	public WorldView() {
		regions = new HashMap<String, Region>();
		status = new JLabel("No world selected.");
		tabs = new JTabbedPane();
		this.add(status);
		this.add(tabs);
		this.setPreferredSize(new Dimension(Globals.PREF_W - 250, Globals.BOTTOM_HEIGHT));
	}

	/**
	 * Create the generation panel for the world.
	 * @param world The world to create the panel for.
	 * @return The JPanel containing the generation panel.
	 * @throws IOException
	 * @throws Exception
	 */
	private JPanel createGenerationPanel(World world) throws IOException, Exception {

		// Create the main container
		JPanel generationPanel = new JPanel(new BorderLayout());

		// Create the fancy map display, and the list of regions
		File[] regionFiles = world.regionFiles;
		worldMapPanel = new WorldMapPanel();
		worldMapPanel.setPreferredSize(new Dimension(933, 512));
		JScrollPane scrollPane = new JScrollPane(worldMapPanel);

		// Create the AWT list component stuff
		DefaultListModel<String> regionListModel = new DefaultListModel<>();
		JList<String> regionList = new JList<>(regionListModel);

		// Add a listener to the list so that when a region is selected, the mapPanel switches
		regionList.getSelectionModel().addListSelectionListener(e -> {
			ListSelectionModel lsm = ((ListSelectionModel) e.getSource());
			if (lsm.isSelectionEmpty() || lsm.getValueIsAdjusting()) {
				return;
			}

			// if something is also being loaded
			if (DialogManager.isOpen()) {
				return;
			}

			// get the selected region
			int idx = lsm.getMinSelectionIndex();
			String regionName = regionListModel.getElementAt(idx);

			// If this region has already been parsed
			if (regions.containsKey(regionName)) {
				Region region = regions.get(regionName);
				worldMapPanel.setRegion(region);
				worldMapPanel.repaint();
			
			// It has not been parsed, so do that
			} else {
				new Thread(() -> {
					try {

						// Parse and set a new region
						Region region = new Region(new File(
								Globals.SERVER_DIRECTORY + "/" + Globals.OPEN_WORLD_NAME + "/region/" + regionName));
						while (!region.isFinished()) {
							region.consumeChunk();
							SwingUtilities.invokeLater(() -> {
								DialogManager.setCount(region.getChunkConsumed());
							});
						}
						regions.put(regionName, region);

						DialogManager.close();
						System.out.println("Region finished: " + region);

						// Set the panel to display it
						worldMapPanel.setRegion(region);
						worldMapPanel.repaint();

						repaint();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}).start();
			}
		});

		for (File region : regionFiles) {
			regionListModel.addElement(region.getName());
		}

		generationPanel.add(regionList, BorderLayout.WEST);
		generationPanel.add(scrollPane, BorderLayout.CENTER);
		return generationPanel;
	}

	/**
	 * Create the summary panel for the world.
	 * @param world The world to create the panel for.
	 * @return The JPanel containing the summary panel.
	 */
	private JPanel createSummaryPanel(World world) {
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

	/**
	 * Create the gamerules panel for the world.
	 * @param world The world to create the panel for.
	 * @return The JPanel containing the gamerules panel.
	 */
	private JPanel createGameRulesPanel(World world) {
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

	/**
	 * Set the world to display. Basically the constructor, but it also lets you switch worlds.
	 * @param world The world to display.
	 * @throws IOException
	 * @throws Exception
	 */
	public void setWorld(World world) throws IOException, Exception {

		// reset everything
		tabs.removeAll();

		tabs.addTab("Summary", createSummaryPanel(world));
		tabs.addTab("Gamerules", createGameRulesPanel(world));
		tabs.addTab("Generation", createGenerationPanel(world));

		status.setText("Current World: " + world);
		status.setVisible(true);
	}
}
