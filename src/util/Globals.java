package util;

import java.awt.Font;

/**
 * This class contains all the global variables used throughout the program
 * It is used to store the size of the window, the font, and the asset locations
 * It also contains the currently active world and directory
 * @author Nate Levison, February 2024
 */
public class Globals {

    /**
     * Width of the window
     */
    public static final int PREF_W = 1250;
    
    /**
     * Height of the window
     */
    public static final int PREF_H = 750;
    
    /**
     * Height of the top panel, used for the title and buttons
     */
    public static final int TOP_HEIGHT = 75;

    /**
     * Height of the bottom panel, used for the statistics and graphs
     * The bottom panel is the height of the window minus the top panel, minus the bottom panel
     */
    public static final int BOTTOM_HEIGHT = PREF_H - TOP_HEIGHT * 2;
    
    /**
     * Font used for the entire program
     */
    public static final Font FONT_PRIMARY = new Font("Helvetica", Font.PLAIN, 16);
    
    /**
     * Name of the pip instance, changed by the {@link DependencyChecker} class
     * This is the name of the pip instance that will be used to install the required python packages
     * @see DependencyChecker
     * @see DependencyChecker#checkDependencies()
     */
    public static final String RECENTS_FILE_PATH = "recent_directories.txt";
    
    /**
     * The directory to store formatted files by the statsviewer
     * Uses the unix format for hidden directories, but may not actually be hidden
     * Default is /.statsviewer
     */
    public static String STATS_VIEWER_DIRECTORY = "/.statsviewer";
    
    /**
     * The current world being read
     * Changed by the {@link pages.BlankPanel} file chooser
     * @see BlankPanel
     */
    public static String OPEN_WORLD_NAME = "";
}
