package player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import main.Lib;

public class MinecraftPlayer {
    @Expose
    @SerializedName("UUID")
    public int[] rawUUID;

    @Expose
    @SerializedName("Pos")
    public double[] pos;

    @Expose
    @SerializedName("Dimension")
    public String dimension;

    @SerializedName("Motion")
    public double[] motion;

    @Expose
    @SerializedName("Rotation")
    public float[] rotation;

    @Expose
    @SerializedName("XpTotal")
    public int xpTotal;

    @Expose
    @SerializedName("Inventory")
    public List<Item> mainInventory;

    @Expose
    @SerializedName("EnderItems")
    public List<Item> enderInventory;

    public transient File serverFile;

    public Map<String, Map<String, Double>> stats;
    public String name;

    @SerializedName("fixedUUID")
    public String UUID;

    public void fixUUID() {
        // turn it into four hex strings
        long mostSigBits = ((long) rawUUID[0] << 32) | (rawUUID[1] & 0xFFFFFFFFL);
        long leastSigBits = ((long) rawUUID[2] << 32) | (rawUUID[3] & 0xFFFFFFFFL);

        UUID fmt = new UUID(mostSigBits, leastSigBits);
        this.UUID = fmt.toString();
    }

    public void addStatsToMinecraftPlayer(File statsFile, File serverFile) throws FileNotFoundException {

        this.serverFile = serverFile;

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

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(parsed, JsonObject.class);
        TypeToken<Map<String, Map<String, Double>>> typeToken = new TypeToken<Map<String, Map<String, Double>>>() {
        };

        this.stats = gson.fromJson(jsonObject, typeToken.getType());

    }

    public String getName() {
        if (this.name == null) {
            return this.UUID;
        }
        return this.name + " (" + this.UUID + ")";
    }
}
