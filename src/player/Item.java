package player;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Probably the greatest example of how Java is fucked
 * A deserialized item as parsed by Gson
 * 
 * @see MinecraftPlayer
 * @author Nate Levison, February 2024
 */
public class Item {

	@Expose @SerializedName("Slot") public int slot;
	@Expose public String id;
	@Expose @SerializedName("Count") public int count;
	@Expose @SerializedName("Damage") public int damage;
	@Expose @SerializedName("RepairCost") public int repairCost;
	@Expose @SerializedName("Items") public Item[] items;
	@Expose @SerializedName("Enchantments") public Enchantment[] enchantments;
	@Expose @SerializedName("Trim") public Trim trim;
	@Expose public String name;
	@Expose public Tag tag; // Add 'tag' field for nested structure

	public class Tag {
		@Expose @SerializedName("RepairCost") public Integer repairCost;
		@Expose @SerializedName("Damage") public Integer damage;
		@Expose @SerializedName("Enchantments") public Enchantment[] enchantments;
		@Expose @SerializedName("BlockEntityTag") public BlockEntityTag blockEntityTag;

		// naming
		@Expose public Display display;
		@Expose public String id;

		// trims
		@Expose @SerializedName("Base") public Integer base;
		@Expose public Pattern[] patterns;

		// goat horns
		@Expose public String instrument;

		// crossbows
		@Expose @SerializedName("ChargedProjectiles") public ChargedProjectile[] chargedProjectiles;
		@Expose @SerializedName("Charged") public Integer charged;

		// firework rocket
		@Expose @SerializedName("Fireworks") public Fireworks fireworks;

		// books
		@Expose public String[] pages;
		@Expose public String author;
		@Expose public String title;
		@Expose public Integer resolved;

		// potions
		@Expose @SerializedName("Potion") public String potion;

		public class Display {
			@Expose @SerializedName("Name") public String name;
		}

		public class BlockEntityTag {
			@Expose @SerializedName("Items") public Item[] items;
			@Expose public String id;
		}

		public class Pattern {
			@Expose public String pattern;
			@Expose public Integer color;
		}

		public class ChargedProjectile {
			@Expose public String id;
			@Expose @SerializedName("Count") public Integer count;
		}

		public class Fireworks {
			@Expose public Explosion explosion;
			@Expose @SerializedName("Flight") public Integer flight;

			public class Explosion {

				@Expose @SerializedName("Flicker") public Boolean flicker;

				@Expose @SerializedName("Trail") public Boolean trail;
				@Expose public Integer[] colors;

				@Expose @SerializedName("FadeColors") public Integer[] fadeColors;
				@Expose public Type type;

				public class Type {
					@Expose public String name;
				}
			}
		}
	}

	public class Enchantment {
		@Expose public String id;
		@Expose public Integer lvl;
	}

	public class Trim {
		@Expose public String type;
		@Expose public String material;
	}

	public class Inventories {
		public Inventory enderItemsInventory;
		public Inventory mainInventory;

		public class Inventory {
			public Item[] inventory;
		}
	}

	/**
	 * Returns the custom name of the item, if it has one.
	 * 
	 * @return Custom name of the item.
	 */
	public String getCustomName() {
		if (this.tag != null && this.tag.display != null) {
			String name = this.tag.display.name;
			if (name.startsWith("{") && name.endsWith("}")) {
				return " " + name.substring(8, name.length() - 1);
			} else {
				return " " + name;
			}
		} else {
			return "";
		}
	}

	/**
	 * Converts the provided object into a JSON string.
	 * 
	 * @param o
	 *            The object to convert to a JSON string.
	 * @return JSON string representation of the object.
	 */
	private static String toString(Object o) {
		Gson gson = new GsonBuilder()
				.disableHtmlEscaping()
				.create();
		return gson.toJson(o);
	}

	/**
	 * peak coding
	 */
	public String toString() {
		return toString(this);
	}

	/**
	 * Generates a fancy string representation of the item for display, including
	 * details like count, damage, and repair cost. This is specifically for block
	 * entities, like chests and shulker boxes.
	 * 
	 * @return Fancy string representation of the item.
	 */
	private String toFancyStringBlockEntity() {

		String count = (this.count > 1) ? ("x" + this.count + " ") : "";
		String damage = "";
		String repairCost = "";
		String tag = (this.tag != null) ? toString(this.tag) : "";
		if (this.damage > 0) {
			damage = " with " + damage + " damage";
		}
		if (this.repairCost > 0) {
			repairCost = " with " + repairCost + " repair cost";
		}
		return count + getCustomName() + " " + this.id + damage + repairCost + tag;
	}

	/**
	 * Generates an array of fancy string representations of the item for display,
	 * including details like count, damage, and repair cost.
	 * 
	 * @return Array of fancy string representations of the item.
	 */
	public String[] toFancyString() {

		String count = (this.count > 1) ? ("x" + this.count + " ") : "";
		String damage = "";
		String repairCost = "";
		String tag = (this.tag != null) ? toString(this.tag) : "";
		if (this.damage > 0) {
			damage = " with " + damage + " damage";
		}
		if (this.repairCost > 0) {
			repairCost = " with " + repairCost + " repair cost";
		}

		String slot = "(slot " + this.slot + ")";
		if (this.slot == 103) {
			slot = "(helmet)";
		} else if (this.slot == 102) {
			slot = "(chestplate)";
		} else if (this.slot == 101) {
			slot = "(leggings)";
		} else if (this.slot == 100) {
			slot = "(boots)";
		} else if (this.slot == -106) {
			slot = "(offhand)";
		}

		String line = slot + getCustomName() + " " + count + this.id + damage + repairCost + tag;
		boolean hasItems = this.tag != null && this.tag.blockEntityTag != null && this.tag.blockEntityTag.items != null;

		if (!hasItems) {
			return new String[] { line };
		}

		String[] lines = new String[this.tag.blockEntityTag.items.length + 1];
		lines[0] = line;
		for (int i = 0; i < this.tag.blockEntityTag.items.length; i++) {
			lines[i + 1] = "    " + this.tag.blockEntityTag.items[i].toFancyStringBlockEntity();
		}
		return lines;
	}

	/**
	 * Prints the contents of the provided inventories, specifically the ender chest
	 * and main inventory contents.
	 * @deprecated
	 * @param inventories
	 *            The list of inventories containing ender chest and main inventory
	 *            items.
	 */
	private static void printInventoryContents(List<List<Item>> inventories) {
		if (inventories != null && inventories.size() == 2) {
			System.out.println("Ender Chest Contents:");
			System.out.println(inventoryToString(inventories.get(0)));

			System.out.println("\nMain Inventory Contents:");
			System.out.println(inventoryToString(inventories.get(1)));
		} else {
			System.out.println("Invalid inventory structure.");
		}
	}

	/**
	 * Converts the provided inventory items into a formatted string representation.
	 * @deprecated
	 * @param inventory
	 *            The list of items in the inventory.
	 * @return Formatted string representation of the inventory contents.
	 */
	private static String inventoryToString(List<Item> inventory) {
		String out = "";
		if (inventory != null) {
			for (Item item : inventory) {
				out += item.toString() + "\n";
			}
		}
		return out;
	}
}
