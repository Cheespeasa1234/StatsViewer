package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

import world.Chunk;
import world.Region;

/**
 * Wrapper componenent for a Region object. Region must already be parsed.
 */
public class WorldMapPanel extends JPanel implements MouseListener, MouseMotionListener {

    private Region region;
    private boolean loaded = false;
    private Point2D mouse = new Point2D.Double(0, 0);
    private Color tooltipBG = new Color(0, 0, 0, 100);

    // Map of biomes to colors
    private static HashMap<String, Color> coloredBiomes = new HashMap<>() {
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

    public WorldMapPanel() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.loaded = false;
        tooltipInterlopationTimer.start();
    }

    // Variables to manage the interpolation of the tooltip
    private int targetTooltipWidth = 0;
    private int targetTooltipHeight = 0;
    private int currentTooltipWidth = 0;
    private int currentTooltipHeight = 0;
    private final double smoothing = 0.5;

    // A separate timer to manage the interpolation of the tooltip
    private Timer tooltipInterlopationTimer = new Timer(50, e -> {
        repaint();

        // If the target width and height are 0, do not interpolate
        // Or if the target width and height have been reached
        if (targetTooltipWidth == 0 && targetTooltipHeight == 0 ||
        targetTooltipWidth == currentTooltipWidth && targetTooltipHeight == currentTooltipHeight) {
            return;
        }

        // Interlopate the width
        if (targetTooltipWidth > currentTooltipWidth) {
            currentTooltipWidth += (targetTooltipWidth - currentTooltipWidth) * smoothing;
        } else {
            currentTooltipWidth -= (currentTooltipWidth - targetTooltipWidth) * smoothing;
        }

        // Interlopate the height
        if (targetTooltipHeight > currentTooltipHeight) {
            currentTooltipHeight += (targetTooltipHeight - currentTooltipHeight) * smoothing;
        } else {
            currentTooltipHeight -= (currentTooltipHeight - targetTooltipHeight) * smoothing;
        }

    });

    /**
     * Set the region to be displayed, and re-render the map
     * @param region the region to display
     */
    public void setRegion(Region region) {
        this.loaded = true;
        this.region = region;
        rendered = render();
    }

    /**
     * Draw the tooltip at the given x and y, with the given lines
     * Dynamically updates width and height, and smooths the transition
     * Should be called from paintComponent
     * @param g2 the graphics object that will be rendered to
     * @param x the x position of the mouse
     * @param y the y position of the mouse
     * @param lines the lines of text to draw
     */
    private void drawTooltip(Graphics2D g2, int x, int y, String[] lines) {
        int maxWidth = 0;
        for (String line : lines) {
            int width = g2.getFontMetrics().stringWidth(line);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        maxWidth += 10;
        int maxHeight = (g2.getFontMetrics().getHeight() + 5) * lines.length;
        targetTooltipWidth = maxWidth;
        targetTooltipHeight = maxHeight;

        g2.setColor(tooltipBG);
        g2.fillRoundRect((int) mouse.getX() + 10, (int) y + 10, currentTooltipWidth, currentTooltipHeight, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect((int) mouse.getX() + 10, (int) y + 10, currentTooltipWidth, currentTooltipHeight, 10, 10);

        int yoff = 10;
        for (String line : lines) {
            g2.drawString(line, (int) mouse.getX() + 15, (int) y + 15 + yoff);
            yoff += g2.getFontMetrics().getHeight() + 5;
        }
    }

    /**
     * Renders the map to a BufferedImage
     * @returns the rendered map
     */
    private BufferedImage render() {
        BufferedImage img = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        // get the region number
        int regionx = region.x;
        int regionz = region.z;
        for (int i = 0; i < region.chunks.length; i++) {
            Chunk chunk = region.chunks[i];

            // If the chunk has not been generated (the Region would not set the chunk)
            if (chunk == null) {
                g2.setColor(Color.RED);
                int x = i % 32;
                int z = i / 32;
                g2.fillRect(x * 16, z * 16, 16, 16);
                continue;
            }

            // Draw the chunk biomes
            int drawx = chunk.x * 16 - regionx * 512;
            int drawz = chunk.z * 16 - regionz * 512;
            for (int z = 0; z < 16; z++) { // Swap x and z in the loop
                for (int x = 0; x < 16; x++) { // Swap x and z in the loop
                    g2.setColor(coloredBiomes.get(chunk.biomePallete[chunk.biomeMap[x / 4][z][0]]));
                    g2.fillRect(drawx + (15 - z), drawz + (15 - x), 1, 1); // Adjust coordinates for rotation
                }
            }

            
            // draw the structure
            drawx = chunk.x * 16 - regionx * 512;
            drawz = chunk.z * 16 - regionz * 512;
            if (chunk.structures != null && chunk.structures.size() > 0) {
                int count = chunk.structures.size();
                int reservedSpace = 16 / count;
                for (int j = 0; j < count; j++) {
                    g2.setColor(Color.BLACK);
                    g2.fillOval(drawx + (j * reservedSpace), drawz + (j * reservedSpace), reservedSpace,
                            reservedSpace);
                }
            }
        }

        return img;
    }

    BufferedImage rendered;

    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (!loaded) {
            g2.drawString("Not loaded.", 10, 10);
        } else {

            // Draw the rendered image
            g2.drawImage(rendered, 0, 0, null);

            // If the mouse is not within a valid area, do not update
            if (mouse.getX() < 0 || mouse.getY() < 0 || mouse.getX() > 512 || mouse.getY() > 512) {
                return;
            }

            // Get the chunk the mouse is on
            int chunkx = (int) Math.floor(mouse.getX() / 16);
            int chunkz = (int) Math.floor(mouse.getY() / 16);
            Chunk chunk = region.chunks[chunkx + chunkz * 32];

            // If the chunk is not generated, set the tooltip to a warning
            if (chunk == null) {
                String[] tooltip = new String[] { "Chunk not generated." };
                drawTooltip(g2, (int) mouse.getX(), (int) mouse.getY(), tooltip);
            } else {

                // Get the hovered block, and get the biome
                int blockx = (int) Math.floor((mouse.getX() - chunkx * 16) / 16 * 16);
                int blockz = (int) Math.floor((mouse.getY() - chunkz * 16) / 16 * 16);

                // Build and display a tooltip
                String biome = chunk.biomePallete[chunk.biomeMap[blockx / 4][blockz][0]];
                String[] tooltip = new String[2 + chunk.structures.size()];
                tooltip[0] = "Biome: " + biome;
                tooltip[1] = "Chunk: " + chunk.x + ", " + chunk.z;
                for (int i = 0; i < chunk.structures.size(); i++) {
                    tooltip[i + 2] = chunk.structures.get(i);
                }
                drawTooltip(g2, (int) mouse.getX(), (int) mouse.getY(), tooltip);
            }
        }

    }

    @Override public void mouseMoved(MouseEvent e) {
        mouse = e.getPoint();
        repaint();
    }

    @Override public void mouseDragged(MouseEvent e) {}

    @Override public void mouseClicked(MouseEvent e) {}

    @Override public void mousePressed(MouseEvent e) {}

    @Override public void mouseReleased(MouseEvent e) {}

    @Override public void mouseEntered(MouseEvent e) {}

    @Override public void mouseExited(MouseEvent e) {}
}
