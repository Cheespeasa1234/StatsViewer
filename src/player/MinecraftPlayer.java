package player;

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

import com.google.gson.Gson;

import main.Lib;

public class MinecraftPlayer {

    public List<Item> mainInventory;
    public List<Item> enderInventory;
    public String UUID;
    public Stats stats = new Stats();
    public File serverFile;

    public MinecraftPlayer(File statsFile, File serverFile) throws FileNotFoundException {

        this.serverFile = serverFile;
        this.UUID = statsFile.getName().substring(9, 36);
        
        // set the inventories
        File playerDataFile = new File(statsFile.getAbsolutePath().replace("world/stats", ".statsviewer/world/playerdata"));
        List<List<Item>> parsedItems = Item.getItems(playerDataFile.getAbsolutePath());
        
        // System.out.println("UUID: " + this.UUID);
        // Item.printInventoryContents(parsedItems);
        // System.out.println();

        this.mainInventory = parsedItems.get(0);
        this.enderInventory = parsedItems.get(1);

        // Format the statistics before proper parsing
        Lib.execute(
            "python3",
            "src/format-stat.py",
            "-I",
            statsFile.getAbsolutePath(),
            statsFile.getAbsolutePath().replace("world", ".statsviewer/world"));


        // Parse the document
        Scanner fileScanner = new Scanner(statsFile);
        String parsed = "";
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            parsed += line; // removes newlines
        }
        fileScanner.close();

        // Parse the JSON
        int currentStatGroup = 0;
        List<String> tokens = Lib.splitPressedJSON(parsed);
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            // if this is the token right before "stats", skip
            if (token.equals("{")) {
                i += 3;
                continue;
            } else if (token.equals(":{")) {
                String groupName = tokens.get(i - 1).split(":")[1];
                groupName = groupName.substring(0, groupName.length() - 1);
                currentStatGroup = stats.statsGroupsNames.indexOf(groupName);
            } else if (token.startsWith(":")) {
                String key = tokens.get(i - 1);
                String value = token.substring(1);
                if (value.endsWith("}"))
                    value = value.substring(0, value.indexOf('}'));
                stats.statsGroups.get(currentStatGroup).put(key, Double.parseDouble(value));
            }
        }

    }

    public String getName() {
        File usercache = new File(serverFile.getAbsolutePath() + "/usercache.json");
        // use Gson to parse the usercache
        // read the file as one string
        String usercacheString = Lib.fileToString(usercache.getAbsolutePath());
        Gson gson = new Gson();
        
        return this.UUID;
    }
}
