package util;

import java.awt.Color;
import java.util.HashMap;
import javax.swing.ImageIcon;

import world.Biome;

public class AssetGlobals {
    public static ImageIcon hoverIcon1 = new ImageIcon("src/assets/hover/hover-normal.png");
    public static ImageIcon hoverIcon2 = new ImageIcon("src/assets/hover/hover-alt.png");
    public static ImageIcon hoverIcon3 = new ImageIcon("src/assets/hover/hover-alt2.png");
    public static HashMap<String, ImageIcon> structureIcons = new HashMap<String, ImageIcon>();
    public static HashMap<String, Biome> biomes = new HashMap<>() {
        {
            put("minecraft:badlands", new Biome("minecraft", "badlands", new Color(0xFF6D00)));
            put("minecraft:bamboo_jungle", new Biome("minecraft", "bamboo_jungle", new Color(0x7E9A45)));
            put("minecraft:basalt_deltas", new Biome("minecraft", "basalt_deltas", new Color(0x2A2829)));
            put("minecraft:beach", new Biome("minecraft", "beach", new Color(0xFADE55)));
            put("minecraft:birch_forest", new Biome("minecraft", "birch_forest", new Color(0x506D3A)));
            put("minecraft:cherry_grove", new Biome("minecraft", "cherry_grove", new Color(0xE965BE)));
            put("minecraft:crimson_forest", new Biome("minecraft", "crimson_forest", new Color(0x970000)));
            put("minecraft:dark_forest", new Biome("minecraft", "dark_forest", new Color(0x1A361F)));
            put("minecraft:deep_cold_ocean", new Biome("minecraft", "deep_cold_ocean", new Color(0x000030)));
            put("minecraft:deep_frozen_ocean", new Biome("minecraft", "deep_frozen_ocean", new Color(0x7E95B6)));
            put("minecraft:deep_lukewarm_ocean", new Biome("minecraft", "deep_lukewarm_ocean", new Color(0x0070FF)));
            put("minecraft:deep_ocean", new Biome("minecraft", "deep_ocean", new Color(0x000050)));
            put("minecraft:deep_dark", new Biome("minecraft", "deep_dark", new Color(0x000000)));
            put("minecraft:desert", new Biome("minecraft", "desert", new Color(0xFAAC58)));
            put("minecraft:dripstone_caves", new Biome("minecraft", "dripstone_caves", new Color(0x9C6E3E)));
            put("minecraft:end_barrens", new Biome("minecraft", "end_barrens", new Color(0xD8D8D8)));
            put("minecraft:end_highlands", new Biome("minecraft", "end_highlands", new Color(0xC8C8C8)));
            put("minecraft:end_midlands", new Biome("minecraft", "end_midlands", new Color(0xA0A0A0)));
            put("minecraft:eroded_badlands", new Biome("minecraft", "eroded_badlands", new Color(0xFF6D00)));
            put("minecraft:flower_forest", new Biome("minecraft", "flower_forest", new Color(0x277C25)));
            put("minecraft:forest", new Biome("minecraft", "forest", new Color(0x056621)));
            put("minecraft:frozen_ocean", new Biome("minecraft", "frozen_ocean", new Color(0xDDE8ED)));
            put("minecraft:frozen_peaks", new Biome("minecraft", "frozen_peaks", new Color(0xF2F2F2)));
            put("minecraft:frozen_river", new Biome("minecraft", "frozen_river", new Color(0x9CD6F2)));
            put("minecraft:grove", new Biome("minecraft", "grove", new Color(0x3D7E46)));
            put("minecraft:ice_spikes", new Biome("minecraft", "ice_spikes", new Color(0xFFFFFF)));
            put("minecraft:jagged_peaks", new Biome("minecraft", "jagged_peaks", new Color(0xCDCDCD)));
            put("minecraft:jungle", new Biome("minecraft", "jungle", new Color(0x337B00)));
            put("minecraft:lukewarm_ocean", new Biome("minecraft", "lukewarm_ocean", new Color(0x00D5FF)));
            put("minecraft:lush_caves", new Biome("minecraft", "lush_caves", new Color(0x5E8A59)));
            put("minecraft:mangrove_swamp", new Biome("minecraft", "mangrove_swamp", new Color(0x51634B)));
            put("minecraft:meadow", new Biome("minecraft", "meadow", new Color(0x4DB049)));
            put("minecraft:mushroom_fields", new Biome("minecraft", "mushroom_fields", new Color(0xFF00FF)));
            put("minecraft:nether_wastes", new Biome("minecraft", "nether_wastes", new Color(0x8B8B8B)));
            put("minecraft:ocean", new Biome("minecraft", "ocean", new Color(0x000030)));
            put("minecraft:old_growth_birch_forest", new Biome("minecraft", "old_growth_birch_forest", new Color(0x506D3A)));
            put("minecraft:old_growth_pine_taiga", new Biome("minecraft", "old_growth_pine_taiga", new Color(0x506D3A)));
            put("minecraft:old_growth_spruce_taiga", new Biome("minecraft", "old_growth_spruce_taiga", new Color(0x506D3A)));
            put("minecraft:plains", new Biome("minecraft", "plains", new Color(0x75A82B)));
            put("minecraft:river", new Biome("minecraft", "river", new Color(0x37507D)));
            put("minecraft:savanna", new Biome("minecraft", "savanna", new Color(0xBDB25F)));
            put("minecraft:savanna_plateau", new Biome("minecraft", "savanna_plateau", new Color(0xBDB25F)));
            put("minecraft:small_end_islands", new Biome("minecraft", "small_end_islands", new Color(0xD8D8D8)));
            put("minecraft:snowy_beach", new Biome("minecraft", "snowy_beach", new Color(0xFAFAFA)));
            put("minecraft:snowy_plains", new Biome("minecraft", "snowy_plains", new Color(0xFAFAFA)));
            put("minecraft:snowy_slopes", new Biome("minecraft", "snowy_slopes", new Color(0xFAFAFA)));
            put("minecraft:snowy_taiga", new Biome("minecraft", "snowy_taiga", new Color(0xD8D8D8)));
            put("minecraft:soul_sand_valley", new Biome("minecraft", "soul_sand_valley", new Color(0x3C3C3C)));
            put("minecraft:sparse_jungle", new Biome("minecraft", "sparse_jungle", new Color(0x1A7C00)));
            put("minecraft:stony_peaks", new Biome("minecraft", "stony_peaks", new Color(0xCDCDCD)));
            put("minecraft:stony_shore", new Biome("minecraft", "stony_shore", new Color(0x8C8C8C)));
            put("minecraft:sunflower_plains", new Biome("minecraft", "sunflower_plains", new Color(0xFFD801)));
            put("minecraft:swamp", new Biome("minecraft", "swamp", new Color(0x2F6E49)));
            put("minecraft:taiga", new Biome("minecraft", "taiga", new Color(0x05703D)));
            put("minecraft:the_end", new Biome("minecraft", "the_end", new Color(0x000000)));
            put("minecraft:the_void", new Biome("minecraft", "the_void", new Color(0x000000)));
            put("minecraft:warm_ocean", new Biome("minecraft", "warm_ocean", new Color(0x00A8FF)));
            put("minecraft:warped_forest", new Biome("minecraft", "warped_forest", new Color(0x13131D)));
            put("minecraft:windswept_forest", new Biome("minecraft", "windswept_forest", new Color(0x3C643C)));
            put("minecraft:windswept_gravelly_hills", new Biome("minecraft", "windswept_gravelly_hills", new Color(0xB2B2B2)));
            put("minecraft:windswept_hills", new Biome("minecraft", "windswept_hills", new Color(0x4D745A)));
            put("minecraft:windswept_savanna", new Biome("minecraft", "windswept_savanna", new Color(0xBDB25F)));
            put("minecraft:wooded_badlands", new Biome("minecraft", "wooded_badlands", new Color(0xFF6D00)));
        }
    };
}
