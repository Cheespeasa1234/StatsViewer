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

import util.AssetGlobals;
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
                    g2.setColor(chunk.biomePallete[chunk.biomeMap[x / 4][z][0]].color);
                    g2.fillRect(drawx + (15 - z), drawz + (15 - x), 2, 2); // Adjust coordinates for rotation
                }
            }
        }

        for (int i = 0; i < region.chunks.length; i++) {
            Chunk chunk = region.chunks[i];
            if (chunk == null) {
                continue;
            }
            // draw the structure
            int drawx = chunk.x * 16 - regionx * 512;
            int drawz = chunk.z * 16 - regionz * 512;
            if (chunk.structures != null && chunk.structures.size() > 0) {
                int count = chunk.structures.size();
                int reservedSpace = 16 / count;
                for (int j = 0; j < count; j++) {
                    String id = chunk.structures.get(j);
                    ImageIcon icon = AssetGlobals.structureIcons.get(id);

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
            g2.drawImage(AssetGlobals.hoverIcon1.getImage(), chunkx * 16, chunkz * 16, 16, 16, null);

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
                String biome = chunk.biomePallete[chunk.biomeMap[blockx / 4][blockz][0]].formalizedName;
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
