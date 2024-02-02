package mainpanel;

import main.Constants;
import player.MinecraftPlayer;

import java.awt.Dimension;
import java.awt.List;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.synth.SynthDesktopIconUI;

public class BottomPanel extends JPanel {

    public JPanel sideBar;
    public PlayerView playerView = new PlayerView();

    public DefaultListModel<String> listModel;
    public JList<String> itemList;
    public JScrollPane scrollPane;

    public interface ItemListInteractionEvent {
        public void itemListInteracted(int selected);
    }

    public BottomPanel(ArrayList<MinecraftPlayer> players) {
        listModel = new DefaultListModel<>();
        itemList = new JList<>(listModel);
        itemList.getSelectionModel().addListSelectionListener(e -> {
            ListSelectionModel lsm = ((ListSelectionModel) e.getSource());
            if (lsm.isSelectionEmpty() || lsm.getValueIsAdjusting()) {
                return;
            }
            int idx = lsm.getMinSelectionIndex();
            playerView.setPlayer(players.get(idx));
        });

        scrollPane = new JScrollPane(itemList);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(175, Constants.BOTTOM_HEIGHT - 100));
        
        this.add(scrollPane);
        this.add(playerView);
        
    }
}
