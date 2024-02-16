package components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import util.Globals;
import util.Utility;
import world.Chunk;
import world.RegionParser;
import world.World;

public class WorldMapPanel extends JPanel {
    public World world;
    public WorldMapPanel(World world) throws IOException, Exception {
        this.world = world;

        this.add(new JLabel("Loading..."));
        File region = new File(Globals.SERVER_DIRECTORY + "/" + Globals.OPEN_WORLD_NAME + "/region/r.0.0.mca");
        System.out.println("Region file: " + region.getAbsolutePath());
        File[] regionFiles = { region };
        world.setRegionFiles(regionFiles);
    }
    
    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int width = this.getWidth();
        int height = this.getHeight();

        int region = 0;
        for (Chunk chunk : world.regions[region].chunks) {
            int x = chunk.x * 16;
            int z = chunk.z * 16;
            g2.drawRect(x, z, 16, 16);
            g2.drawString(chunk.x + "," + chunk.z, x, z);
        }
    }
}
