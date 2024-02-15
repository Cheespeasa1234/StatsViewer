package pages;

import main.Globals;
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
import util.Lib;

/**
 * The WorldView class is used to display information about a specific world.
 * It contains methods for creating panels to display information about the
 * world.
 * 
 * @see MinecraftPlayer
 * @see ListPanel
 * @see Globals
 * @see Lib
 * @author Nate Levison, February 2024
 */
public class PlayerView extends JPanel {
	private JLabel status;
	private JTabbedPane statsGroups;
	private MinecraftPlayer player = null;

	private final int WIDTH = Globals.PREF_W - 250;

	/*
	 * Constructor for the PlayerView class.
	 * This class is used to display information about a specific player.
	 */
	public PlayerView() {
		status = new JLabel("No player selected.");
		statsGroups = new JTabbedPane();
		this.add(status);
		this.add(statsGroups);
		this.setPreferredSize(new Dimension(Globals.PREF_W - 250, Globals.BOTTOM_HEIGHT));
	}

	/**
	 * Creates a summary panel displaying basic information about the player.
	 *
	 * @param player
	 *            The Minecraft player whose information needs to be displayed.
	 * @return JPanel containing player summary information.
	 */
	private JPanel createSummaryPanel(MinecraftPlayer player) {
		ListPanel summaryPanel = new ListPanel(WIDTH, Globals.BOTTOM_HEIGHT - 100, ListPanel.NO_OPTIONS, 0);
		String name = "Name: " + player.name;
		String uuid = "UUID: " + player.UUID;
		String position = "Position: " + player.position[0] + ", " + player.position[1] + ", "
				+ player.position[2] + " in " + player.dimension;
		String spawn = "Spawn: " + player.spawnX + ", " + player.spawnY + ", " + player.spawnZ;

		JLabel playerInfoTitle = new JLabel("Player Information");
		playerInfoTitle.setFont(Globals.FONT_PRIMARY.deriveFont(24f));
		summaryPanel.addLabel(playerInfoTitle);
		summaryPanel.addLabel(name);
		summaryPanel.addLabel(uuid);
		summaryPanel.addLabel(position);
		summaryPanel.addLabel(spawn);
		summaryPanel.addSpacer();

		JLabel playerStatusTitle = new JLabel("Status Information");
		playerStatusTitle.setFont(Globals.FONT_PRIMARY.deriveFont(24f));
		summaryPanel.addLabel(playerStatusTitle);
		summaryPanel.addLabel("Operator: " + player.op);
		summaryPanel.addLabel("Whitelisted: " + player.whitelisted);
		summaryPanel.addLabel("Banned: " + player.banned);
		JPanel mainPanel = new JPanel();
		mainPanel.add(summaryPanel);
		return mainPanel;
	}

	/**
	 * Creates a panel displaying advancements achieved by the player.
	 *
	 * @param player
	 *            The Minecraft player whose advancements need to be displayed.
	 * @return JPanel containing player advancements information.
	 */
	private JPanel createAdvancementsPanel(MinecraftPlayer player) {
		ListPanel panel = new ListPanel(
				WIDTH, Globals.BOTTOM_HEIGHT - 100,
				ListPanel.ALL_OPTIONS_EXCEPT_SLOT, ListPanel.SORT_AZ);
		String[] advancementNames = player.advancements.keySet().toArray(new String[0]);
		Arrays.sort(advancementNames);
		for (String advancement : advancementNames) {
			Advancement a = player.advancements.get(advancement);
			if (a.done) {
				panel.addLabel(advancement + ": " + Lib.getTimeSince(a.getCompleted()),
						Lib.getSecondsSince(a.getCompleted()));
			}
		}
		JPanel mainPanel = new JPanel();
		mainPanel.add(panel);
		return mainPanel;
	}

	/**
	 * Creates a panel displaying the inventory of the player.
	 *
	 * @param player
	 *            The Minecraft player whose inventory needs to be displayed.
	 * @return JPanel containing player inventory information.
	 */
	private JPanel createInventoryPanel(MinecraftPlayer player) {
		JPanel inventoryPanel = new JPanel();
		JPanel mainInventoryContainer = new JPanel();
		JPanel enderInventoryContainer = new JPanel();
		mainInventoryContainer.setLayout(new BoxLayout(mainInventoryContainer, BoxLayout.Y_AXIS));
		enderInventoryContainer.setLayout(new BoxLayout(enderInventoryContainer, BoxLayout.Y_AXIS));
		ListPanel mainInventoryPanel = new ListPanel(WIDTH / 2 - 25, Globals.BOTTOM_HEIGHT - 100,
				ListPanel.ALL_OPTIONS, ListPanel.SORT_SLOT_MOST);
		ListPanel enderInventoryPanel = new ListPanel(WIDTH / 2 - 25, Globals.BOTTOM_HEIGHT - 100,
				ListPanel.ALL_OPTIONS, ListPanel.SORT_SLOT_MOST);
		JLabel mainInventoryLabel = new JLabel("Main Inventory");
		mainInventoryLabel.setFont(Globals.FONT_PRIMARY.deriveFont(24f));
		mainInventoryContainer.add(mainInventoryLabel);
		JLabel enderInventoryLabel = new JLabel("Ender Inventory");
		enderInventoryLabel.setFont(Globals.FONT_PRIMARY.deriveFont(24f));
		enderInventoryContainer.add(enderInventoryLabel);

		if (player.mainInventory.size() == 0)
			mainInventoryPanel.addLabel("No items in main inventory", 0);
		else
			for (Item item : player.mainInventory) {
				String[] lines = item.toFancyString();
				for (String line : lines)
					mainInventoryPanel.addLabel(line, item.count, item.slot);
			}
		if (player.enderInventory.size() == 0)
			enderInventoryPanel.addLabel("No items in ender inventory", 0);
		else
			for (Item item : player.enderInventory) {
				String[] lines = item.toFancyString();
				for (String line : lines)
					enderInventoryPanel.addLabel(line, item.count, item.slot);
			}
		mainInventoryContainer.add(mainInventoryPanel);
		enderInventoryContainer.add(enderInventoryPanel);
		inventoryPanel.add(mainInventoryContainer);
		inventoryPanel.add(enderInventoryContainer);
		return inventoryPanel;
	}

	/**
	 * Creates a tab displaying statistics of a specific type for the player.
	 *
	 * @param tabName
	 *            The name of the statistics tab.
	 * @return JPanel containing statistics information.
	 */
	private JPanel createStatsTab(String tabName) {
		int size = player.stats.get(tabName).size();
		if (size == 0)
			return null;

		ListPanel scrollableLabelPanel = new ListPanel(WIDTH, Globals.BOTTOM_HEIGHT - 100);

		// make the layout so everything is on a new line
		List<HashMap.Entry<String, Double>> entries = new ArrayList<HashMap.Entry<String, Double>>(
				player.stats.get(tabName).entrySet());
		entries.sort(Map.Entry.<String, Double>comparingByValue().reversed());
		for (HashMap.Entry<String, Double> entry : entries) {
			scrollableLabelPanel.addLabel(entry.getKey() + ": " + Lib.doubleToString(entry.getValue()),
					entry.getValue());
		}

		JPanel mainPanel = new JPanel();
		mainPanel.add(scrollableLabelPanel);
		return mainPanel;
	}

	/**
	 * Sets the player whose information needs to be displayed.
	 * This method updates the view with the player's details.
	 *
	 * @param player
	 *            The Minecraft player to be displayed.
	 */
	public void setPlayer(MinecraftPlayer player) {

		// reset everything
		this.player = player;
		statsGroups.removeAll();

		// make the inventory tab
		statsGroups.addTab("Summary", createSummaryPanel(player));
		statsGroups.addTab("Inventory", createInventoryPanel(player));
		statsGroups.addTab("Advancements", createAdvancementsPanel(player));

		// get a list of all the maps in the stats
		String[] tabNamesArray = new String[player.stats.keySet().size()];
		player.stats.keySet().toArray(tabNamesArray);

		for (int i = 0; i < tabNamesArray.length; i++) {
			String tabName = tabNamesArray[i];
			JPanel panel = createStatsTab(tabName);
			statsGroups.addTab(tabName.substring(tabName.indexOf(":") + 1), panel);
		}
		status.setText("Current Player: " + player.getName());
		status.setVisible(true);
	}
}
