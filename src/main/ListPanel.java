package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListPanel extends JPanel {

    public static final int SORT_SLOT_MOST = 0;
    public static final int SORT_SLOT_LEAST = 1;
    public static final int SORT_COUNT_MOST = 2;
    public static final int SORT_COUNT_LEAST = 3;
    public static final int SORT_AZ = 4;
    public static final int SORT_ZA = 5;

    private static String[] allSortOptions = { "Slot First", "Slot Last", "Count Most", "Count Least", "A-Z", "Z-A" };
    private static int[] allSortConstants = { SORT_SLOT_MOST, SORT_SLOT_LEAST, SORT_COUNT_MOST, SORT_COUNT_LEAST, SORT_AZ, SORT_ZA };

    public static final int[] ALL_OPTIONS = allSortConstants;
    public static final int[] ALL_OPTIONS_EXCEPT_SLOT = { SORT_COUNT_MOST, SORT_COUNT_LEAST, SORT_AZ, SORT_ZA };
    public static final int[] ALL_AZ_OPTIONS = { SORT_AZ, SORT_ZA };

    private JPanel labelPanel;
    private List<QuantityLabel> labels;
    private int width, height;
    private JScrollPane scrollPanel = new JScrollPane();

    public ListPanel(int width, int height, int[] enabledOptions, int defaultOption) {
        this.labels = new ArrayList<QuantityLabel>();
        this.width = width;
        this.height = height;

        labels = new ArrayList<>();
        labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

        scrollPanel.setViewportView(labelPanel);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setPreferredSize(new Dimension(width, height));

        // get the vertical scroll bar
        scrollPanel.getVerticalScrollBar().setUnitIncrement(16);
        scrollPanel.getHorizontalScrollBar().setUnitIncrement(16);
        
        JPanel switchPanel = new JPanel(new BorderLayout());
        String[] options = new String[enabledOptions.length];
        int[] optionIndexes = new int[enabledOptions.length];
        int defaultOptionActual = -1;
        for (int i = 0; i < enabledOptions.length; i++) {
            options[i] = allSortOptions[enabledOptions[i]];
            optionIndexes[i] = allSortConstants[enabledOptions[i]];
            if (optionIndexes[i] == defaultOption) 
                defaultOptionActual = i;
        }
        JComboBox<String> sortOptionPicker = new JComboBox<String>(options);
        sortOptionPicker.setSelectedIndex(defaultOptionActual);
        sortOptionPicker.addActionListener(e -> {
            sortLabels(optionIndexes[sortOptionPicker.getSelectedIndex()]);
        });
        sortLabels(defaultOptionActual);
        sortOptionPicker.setPreferredSize(new Dimension(width / 2, 20));
        
        JPanel panel = new JPanel();

        panel.add(new JLabel("Sort By"));
        panel.add(sortOptionPicker);
        panel.setPreferredSize(new Dimension(width / 2, 100));
        switchPanel.add(panel, BorderLayout.EAST);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        int h = 50;
        switchPanel.setPreferredSize(new Dimension(width, h));
        scrollPanel.setPreferredSize(new Dimension(width, height - h));
        this.add(switchPanel);
        this.add(scrollPanel);
    }

    public ListPanel(int width, int height) {
        int[] enabledOptions = ALL_OPTIONS_EXCEPT_SLOT;
        this.labels = new ArrayList<QuantityLabel>();
        this.width = width;
        this.height = height;

        labels = new ArrayList<>();
        labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

        scrollPanel.setViewportView(labelPanel);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setPreferredSize(new Dimension(width, height));

        // get the vertical scroll bar
        scrollPanel.getVerticalScrollBar().setUnitIncrement(16);
        scrollPanel.getHorizontalScrollBar().setUnitIncrement(16);
        
        JPanel switchPanel = new JPanel(new BorderLayout());
        String[] options = new String[enabledOptions.length];
        int[] optionIndexes = new int[enabledOptions.length];
        for (int i = 0; i < enabledOptions.length; i++) {
            options[i] = allSortOptions[enabledOptions[i]];
            optionIndexes[i] = allSortConstants[enabledOptions[i]];
        }
        JComboBox<String> sortOptionPicker = new JComboBox<String>(options);
        sortOptionPicker.setSelectedIndex(0);
        sortOptionPicker.addActionListener(e -> {
            sortLabels(optionIndexes[sortOptionPicker.getSelectedIndex()]);
        });
        sortOptionPicker.setPreferredSize(new Dimension(width / 2, 20));
        
        JPanel panel = new JPanel();

        panel.add(new JLabel("Sort By"));
        panel.add(sortOptionPicker);
        panel.setPreferredSize(new Dimension(width / 2, 100));
        switchPanel.add(panel, BorderLayout.EAST);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        switchPanel.setPreferredSize(new Dimension(width, 100));
        scrollPanel.setPreferredSize(new Dimension(width, height - 100));
        this.add(switchPanel);
        this.add(scrollPanel);
    }

    public void addLabel(String labelText, double count, int slot) {
        QuantityLabel label = new QuantityLabel(labelText, count, slot);
        labels.add(label);
        labelPanel.add(label);
        setPreferredSize(new Dimension(width, height));
        revalidate();
        repaint();
    }

    public void addLabel(String labelText, double count) {
        QuantityLabel label = new QuantityLabel(labelText, count);
        labels.add(label);
        labelPanel.add(label);
        setPreferredSize(new Dimension(width, height));
        revalidate();
        repaint();
    }

    public void addLabel(QuantityLabel label) {
        labels.add(label);
        labelPanel.add(label);
        revalidate();
        repaint();
    }

    public void sortLabels(int type) {
        if (type == SORT_SLOT_MOST) {
            Collections.sort(labels, Comparator.comparing(QuantityLabel::getSlot));
        } else if (type == SORT_SLOT_LEAST) {
            Collections.sort(labels, Comparator.comparing(QuantityLabel::getSlot).reversed());
        } else if (type == SORT_COUNT_MOST) {
            Collections.sort(labels, Comparator.comparing(QuantityLabel::getCount).reversed());
        } else if (type == SORT_COUNT_LEAST) {
            Collections.sort(labels, Comparator.comparing(QuantityLabel::getCount));
        } else if (type == SORT_AZ) {
            Collections.sort(labels, Comparator.comparing(QuantityLabel::getText));
        } else if (type == SORT_ZA) {
            Collections.sort(labels, Comparator.comparing(QuantityLabel::getText).reversed());
        }

        labelPanel.removeAll();
        for (QuantityLabel l : labels) {
            labelPanel.add(l);
        }
        revalidate();
        repaint();
        
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
