package mainpanel;

import main.Constants;
import main.Lib;
import main.ScrollableLabelPanel;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import player.Item;
import player.MinecraftPlayer;

public class PlayerView extends JPanel {
    JLabel status;
    JTabbedPane statsGroups;
    MinecraftPlayer player = null;

    public PlayerView() {
        status = new JLabel("No player.");
        statsGroups = new JTabbedPane();
        this.add(status);
        this.add(statsGroups);
        this.setPreferredSize(new Dimension(Constants.PREF_W - 200, Constants.BOTTOM_HEIGHT));
    }

    public JPanel createInventoryPanel(MinecraftPlayer player) {
        JPanel inventoryPanel = new JPanel();
        JPanel mainInventoryContainer = new JPanel();
        JPanel enderInventoryContainer = new JPanel();
        mainInventoryContainer.setLayout(new BoxLayout(mainInventoryContainer, BoxLayout.Y_AXIS));
        enderInventoryContainer.setLayout(new BoxLayout(enderInventoryContainer, BoxLayout.Y_AXIS));
        ScrollableLabelPanel mainInventoryPanel = new ScrollableLabelPanel((Constants.PREF_W - 200) / 2,
                Constants.BOTTOM_HEIGHT - 100);
        ScrollableLabelPanel enderInventoryPanel = new ScrollableLabelPanel((Constants.PREF_W - 200) / 2,
                Constants.BOTTOM_HEIGHT - 100);
        JLabel mainInventoryLabel = new JLabel("Main Inventory");
        mainInventoryLabel.setFont(Constants.FONT_PRIMARY.deriveFont(24f));
        mainInventoryContainer.add(mainInventoryLabel);
        JLabel enderInventoryLabel = new JLabel("Ender Inventory");
        enderInventoryLabel.setFont(Constants.FONT_PRIMARY.deriveFont(24f));
        enderInventoryContainer.add(enderInventoryLabel);
        if (player.mainInventory.size() == 0)
            mainInventoryPanel.addLabel("No items in main inventory");
        else
            for (Item item : player.mainInventory)
                mainInventoryPanel.addLabel(item.toFancyString());
        if (player.enderInventory.size() == 0)
            enderInventoryPanel.addLabel("No items in ender inventory");
        else
            for (Item item : player.enderInventory)
                enderInventoryPanel.addLabel(item.toFancyString());
        mainInventoryContainer.add(mainInventoryPanel);
        enderInventoryContainer.add(enderInventoryPanel);
        inventoryPanel.add(mainInventoryContainer);
        inventoryPanel.add(enderInventoryContainer);
        return inventoryPanel;
    }

    public ScrollableLabelPanel createStatsTab(String tabName) {
        int size = player.stats.get(tabName).size();
        if (size == 0) return null;

        ScrollableLabelPanel scrollableLabelPanel = new ScrollableLabelPanel(Constants.PREF_W - 200,
                Constants.BOTTOM_HEIGHT - 100);

        // make the layout so everything is on a new line
        List<HashMap.Entry<String, Double>> entries = new ArrayList<HashMap.Entry<String, Double>>(
                player.stats.get(tabName).entrySet());
        entries.sort(Map.Entry.<String, Double>comparingByValue().reversed());
        for (HashMap.Entry<String, Double> entry : entries) {
            scrollableLabelPanel.addLabel(entry.getKey() + ": " + Lib.doubleToString(entry.getValue()));
        }

        return scrollableLabelPanel;
    }
    
    public void setPlayer(MinecraftPlayer player) {

        // reset everything
        this.player = player;
        statsGroups.removeAll();

        // make the inventory tab        
        statsGroups.addTab("Inventory", createInventoryPanel(player));

        // get a list of all the maps in the stats
        String[] tabNamesArray = new String[player.stats.keySet().size()];
        player.stats.keySet().toArray(tabNamesArray);

        for (int i = 0; i < tabNamesArray.length; i++) {

            String tabName = tabNamesArray[i];
            String tabEntryCount = " (" + player.stats.get(tabName).size() + ")";

            ScrollableLabelPanel scrollableLabelPanel = createStatsTab(tabName);
            if (scrollableLabelPanel == null) continue;

            statsGroups.addTab(tabName.substring(tabName.indexOf(":") + 1) + tabEntryCount, scrollableLabelPanel);
        }
        status.setText("Current Player: " + player.getName());
        status.setVisible(true);
    }
}
