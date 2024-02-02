package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScrollableLabelPanel extends JScrollPane {

    private JPanel labelPanel;
    private List<JLabel> labels;

    public ScrollableLabelPanel(int width, int height) {
        labels = new ArrayList<>();
        labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

        setViewportView(labelPanel);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setPreferredSize(new Dimension(width, height));
    }

    public void addLabel(String labelText) {
        JLabel label = new JLabel(labelText);
        labels.add(label);
        labelPanel.add(label);
        revalidate();
        repaint();
    }

    public void addLabel(JLabel label) {
        labels.add(label);
        labelPanel.add(label);
        revalidate();
        repaint();
    }

    public void clearLabels() {
        labels.clear();
        labelPanel.removeAll();
        revalidate();
        repaint();
    }

}
