package main;

import java.awt.Font;

public class Globals {
    public static final int PREF_W = 1250;
    public static final int PREF_H = 750;
    public static final int TOP_HEIGHT = 75;
    public static final int BOTTOM_HEIGHT = PREF_H - TOP_HEIGHT * 2;
    public static final Font FONT_PRIMARY = new Font("Helvetica", Font.PLAIN, 16);
    public static String PYTHON_INSTANCE = "python";
    public static String STATS_VIEWER_DIRECTORY = "/.statsviewer";
    public static String worldName = "";
    public static final String RECENTS_FILE_PATH = "recent_directories.txt";
}
