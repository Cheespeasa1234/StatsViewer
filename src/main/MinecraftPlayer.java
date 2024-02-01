package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MinecraftPlayer {

    public class Stats {
        public HashMap<String, Double> killed = new HashMap<String, Double>();
        public HashMap<String, Double> killed_by = new HashMap<String, Double>();
        public HashMap<String, Double> used = new HashMap<String, Double>();
        public HashMap<String, Double> crafted = new HashMap<String, Double>();
        public HashMap<String, Double> dropped = new HashMap<String, Double>();
        public HashMap<String, Double> picked_up = new HashMap<String, Double>();
        public HashMap<String, Double> mined = new HashMap<String, Double>();
        public HashMap<String, Double> broken = new HashMap<String, Double>();
        public HashMap<String, Double> custom = new HashMap<String, Double>();
        public List<HashMap<String, Double>> statsGroups = List.of(
                killed, killed_by, used, crafted, dropped, picked_up, mined, broken, custom);
        public List<String> statsGroupsNames = List.of(
                "killed", "killed_by", "used", "crafted", "dropped", "picked_up", "mined", "broken", "custom");
    }

    public String UUID;
    public Stats stats = new Stats();

    public MinecraftPlayer(File statsFile) throws FileNotFoundException {

        // Format the statistics before proper parsing
        Lib.getInstance().execute(
                "python3",
                "src/format-stat.py",
                "-I",
                statsFile.getAbsolutePath(),
                statsFile.getAbsolutePath().replace("world", ".statsviewer/world"));

        // Parse the document
        this.UUID = statsFile.getName().substring(9, 36);
        Scanner fileScanner = new Scanner(statsFile);
        String parsed = "";
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            parsed += line; // removes newlines
        }
        fileScanner.close();

        int currentStatGroup = -2;
        List<String> tokens = Lib.getInstance().splitPressedJSON(parsed);
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            // if this is defining a new category
            if (token.equals(":{")) {
                currentStatGroup++;
            } else if (token.startsWith(":")) {
                String key = tokens.get(i - 1);
                String value = token.substring(1);
                if (value.endsWith("}"))
                    value = value.substring(0, value.indexOf('}'));
                stats.statsGroups.get(currentStatGroup).put(key, Double.parseDouble(value));
            }
        }

        for (int i = 0; i < stats.statsGroups.size(); i++) {
            HashMap<String, Double> group = stats.statsGroups.get(i);
            String groupName = stats.statsGroupsNames.get(i);
            System.out.println(this.UUID + "." + groupName + ": " + group.toString());
        }

    }
}
