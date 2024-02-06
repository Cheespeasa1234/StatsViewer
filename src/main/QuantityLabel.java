package main;

import javax.swing.JLabel;

public class QuantityLabel extends JLabel {
    public double count;
    public int slot;
    public QuantityLabel(String l, double c) {
        super(l);
        count = c;
    }
    public QuantityLabel(String l, double c, int s) {
        super(l);
        count = c;
        slot = s;
    }
    public double getCount() { return count; }
    public int getSlot() { return slot; }
}
