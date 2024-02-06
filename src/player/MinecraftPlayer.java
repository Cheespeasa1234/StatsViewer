package player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serial;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import main.Globals;
import main.Lib;

public class MinecraftPlayer {

    @Expose
    @SerializedName("SpawnX")
    public double spawnX;

    @Expose
    @SerializedName("SpawnY")
    public double spawnY;

    @Expose
    @SerializedName("SpawnZ")
    public double spawnZ;

    @Expose
    @SerializedName("UUID")
    public int[] rawUUID;

    @Expose
    @SerializedName("Pos")
    public double[] position;

    @Expose
    @SerializedName("Dimension")
    public String dimension;

    @SerializedName("Motion")
    public double[] velocity;

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

    // Do not expose this property to the serializer
    public transient File serverFile;

    // Properties that the program finds later, not GSON's problem
    public Map<String, Map<String, Double>> stats;
    public Map<String, Advancement> advancements;
    @SerializedName("displayName")
    public String name;
    @SerializedName("fixedUUID")
    public String UUID;

    /**
     * Sets the player's UUID, based on the four UUID numbers given in the
     * deserializer
     * In a player's JSON, four signed integers are defined. When parsed to hex and
     * cropped, they make a UUID
     * A player's UUID is different than the name used in the file names, in some
     * rare cases
     * 
     * @return void
     */
    public void fixUUID() {
        // turn it into four hex strings
        long mostSigBits = ((long) rawUUID[0] << 32) | (rawUUID[1] & 0xFFFFFFFFL);
        long leastSigBits = ((long) rawUUID[2] << 32) | (rawUUID[3] & 0xFFFFFFFFL);
        UUID fmt = new UUID(mostSigBits, leastSigBits); // apparently this is built in? Lol
        this.UUID = fmt.toString();
    }

    /**
     * Sets the player's statistics, from a file given
     * 
     * @param statsFile
     *            The exact .dat file of the player
     * @param serverFile
     *            The server directory
     */
    public void addStatsToMinecraftPlayer(File statsFile, File serverFile) throws FileNotFoundException {

        this.serverFile = serverFile;

        // Format the statistics before proper parsing
        Lib.convertNBT(
                statsFile.getAbsolutePath(),
                statsFile.getAbsolutePath().replace(Globals.OPEN_WORLD_NAME, ".statsviewer/" + Globals.OPEN_WORLD_NAME));

        // Parse the document
        Scanner fileScanner = new Scanner(statsFile);
        String parsed = "";
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            parsed += line; // removes newlines
        }
        fileScanner.close();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<Map<String, Map<String, Double>>>() {}.getType(), new StatisticsDeserializer())
                .create();

        Map<String, Map<String, Double>> resultMap = gson.fromJson(parsed, new TypeToken<Map<String, Map<String, Double>>>() {}.getType());

        this.stats = resultMap;
    }

    public void addAdvancementsToMinecraftPlayer(File advancementsFile, File serverFile) throws FileNotFoundException {
        this.serverFile = serverFile;

        // Parse the document
        Scanner fileScanner = new Scanner(advancementsFile);
        String parsed = "";
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            parsed += line; // removes newlines
        }
        fileScanner.close();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<Map<String, Advancement>>() {}.getType(), new AdvancementsDeserializer())
                .create();

        Map<String, Advancement> resultMap = gson.fromJson(parsed, new TypeToken<Map<String, Advancement>>() {}.getType());

        this.advancements = resultMap;

    }

    public class AdvancementsDeserializer implements JsonDeserializer<Map<String, Advancement>> {

        @Override
        public Map<String, Advancement> deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            Map<String, Advancement> result = new HashMap<>();
            JsonObject jsonObject = json.getAsJsonObject();


                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    String key = entry.getKey();
                    JsonElement value = entry.getValue();

                    if (value.isJsonObject()) {
                        // If the value is a JsonObject, deserialize it as usual
                        result.put(key, context.deserialize(value, Advancement.class));
                    } else if (value.isJsonPrimitive()) {
                        // continue ;)
                    } else {
                        throw new JsonParseException("Unexpected JSON structure for key: " + key);
                    }
                }

            return result;
        }

    }

    public class StatisticsDeserializer implements JsonDeserializer<Map<String, Map<String, Double>>> {

        @Override
        public Map<String, Map<String, Double>> deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            Map<String, Map<String, Double>> result = new HashMap<>();
            JsonObject jsonObject = json.getAsJsonObject();

            if (jsonObject.has("stats")) {
                JsonObject statsObject = jsonObject.getAsJsonObject("stats");

                for (Map.Entry<String, JsonElement> entry : statsObject.entrySet()) {
                    String key = entry.getKey();
                    JsonElement value = entry.getValue();

                    if (value.isJsonObject()) {
                        // If the value is a JsonObject, deserialize it as usual
                        result.put(key, context.deserialize(value, Map.class));
                    } else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
                        // If the value is a JsonPrimitive and is a number, create a Map with a single
                        // entry
                        Map<String, Double> singleEntryMap = new HashMap<>();
                        singleEntryMap.put(key, value.getAsDouble());
                        result.put(key, singleEntryMap);
                    } else {
                        throw new JsonParseException("Unexpected JSON structure for key: " + key);
                    }
                }
            } else {
                throw new JsonParseException("JSON does not contain the 'stats' key");
            }

            return result;
        }

    }

    public String getName() {
        if (this.name == null) {
            return this.UUID;
        }
        return this.name + " (" + this.UUID + ")";
    }
}
