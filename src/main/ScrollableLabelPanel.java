package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScrollableLabelPanel extends JScrollPane {

    private JPanel labelPanel;
    private List<JLabel> labels;
    private int width, height;

    public ScrollableLabelPanel(int width, int height) {
        this.width = width;
        this.height = height;
        
        labels = new ArrayList<>();
        labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

        setViewportView(labelPanel);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setPreferredSize(new Dimension(width, height));

        // get the vertical scroll bar
        getVerticalScrollBar().setUnitIncrement(16);
        getHorizontalScrollBar().setUnitIncrement(16);
    }

    public void addLabel(String labelText) {
        JLabel label = new JLabel(labelText);
        labels.add(label);
        labelPanel.add(label);
        setPreferredSize(new Dimension(width, height));
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
