package mainpanel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import main.MinecraftPlayer;

public class PlayerView extends JPanel {
    JLabel status;
    JTabbedPane statsTabs;
    public PlayerView() {
        status = new JLabel("No player.");
        statsTabs = new JTabbedPane();
        statsTabs.setVisible(false);
        this.add(status);
        this.add(statsTabs);
    }

    public void setPlayer(MinecraftPlayer player) {
        status = new JLabel("Current Player: " + player.UUID);
        statsTabs = new JTabbedPane();
        statsTabs.setVisible(true);
        for (int i = 0; i < player.stats.statsGroups.size(); i++) {
            JPanel tab = new JPanel();
            tab.add(new JLabel(player.stats.statsGroups.get(i).toString()));
            statsTabs.addTab(player.stats.statsGroupsNames.get(i), tab);
        }
    }
}
