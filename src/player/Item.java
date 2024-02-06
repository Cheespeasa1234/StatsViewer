package player;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @Expose
    @SerializedName("Slot")
    public int slot;
    @Expose
    public String id;
    @Expose
    @SerializedName("Count")
    public int count;
    @Expose
    @SerializedName("Damage")
    public int damage;
    @Expose
    @SerializedName("RepairCost")
    public int repairCost;
    @Expose
    @SerializedName("Items")
    public Item[] items;
    @Expose
    @SerializedName("Enchantments")
    public Enchantment[] enchantments;
    @Expose
    @SerializedName("Trim")
    public Trim trim;
    @Expose
    public String name;
    @Expose
    public Tag tag; // Add 'tag' field for nested structure

    public class Tag {
        @Expose
        @SerializedName("RepairCost")
        public Integer repairCost;
        @Expose
        @SerializedName("Damage")
        public Integer damage;
        @Expose
        @SerializedName("Enchantments")
        public Enchantment[] enchantments;
        @Expose
        @SerializedName("BlockEntityTag")
        public BlockEntityTag blockEntityTag;

        // naming
        @Expose
        public Display display;
        @Expose
        public String id;

        // trims
        @Expose
        @SerializedName("Base")
        public Integer base;
        @Expose
        public Pattern[] patterns;

        // goat horns
        @Expose
        public String instrument;

        // crossbows
        @Expose
        @SerializedName("ChargedProjectiles")
        public ChargedProjectile[] chargedProjectiles;
        @Expose
        @SerializedName("Charged")
        public Integer charged;

        // firework rocket
        @Expose
        @SerializedName("Fireworks")
        public Fireworks fireworks;

        // books
        @Expose
        public String[] pages;
        @Expose
        public String author;
        @Expose
        public String title;
        @Expose
        public Integer resolved;

        public class Display {
            @Expose
            @SerializedName("Name")
            public String name;
        }

        public class BlockEntityTag {
            @Expose
            @SerializedName("Items")
            public Item[] items;
            @Expose
            public String id;
        }

        public class Pattern {
            @Expose
            public String pattern;
            @Expose
            public Integer color;
        }

        public class ChargedProjectile {
            @Expose
            public String id;
            @Expose
            @SerializedName("Count")
            public Integer count;
        }

        public class Fireworks {
            @Expose
            public Explosion explosion;
            @Expose
            @SerializedName("Flight")
            public Integer flight;

            public class Explosion {

                @Expose
                @SerializedName("Flicker")
                public Boolean flicker;

                @Expose
                @SerializedName("Trail")
                public Boolean trail;
                @Expose
                public Integer[] colors;

                @Expose
                @SerializedName("FadeColors")
                public Integer[] fadeColors;
                @Expose
                public Type type;

                public class Type {
                    @Expose
                    public String name;
                }
            }
        }
    }

    public class Enchantment {
        @Expose
        public String id;
        @Expose
        public Integer lvl;
    }

    public class Trim {
        @Expose
        public String type;
        @Expose
        public String material;
    }

    public class Inventories {
        public Inventory enderItemsInventory;
        public Inventory mainInventory;

        public class Inventory {
            public Item[] inventory;
        }
    }

    private static String toString(Object o) {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .create();
        return gson.toJson(o);
    }

    public String toString() {
        return toString(this);
    }

    public String toFancyString() {
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

        return slot + " " + count + id + damage + repairCost + tag;
    }

    public static void printInventoryContents(List<List<Item>> inventories) {
        if (inventories != null && inventories.size() == 2) {
            System.out.println("Ender Chest Contents:");
            System.out.println(inventoryToString(inventories.get(0)));

            System.out.println("\nMain Inventory Contents:");
            System.out.println(inventoryToString(inventories.get(1)));
        } else {
            System.out.println("Invalid inventory structure.");
        }
    }

    public static String inventoryToString(List<Item> inventory) {
        String out = "";
        if (inventory != null) {
            for (Item item : inventory) {
                out += item.toString() + "\n";
            }
        }
        return out;
    }
}
