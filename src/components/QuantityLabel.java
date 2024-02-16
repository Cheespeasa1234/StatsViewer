package components;

import javax.swing.JLabel;

/**
 * A JLabel that can be sorted by certain criteria
 * Can be sorted by name, count, or slot
 * Used in the {@link ListPanel} component as a Label
 * 
 * @see ListPanel
 * @see ListPanel#sortList()
 * @author Nate Levison, February 2024
 */
public class QuantityLabel extends JLabel {
    private double count;
    private int slot;

    public QuantityLabel(String l, double c) {
        super(l);
        count = c;
    }

    public QuantityLabel(String l, double c, int s) {
        super(l);
        count = c;
        slot = s;
    }

    public double getCount() {
        return count;
    }

    public int getSlot() {
        return slot;
    }
}
