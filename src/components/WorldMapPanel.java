package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import util.Globals;
import util.Utility;
import world.Chunk;
import world.RegionParser;
import world.World;

public class WorldMapPanel extends JPanel {
    public World world;
    public boolean loaded = false;

    public static HashMap<String, Color> coloredBiomes = new HashMap<>() {
        {
            put("minecraft:badlands", Color.decode("#FF6D00"));
            put("minecraft:bamboo_jungle", Color.decode("#7E9A45"));
            put("minecraft:basalt_deltas", Color.decode("#2A2829"));
            put("minecraft:beach", Color.decode("#FADE55"));
            put("minecraft:birch_forest", Color.decode("#506D3A"));
            put("minecraft:cherry_grove", Color.decode("#E965BE"));
            put("minecraft:crimson_forest", Color.decode("#970000"));
            put("minecraft:dark_forest", Color.decode("#1A361F"));
            put("minecraft:deep_cold_ocean", Color.decode("#000030"));
            put("minecraft:deep_frozen_ocean", Color.decode("#7E95B6"));
            put("minecraft:deep_lukewarm_ocean", Color.decode("#0070FF"));
            put("minecraft:deep_ocean", Color.decode("#000050"));
            put("minecraft:deep_dark", Color.decode("#000000"));
            put("minecraft:desert", Color.decode("#FAAC58"));
            put("minecraft:dripstone_caves", Color.decode("#9C6E3E"));
            put("minecraft:end_barrens", Color.decode("#D8D8D8"));
            put("minecraft:end_highlands", Color.decode("#C8C8C8"));
            put("minecraft:end_midlands", Color.decode("#A0A0A0"));
            put("minecraft:eroded_badlands", Color.decode("#FF6D00"));
            put("minecraft:flower_forest", Color.decode("#277C25"));
            put("minecraft:forest", Color.decode("#056621"));
            put("minecraft:frozen_ocean", Color.decode("#DDE8ED"));
            put("minecraft:frozen_peaks", Color.decode("#F2F2F2"));
            put("minecraft:frozen_river", Color.decode("#9CD6F2"));
            put("minecraft:grove", Color.decode("#3D7E46"));
            put("minecraft:ice_spikes", Color.decode("#FFFFFF"));
            put("minecraft:jagged_peaks", Color.decode("#CDCDCD"));
            put("minecraft:jungle", Color.decode("#337B00"));
            put("minecraft:lukewarm_ocean", Color.decode("#00D5FF"));
            put("minecraft:lush_caves", Color.decode("#5E8A59"));
            put("minecraft:mangrove_swamp", Color.decode("#51634B"));
            put("minecraft:meadow", Color.decode("#4DB049"));
            put("minecraft:mushroom_fields", Color.decode("#FF00FF"));
            put("minecraft:nether_wastes", Color.decode("#8B8B8B"));
            put("minecraft:ocean", Color.decode("#000030"));
            put("minecraft:old_growth_birch_forest", Color.decode("#506D3A"));
            put("minecraft:old_growth_pine_taiga", Color.decode("#506D3A"));
            put("minecraft:old_growth_spruce_taiga", Color.decode("#506D3A"));
            put("minecraft:plains", Color.decode("#75A82B"));
            put("minecraft:river", Color.decode("#37507D"));
            put("minecraft:savanna", Color.decode("#BDB25F"));
            put("minecraft:savanna_plateau", Color.decode("#BDB25F"));
            put("minecraft:small_end_islands", Color.decode("#D8D8D8"));
            put("minecraft:snowy_beach", Color.decode("#FAFAFA"));
            put("minecraft:snowy_plains", Color.decode("#FAFAFA"));
            put("minecraft:snowy_slopes", Color.decode("#FAFAFA"));
            put("minecraft:snowy_taiga", Color.decode("#D8D8D8"));
            put("minecraft:soul_sand_valley", Color.decode("#3C3C3C"));
            put("minecraft:sparse_jungle", Color.decode("#1A7C00"));
            put("minecraft:stony_peaks", Color.decode("#CDCDCD"));
            put("minecraft:stony_shore", Color.decode("#8C8C8C"));
            put("minecraft:sunflower_plains", Color.decode("#FFD801"));
            put("minecraft:swamp", Color.decode("#2F6E49"));
            put("minecraft:taiga", Color.decode("#05703D"));
            put("minecraft:the_end", Color.decode("#000000"));
            put("minecraft:the_void", Color.decode("#000000"));
            put("minecraft:warm_ocean", Color.decode("#00A8FF"));
            put("minecraft:warped_forest", Color.decode("#13131D"));
            put("minecraft:windswept_forest", Color.decode("#3C643C"));
            put("minecraft:windswept_gravelly_hills", Color.decode("#B2B2B2"));
            put("minecraft:windswept_hills", Color.decode("#4D745A"));
            put("minecraft:windswept_savanna", Color.decode("#BDB25F"));
            put("minecraft:wooded_badlands", Color.decode("#FF6D00"));
        }
    };

	public void reset(World world, String regionFileName) throws IOException, Exception { 
		this.world = world;

        File region = new File(Globals.SERVER_DIRECTORY + "/" + Globals.OPEN_WORLD_NAME + "/region/" + regionFileName);
        System.out.println("Region file: " + region.getAbsolutePath());
        File[] regionFiles = {
                region
        };
        world.setRegionFiles(regionFiles, () -> {
            this.loaded = true;
            this.repaint();
        });
	}

    public WorldMapPanel(World world, String regionFileName) throws IOException, Exception {
        this.world = world;

        File region = new File(Globals.SERVER_DIRECTORY + "/" + Globals.OPEN_WORLD_NAME + "/region/" + regionFileName);
        System.out.println("Region file: " + region.getAbsolutePath());
        File[] regionFiles = {
                region
        };
        world.setRegionFiles(regionFiles, () -> {
            this.loaded = true;
            this.repaint();
        });
    }

    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int width = this.getWidth();
        int height = this.getHeight();

        if (!loaded) {
            return;
        }

        int region = 0;
        for (int i = 0; i < world.regions[region].chunks.length; i++) {
            Chunk chunk = world.regions[region].chunks[i];
            String biome = chunk.biome;
            g2.setColor(coloredBiomes.get(biome));
            int drawx = chunk.x * 16;
            int drawz = chunk.z * 16;
            g2.fillRect(drawx, drawz, 16, 16);

			// draw the structure
			if (chunk.structures != null && chunk.structures.size() > 0) {
				int count = chunk.structures.size();
				int reservedSpace = 16 / count;
				for (int j = 0; j < count; j++) {
					g2.setColor(Color.BLACK);
					g2.fillOval(drawx + (j * reservedSpace), drawz + (j * reservedSpace), reservedSpace, reservedSpace);
				}
			}
        }
    }
}
