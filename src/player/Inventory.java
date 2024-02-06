package player;

import java.util.List;

/**
 * Utility class for representing a Minecraft player's inventory
 * Never used in the actual player, just used to coerce GSON
 * @see MinecraftPlayer
 * @author Nate Levison, February 2024
 */
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
