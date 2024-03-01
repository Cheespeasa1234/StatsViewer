package components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import world.Chunk;
import world.Region;

/**
 * Wrapper componenent for a Region object. Region must already be parsed.
 */
public class WorldMapPanel extends JPanel implements MouseListener, MouseMotionListener {

    private Region region;
    private Point2D mouse = new Point2D.Double(0, 0);
    private Color tooltipBG = new Color(0, 0, 0, 100);
    public BufferedImage rendered;
    public boolean loaded = false;

    public boolean mapRendered() {
        return rendered != null;
    }

    private static HashMap<String, ImageIcon> structureIconCache = new HashMap<>();

    // Map of biomes to colors
    private static HashMap<String, Color> coloredBiomes = new HashMap<>() {
        {
            put("minecraft:badlands", new Color(0xFF6D00));
            put("minecraft:bamboo_jungle", new Color(0x7E9A45));
            put("minecraft:basalt_deltas", new Color(0x2A2829));
            put("minecraft:beach", new Color(0xFADE55));
            put("minecraft:birch_forest", new Color(0x506D3A));
            put("minecraft:cherry_grove", new Color(0xE965BE));
            put("minecraft:crimson_forest", new Color(0x970000));
            put("minecraft:dark_forest", new Color(0x1A361F));
            put("minecraft:deep_cold_ocean", new Color(0x000030));
            put("minecraft:deep_frozen_ocean", new Color(0x7E95B6));
            put("minecraft:deep_lukewarm_ocean", new Color(0x0070FF));
            put("minecraft:deep_ocean", new Color(0x000050));
            put("minecraft:deep_dark", new Color(0x000000));
            put("minecraft:desert", new Color(0xFAAC58));
            put("minecraft:dripstone_caves", new Color(0x9C6E3E));
            put("minecraft:end_barrens", new Color(0xD8D8D8));
            put("minecraft:end_highlands", new Color(0xC8C8C8));
            put("minecraft:end_midlands", new Color(0xA0A0A0));
            put("minecraft:eroded_badlands", new Color(0xFF6D00));
            put("minecraft:flower_forest", new Color(0x277C25));
            put("minecraft:forest", new Color(0x056621));
            put("minecraft:frozen_ocean", new Color(0xDDE8ED));
            put("minecraft:frozen_peaks", new Color(0xF2F2F2));
            put("minecraft:frozen_river", new Color(0x9CD6F2));
            put("minecraft:grove", new Color(0x3D7E46));
            put("minecraft:ice_spikes", new Color(0xFFFFFF));
            put("minecraft:jagged_peaks", new Color(0xCDCDCD));
            put("minecraft:jungle", new Color(0x337B00));
            put("minecraft:lukewarm_ocean", new Color(0x00D5FF));
            put("minecraft:lush_caves", new Color(0x5E8A59));
            put("minecraft:mangrove_swamp", new Color(0x51634B));
            put("minecraft:meadow", new Color(0x4DB049));
            put("minecraft:mushroom_fields", new Color(0xFF00FF));
            put("minecraft:nether_wastes", new Color(0x8B8B8B));
            put("minecraft:ocean", new Color(0x000030));
            put("minecraft:old_growth_birch_forest", new Color(0x506D3A));
            put("minecraft:old_growth_pine_taiga", new Color(0x506D3A));
            put("minecraft:old_growth_spruce_taiga", new Color(0x506D3A));
            put("minecraft:plains", new Color(0x75A82B));
            put("minecraft:river", new Color(0x37507D));
            put("minecraft:savanna", new Color(0xBDB25F));
            put("minecraft:savanna_plateau", new Color(0xBDB25F));
            put("minecraft:small_end_islands", new Color(0xD8D8D8));
            put("minecraft:snowy_beach", new Color(0xFAFAFA));
            put("minecraft:snowy_plains", new Color(0xFAFAFA));
            put("minecraft:snowy_slopes", new Color(0xFAFAFA));
            put("minecraft:snowy_taiga", new Color(0xD8D8D8));
            put("minecraft:soul_sand_valley", new Color(0x3C3C3C));
            put("minecraft:sparse_jungle", new Color(0x1A7C00));
            put("minecraft:stony_peaks", new Color(0xCDCDCD));
            put("minecraft:stony_shore", new Color(0x8C8C8C));
            put("minecraft:sunflower_plains", new Color(0xFFD801));
            put("minecraft:swamp", new Color(0x2F6E49));
            put("minecraft:taiga", new Color(0x05703D));
            put("minecraft:the_end", new Color(0x000000));
            put("minecraft:the_void", new Color(0x000000));
            put("minecraft:warm_ocean", new Color(0x00A8FF));
            put("minecraft:warped_forest", new Color(0x13131D));
            put("minecraft:windswept_forest", new Color(0x3C643C));
            put("minecraft:windswept_gravelly_hills", new Color(0xB2B2B2));
            put("minecraft:windswept_hills", new Color(0x4D745A));
            put("minecraft:windswept_savanna", new Color(0xBDB25F));
            put("minecraft:wooded_badlands", new Color(0xFF6D00));
        }
    };

    public WorldMapPanel() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.loaded = false;
        tooltipInterlopationTimer.start();
    }

    // Variables to manage the interpolation of the tooltip
    private double targetTooltipWidth = 0;
    private double targetTooltipHeight = 0;
    private double currentTooltipWidth = 0;
    private double currentTooltipHeight = 0;

    // The higher the smoothing, the faster it moves
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
        this.setPreferredSize(new Dimension(933, 512));
        this.repaint();
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

        // Check if the tooltip height exceeds the available space below the mouse position
        int screenHeight = getHeight();
        if (y + 10 + maxHeight > screenHeight) {
            y -= maxHeight + 10; // Adjust y position to keep tooltip above the mouse
        }

        targetTooltipWidth = maxWidth;
        targetTooltipHeight = maxHeight;

        g2.setColor(tooltipBG);
        g2.fillRoundRect(x + 10, y + 10, (int) currentTooltipWidth, (int) currentTooltipHeight, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x + 10, y + 10, (int) currentTooltipWidth, (int) currentTooltipHeight, 10, 10);

        int yoff = 10;
        for (String line : lines) {
            g2.drawString(line, x + 15, y + 15 + yoff);
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
                    g2.fillRect(drawx + (15 - z), drawz + (15 - x), 2, 2); // Adjust coordinates for rotation
                }
            }
        }

        for (int i = 0; i < region.chunks.length; i++) {
            Chunk chunk = region.chunks[i];
            // draw the structure
            int drawx = chunk.x * 16 - regionx * 512;
            int drawz = chunk.z * 16 - regionz * 512;
            if (chunk.structures != null && chunk.structures.size() > 0) {
                int count = chunk.structures.size();
                int reservedSpace = 16 / count;
                for (int j = 0; j < count; j++) {
                    String id = chunk.structures.get(j);
                    ImageIcon icon;
                    if (!structureIconCache.containsKey(id)) {
                        File file = new File("src/icons/struct/" + id.replace(":", "_") + ".png");
                        icon = new ImageIcon(file.getAbsolutePath());
                        structureIconCache.put(id, icon);
                    } else {
                        icon = structureIconCache.get(id);
                    }

                    int x = drawx + (j * reservedSpace);
                    int z = drawz + (j * reservedSpace);
                    int w = reservedSpace, h = reservedSpace;
                    if (icon.getImage() == null) {
                        g2.setColor(Color.BLACK);
                        g2.fillOval(x, z, w, h);
                    } else if (id.equals("minecraft:mineshaft") || id.equals("minecraft:buried_treasure")) {
                        g2.drawImage(icon.getImage(), x, z, w, h, null);
                    } else {
                        g2.drawImage(icon.getImage(), x - w / 2, z - h / 2, w * 2, h * 2, null);
                    }
                }
            }
        }

        return img;
    }

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
            g2.setColor(Color.BLACK);
            g2.drawRect(chunkx * 16, chunkz * 16, 16, 16);

            // If the chunk is not generated, set the tooltip to a warning
            if (chunk == null) {
                String[] tooltip = new String[] {
                        "Not generated.",
                        "Chunk " + (chunkx + region.x * 32) + ", " + (chunkz + region.z * 32)
                };
                drawTooltip(g2, (int) mouse.getX(), (int) mouse.getY(), tooltip);
            } else {

                // Get the hovered block, and get the biome
                int blockx = (int) Math.floor((mouse.getX() - chunkx * 16) / 16 * 16);
                int blockz = (int) Math.floor((mouse.getY() - chunkz * 16) / 16 * 16);

                int y = chunk.surfaceHeightmap.getHeight(blockx, blockz);

                // Build and display a tooltip
                String biome = chunk.biomePallete[chunk.biomeMap[blockx / 4][blockz][0]];
                String[] tooltip = new String[3 + chunk.structures.size()];
                tooltip[0] = "Biome: " + biome;
                tooltip[1] = "Block: x=" + blockx + " y=" + y + " z=" + blockz;
                tooltip[2] = "Chunk: x=" + chunkx + " z=" + chunkz;
                for (int i = 0; i < chunk.structures.size(); i++) {
                    tooltip[i + 3] = chunk.structures.get(i);
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
