package player;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class Item {
    
    @SerializedName("Slot") public int slot;
    public String id;
    @SerializedName("Count") public int count;
    @SerializedName("Damage") public int damage;
    @SerializedName("RepairCost") public int repairCost;
    @SerializedName("Items") public Item[] items;
    @SerializedName("Enchantments") public Enchantment[] enchantments;
    @SerializedName("Trim") public Trim trim;
    public String name;
    public Tag tag; // Add 'tag' field for nested structure
    
    public class Tag {
        @SerializedName("RepairCost") public Integer repairCost;
        @SerializedName("Damage") public Integer damage;
        @SerializedName("Enchantments") public Enchantment[] enchantments;
        @SerializedName("BlockEntityTag") public BlockEntityTag blockEntityTag;
        
        // naming
        public Display display;
        public String id;
        
        // trims
        @SerializedName("Base") public Integer base;
        public Pattern[] patterns;
        
        // goat horns
        public String instrument;

        // crossbows
        @SerializedName("ChargedProjectiles")
        public ChargedProjectile[] chargedProjectiles;
        @SerializedName("Charged")
        public Integer charged;
        
        // firework rocket
        @SerializedName("Fireworks")
        public Fireworks fireworks;
        
        // books
        public String[] pages;
        public String author;
        public String title;
        public Integer resolved;

        public class Display {
            @SerializedName("Name") public String name;
        }
        public class BlockEntityTag {
            @SerializedName("Items") public Item[] items;
            public String id;
        }        
        public class Pattern {
            public String pattern;
            public Integer color;
        }
        public class ChargedProjectile {
            public String id;
            @SerializedName("Count")
            public Integer count;
        }
        public class Fireworks {
            public Explosion explosion;
            @SerializedName("Flight")
            public Integer flight;
    
            public class Explosion {
                @SerializedName("Flicker")
                public Boolean flicker;
                @SerializedName("Trail")
                public Boolean trail;
                public Integer[] colors;
                @SerializedName("FadeColors")
                public Integer[] fadeColors;
                public Type type;
    
                public class Type {
                    public String name;
                }
            }
        }
    }

    public class Enchantment {
        public String id;
        public Integer lvl;
    }

    public class Trim {
        public String type;
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
        String count = (this.count > 1) ? (" x" + this.count) : "";
        String damage = "";
        String repairCost = "";
        String tag = (this.tag != null) ? toString(this.tag) : "";
        if (this.damage > 0) {
            damage =" with " + damage + " damage";
        }
        if (this.repairCost > 0) {
            repairCost = " with " + repairCost + " repair cost";
        }
    
        return "(slot " + slot + ") " + id + count + damage + repairCost + tag;
    }

    public static List<Item> getInventoryItems(String filePath) {
        return getItems(filePath, "Inventory");
    }

    public static List<Item> getEnderItems(String filePath) {
        return getItems(filePath, "EnderItems");
    }

    private static List<Item> getItems(String filePath, String sectionName) {
        List<Item> items = new ArrayList<Item>();

        try {
            Gson gson = new Gson();
            JsonObject playerData = gson.fromJson(new FileReader(filePath), JsonObject.class);

            if (playerData.has(sectionName)) {
                JsonArray itemsArray = playerData.getAsJsonArray(sectionName);

                for (JsonElement itemElement : itemsArray) {
                    Item item = gson.fromJson(itemElement, Item.class);
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
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

    public static List<List<Item>> getItems(String filePath) {
        List<Item> inventoryItems = getInventoryItems(filePath);
        List<Item> enderItems = getEnderItems(filePath);
        return List.of(inventoryItems, enderItems);
    }
}
