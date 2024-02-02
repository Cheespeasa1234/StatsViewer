package player;

import java.util.List;

public class Inventory {

    public List<Item> items;

    public Inventory(List<Item> items) {
        this.items = items;
    }

    public Item getItemInSlot(int slot) {
        for (Item item : items) {
            if (item.slot == slot) {
                return item;
            }
        }
        return null;
    }

}
