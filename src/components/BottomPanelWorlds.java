package components;

import util.Globals;
import world.World;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class BottomPanelWorlds extends JPanel {

    // private JPanel sideBar;
    private WorldView worldView = new WorldView();

    public ArrayList<World> worlds;
    public DefaultListModel<String> listModel;
    public JList<String> itemList;
    private JScrollPane scrollPane;

    public interface ItemListInteractionEvent {
        public void itemListInteracted(int selected);
    }

    public BottomPanelWorlds() {
        worlds = new ArrayList<>();
        listModel = new DefaultListModel<>();
        itemList = new JList<>(listModel);
        itemList.getSelectionModel().addListSelectionListener(e -> {
            ListSelectionModel lsm = ((ListSelectionModel) e.getSource());
            if (lsm.isSelectionEmpty() || lsm.getValueIsAdjusting()) {
                return;
            }
            int idx = lsm.getMinSelectionIndex();
            try {
				worldView.setWorld(worlds.get(idx));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        });

        scrollPane = new JScrollPane(itemList);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(175, Globals.BOTTOM_HEIGHT - 100));

        this.add(scrollPane);
        this.add(worldView);

    }
}
