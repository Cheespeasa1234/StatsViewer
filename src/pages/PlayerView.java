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

public class PlayerView extends JPanel {
    JLabel status;
    JTabbedPane statsGroups;
    MinecraftPlayer player = null;

    private final int WIDTH = Globals.PREF_W - 250;

    public PlayerView() {
        status = new JLabel("No player selected.");
        statsGroups = new JTabbedPane();
        this.add(status);
        this.add(statsGroups);
        this.setPreferredSize(new Dimension(Globals.PREF_W - 250, Globals.BOTTOM_HEIGHT));
    }

    public JPanel createAdvancementsPanel(MinecraftPlayer player) {
        ListPanel panel = new ListPanel(
            WIDTH, Globals.BOTTOM_HEIGHT - 100,
            ListPanel.ALL_AZ_OPTIONS, ListPanel.SORT_AZ
        );
        String[] advancementNames = player.advancements.keySet().toArray(new String[0]);
        Arrays.sort(advancementNames);
        for (String advancement : advancementNames) {
            Advancement a = player.advancements.get(advancement);
            if (a.done) {
                panel.addLabel(advancement + ": " + Lib.getTimeSince(a.getCompleted()), 0);
            }
        }
        JPanel mainPanel = new JPanel();
        mainPanel.add(panel);
        return mainPanel;
    }

    public JPanel createInventoryPanel(MinecraftPlayer player) {
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
            for (Item item : player.mainInventory)
                mainInventoryPanel.addLabel(item.toFancyString(), item.count, item.slot);
        if (player.enderInventory.size() == 0)
            enderInventoryPanel.addLabel("No items in ender inventory", 0);
        else
            for (Item item : player.enderInventory)
                enderInventoryPanel.addLabel(item.toFancyString(), item.count, item.slot);
        mainInventoryContainer.add(mainInventoryPanel);
        enderInventoryContainer.add(enderInventoryPanel);
        inventoryPanel.add(mainInventoryContainer);
        inventoryPanel.add(enderInventoryContainer);
        return inventoryPanel;
    }

    public JPanel createStatsTab(String tabName) {
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

    public void setPlayer(MinecraftPlayer player) {

        // reset everything
        this.player = player;
        statsGroups.removeAll();

        // make the inventory tab
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
