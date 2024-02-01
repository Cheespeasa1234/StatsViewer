package mainpanel;

import main.Constants;

import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class BottomPanel extends JTabbedPane {

    public JPanel playersPage;
    public JPanel serverPage;
    public JPanel sideBar;
    public JTextField searchBar;
    public PlayerView playerViewer;

    public DefaultListModel<String> listModel;
    public JList<String> itemList;

    public interface ItemListInteractionEvent {
        public void itemListInteracted(int selected);
    }

    public BottomPanel(ItemListInteractionEvent onItemListInteraction) {
        listModel = new DefaultListModel<>();
        itemList = new JList<>(listModel);
        itemList.getSelectionModel().addListSelectionListener(e -> {
            ListSelectionModel lsm = ((ListSelectionModel) e.getSource());
            int idx = lsm.getMinSelectionIndex();
            onItemListInteraction.itemListInteracted(idx);
        });

        JScrollPane scrollPane = new JScrollPane(itemList);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(200, Constants.BOTTOM_HEIGHT - 100));
        
        JPanel searchGroup = new JPanel();
        JLabel searchBarLabel = new JLabel("Search");
        searchBar = new JTextField();
        searchBar.setText("UUID");
        searchGroup.add(searchBarLabel);
        searchGroup.add(searchBar);

        sideBar = new JPanel();
        sideBar.add(scrollPane);
        sideBar.add(searchGroup);
        sideBar.setPreferredSize(new Dimension(200, Constants.BOTTOM_HEIGHT));

        playersPage = new JPanel();
        playerViewer = new PlayerView();
        playersPage.add(sideBar);
        playersPage.add(playerViewer);
        playersPage.setPreferredSize(new Dimension(Constants.PREF_W, Constants.BOTTOM_HEIGHT));
        
        JPanel panel2 = new JPanel();
        panel2.add(new JLabel("Hello, Tab 2!"));
        panel2.setPreferredSize(new Dimension(Constants.PREF_W, Constants.BOTTOM_HEIGHT));
        
        this.setPreferredSize(new Dimension(Constants.PREF_W, Constants.BOTTOM_HEIGHT));

        this.addTab("Players", playersPage);
        this.addTab("Level", panel2);

    }
}
