package main;

import java.awt.Font;

public class Globals {
    // Size of the window
    public static final int PREF_W = 1250;
    public static final int PREF_H = 750;
    
    // Size of relative components
    public static final int TOP_HEIGHT = 75;
    public static final int BOTTOM_HEIGHT = PREF_H - TOP_HEIGHT * 2;
    
    // Asset locations
    public static final Font FONT_PRIMARY = new Font("Helvetica", Font.PLAIN, 16);
    public static String PYTHON_INSTANCE = "python";
    public static final String RECENTS_FILE_PATH = "recent_directories.txt";
    
    // Currently active world and directory
    public static String STATS_VIEWER_DIRECTORY = "/.statsviewer";
    public static String OPEN_WORLD_NAME = "";
}
