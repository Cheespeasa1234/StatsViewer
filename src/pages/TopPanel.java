package pages;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.Globals;

public class TopPanel extends JPanel {

	public interface Event {
		public void run();
	}

    JLabel statusLabel;
    JButton loadButton, backButton, view1Button, view2Button;

    public TopPanel(int topHeight, Event onLoad, Event onExit, Event onView1, Event onView2) {
        
        loadButton = new JButton("Confirm");
        loadButton.addActionListener(e -> {
            onLoad.run();
        });
        backButton = new JButton("Exit");
        backButton.setBackground(new Color(255, 0, 0, 100));
        backButton.addActionListener(e -> {
            onExit.run();
        });
        
        statusLabel = new JLabel("Server not open");

		view1Button = new JButton("Players");
		view2Button = new JButton("Worlds");

		view1Button.addActionListener(e -> {
			onView1.run();
		});
		view2Button.addActionListener(e -> {
			onView2.run();
		});

        // this.add(statusLabel);
        this.add(loadButton);
        this.add(backButton);
		this.add(view1Button);
		this.add(view2Button);
        this.setPreferredSize(new Dimension(Globals.PREF_W, topHeight));
        
    }
}
