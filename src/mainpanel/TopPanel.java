package mainpanel;

import main.Constants;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TopPanel extends JPanel {
    public interface ServerLoadEvent {
        public void serverLoaded();
    }
    public interface ServerExitEvent {
        public void serverExit();
    }

    JLabel statusLabel;
    JButton loadButton, backButton;

    public TopPanel(int topHeight, ServerLoadEvent onLoad, ServerExitEvent onExit) {
        
        loadButton = new JButton("Confirm");
        loadButton.addActionListener(e -> {
            onLoad.serverLoaded();
        });
        backButton = new JButton("Exit");
        backButton.setBackground(new Color(255, 0, 0, 100));
        backButton.addActionListener(e -> {
            onExit.serverExit();
        });
        
        statusLabel = new JLabel("Server not open");

        this.add(statusLabel);
        this.add(loadButton);
        this.add(backButton);
        this.setPreferredSize(new Dimension(Constants.PREF_W, topHeight));
        
    }
}
