package main;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A JPanel that displays a dynamic list of {@link QuantityLabel}s
 * The list can be sorted by slot, count, or name
 * Dynamically resizes itself
 * 
 * @see QuantityLabel
 * @author Nate Levison, February 2024
 */
public class ListPanel extends JPanel {

	/**
	 * Sort the list by slot, with the first slot first
	 * 
	 * @see ListPanel#sortLabels(int)
	 */
	public static final int SORT_SLOT_MOST = 0;

	/**
	 * Sort the list by slot, with the last slot first
	 * 
	 * @see ListPanel#sortLabels(int)
	 */
	public static final int SORT_SLOT_LEAST = 1;

	/**
	 * Sort the list by item count, with the largest stack count first
	 * 
	 * @see ListPanel#sortLabels(int)
	 */
	public static final int SORT_COUNT_MOST = 2;

	/**
	 * Sort the list by item conut, with the smallest stack count first
	 * 
	 * @see ListPanel#sortLabels(int)
	 */
	public static final int SORT_COUNT_LEAST = 3;

	/**
	 * Sort the list by name, in ascending order
	 * 
	 * @see ListPanel#sortLabels(int)
	 */
	public static final int SORT_AZ = 4;

	/**
	 * Sort the list by name, in descending order
	 * 
	 * @see ListPanel#sortLabels(int)
	 */
	public static final int SORT_ZA = 5;

	/**
	 * All the sort options
	 * Used to populate the JComboBox
	 */
	private static String[] allSortOptions = {
			"Slot First", "Slot Last", "Count Most", "Count Least", "A-Z", "Z-A"
	};

	/**
	 * A list of all the sort constants indexes
	 */
	private static int[] allSortConstants = {
			SORT_SLOT_MOST, SORT_SLOT_LEAST, SORT_COUNT_MOST, SORT_COUNT_LEAST,
			SORT_AZ, SORT_ZA
	};

	/**
	 * All the sort constants
	 * Use it to make all sorting types available on a Panel
	 * 
	 * @see ListPanel#ListPanel(int, int, int[], int)
	 */
	public static final int[] ALL_OPTIONS = allSortConstants;

	/**
	 * All the sort constants except for the slot sorting
	 * Use it to make all sorting types available on a Panel except for the slot
	 * sorting
	 * 
	 * @see ListPanel#ListPanel(int, int, int[], int)
	 */
	public static final int[] ALL_OPTIONS_EXCEPT_SLOT = {
			SORT_COUNT_MOST, SORT_COUNT_LEAST, SORT_AZ, SORT_ZA
	};

	/**
	 * Only the alphabetical-related sorting options
	 * Use it to make all alphabetical sorting types available on a Panel
	 * 
	 * @see ListPanel#ListPanel(int, int, int[], int)
	 */
	public static final int[] ALL_AZ_OPTIONS = {
			SORT_AZ, SORT_ZA
	};

	/**
	 * No sorting options
	 * Use it to make no sorting options available on a Panel
	 * 
	 * @see ListPanel#ListPanel(int, int, int[], int)
	 */
	public static final int[] NO_OPTIONS = {};

	/**
	 * The panel that holds the labels
	 */
	private JPanel labelPanel;

	/**
	 * The list of labels to display
	 */
	private List<QuantityLabel> labels;

	/**
	 * The width and height of the panel
	 */
	private int width, height;

	/**
	 * The scroll panel that holds the label panel
	 */
	private JScrollPane scrollPanel = new JScrollPane();

	/**
	 * Create a new ListPanel with the given width and height, and enabled options
	 * The list of the enabled options is used to populate the JComboBox
	 * 
	 * @param width
	 * the width of the panel
	 * @param height
	 * the height of the panel
	 * @param enabledOptions
	 * the sorting options to enable, use the constants in this class
	 * @param defaultOption
	 * the default sorting option, use the constants in this class
	 * @see ListPanel#ALL_OPTIONS
	 * @see ListPanel#ALL_OPTIONS_EXCEPT_SLOT
	 * @see ListPanel#ALL_AZ_OPTIONS
	 */
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

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(scrollPanel);
		if (enabledOptions.length != 0) {
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

			int h = 50;
			switchPanel.setPreferredSize(new Dimension(width, h));
			scrollPanel.setPreferredSize(new Dimension(width, height - h));
			this.add(switchPanel);
		}

	}

	/**
	 * Create a new ListPanel with the given width and height
	 * 
	 * @param width
	 * the width of the panel
	 * @param height
	 * the height of the panel
	 */
	public ListPanel(int width, int height) {
		int[] enabledOptions = ALL_OPTIONS_EXCEPT_SLOT;
		this.labels = new ArrayList<QuantityLabel>();
		this.width = width;
		this.height = height;
		setBorder(new EmptyBorder(10, 10, 10, 10));

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

	/**
	 * Add a label to the list, sortable by count and slot
	 * 
	 * @param labelText
	 * the text of the label
	 * @param count
	 * the count of the label
	 * @param slot
	 * the slot of the label
	 * @see QuantityLabel
	 */
	public void addLabel(String labelText, double count, int slot) {
		QuantityLabel label = new QuantityLabel(labelText, count, slot);
		labels.add(label);
		labelPanel.add(label);
		setPreferredSize(new Dimension(width, height));
		revalidate();
		repaint();
	}

	/**
	 * Add a label to the list, sortable by count
	 * 
	 * @param labelText
	 * the text of the label
	 * @param count
	 * the count of the label
	 * @see QuantityLabel
	 */
	public void addLabel(String labelText, double count) {
		QuantityLabel label = new QuantityLabel(labelText, count);
		labels.add(label);
		labelPanel.add(label);
		setPreferredSize(new Dimension(width, height));
		revalidate();
		repaint();
	}

	/**
	 * Add a label to the list, sortable by count
	 * 
	 * @param label
	 * the label to add
	 * @see QuantityLabel
	 */
	public void addLabel(QuantityLabel label) {
		labels.add(label);
		labelPanel.add(label);
		revalidate();
		repaint();
	}

	public void addSpacer() {
		JLabel label = new JLabel(" ");
		label.setPreferredSize(new Dimension(width, 20));
		labelPanel.add(label);
		revalidate();
		repaint();
	}

	public void addLabel(JLabel label) {
		QuantityLabel lbl = new QuantityLabel(label.getText(), 0);
		lbl.setFont(label.getFont());
		lbl.setForeground(label.getForeground());
		lbl.setBackground(label.getBackground());
		lbl.setOpaque(label.isOpaque());

		labels.add(lbl);
		labelPanel.add(lbl);
		revalidate();
		repaint();
	}

	public void addLabel(String lbl) {
		QuantityLabel label = new QuantityLabel(lbl, 0);
		labels.add(label);
		labelPanel.add(label);
		setPreferredSize(new Dimension(width, height));
		revalidate();
		repaint();
	}

	/**
	 * Sort the labels by the given type
	 * 
	 * @param type
	 * the type of sorting to use, use the constants in this class
	 */
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

	/**
	 * Clear all the labels from the list
	 */
	public void clearLabels() {
		labels.clear();
		labelPanel.removeAll();
		revalidate();
		repaint();
	}

}
