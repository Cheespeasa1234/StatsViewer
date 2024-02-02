package mainpanel;

import main.Constants;
import main.Lib;
import main.ScrollableLabelPanel;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import main.Constants;
import main.ScrollableLabelPanel;
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
    
    public void setPlayer(MinecraftPlayer player) {
        
        this.player = player;
        statsGroups.removeAll();
        
        JPanel inventoryPanel = new JPanel();

        JPanel mainInventoryContainer = new JPanel();
        mainInventoryContainer.setLayout(new BoxLayout(mainInventoryContainer, BoxLayout.Y_AXIS));
        
        JPanel enderInventoryContainer = new JPanel();
        enderInventoryContainer.setLayout(new BoxLayout(enderInventoryContainer, BoxLayout.Y_AXIS));

        ScrollableLabelPanel mainInventoryPanel = new ScrollableLabelPanel((Constants.PREF_W - 200) / 2, Constants.BOTTOM_HEIGHT - 100);
        ScrollableLabelPanel enderInventoryPanel = new ScrollableLabelPanel((Constants.PREF_W - 200) / 2, Constants.BOTTOM_HEIGHT - 100);

        JLabel mainInventoryLabel = new JLabel("Main Inventory");
        mainInventoryLabel.setFont(Constants.FONT_PRIMARY.deriveFont(24f));
        mainInventoryContainer.add(mainInventoryLabel);

        JLabel enderInventoryLabel = new JLabel("Ender Inventory");
        enderInventoryLabel.setFont(Constants.FONT_PRIMARY.deriveFont(24f));
        enderInventoryContainer.add(enderInventoryLabel);

        for (Item item: player.mainInventory) {
            mainInventoryPanel.addLabel(item.toFancyString());
        }

        for (Item item: player.enderInventory) {
            enderInventoryPanel.addLabel(item.toFancyString());
        }

        mainInventoryContainer.add(mainInventoryPanel);
        enderInventoryContainer.add(enderInventoryPanel);

        inventoryPanel.add(mainInventoryContainer);
        inventoryPanel.add(enderInventoryContainer);

        statsGroups.addTab("Inventory", inventoryPanel);

        // set the stats
        for (int i = 0; i < player.stats.statsGroups.size(); i++) {
            
            int size = player.stats.statsGroups.get(i).size();
            if (size == 0) continue;
            
            String tabEntryCount = " (" + size + ")";
            ScrollableLabelPanel scrollableLabelPanel = new ScrollableLabelPanel(Constants.PREF_W - 200, Constants.BOTTOM_HEIGHT - 100);

            // make the layout so everything is on a new line
            List<HashMap.Entry<String, Double>> entries = new ArrayList<HashMap.Entry<String, Double>>(player.stats.statsGroups.get(i).entrySet());
            entries.sort(Map.Entry.<String, Double>comparingByValue().reversed());
            for (HashMap.Entry<String, Double> entry : entries) {
                scrollableLabelPanel.addLabel(entry.getKey() + ": " + Lib.doubleToString(entry.getValue()));
            }
            
            String tabName = player.stats.statsGroupsNames.get(i);
            statsGroups.addTab(tabName + tabEntryCount, scrollableLabelPanel);

        }
        status.setText("Current Player: " + player.getName());
        status.setVisible(true);
    }
}
