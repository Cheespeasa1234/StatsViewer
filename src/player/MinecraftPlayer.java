package player;

import java.io.File;
import java.io.FileNotFoundException;
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
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import main.Globals;
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
                Globals.PYTHON_INSTANCE,
                "src/format-stat.py",
                "-I",
                statsFile.getAbsolutePath(),
                statsFile.getAbsolutePath().replace(Globals.worldName, ".statsviewer/" + Globals.worldName));

        // Parse the document
        Scanner fileScanner = new Scanner(statsFile);
        String parsed = "";
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            parsed += line; // removes newlines
        }
        fileScanner.close();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<Map<String, Map<String, Double>>>() {
                }.getType(), new CustomMapDeserializer())
                .create();

        Map<String, Map<String, Double>> stats = gson.fromJson(parsed, new TypeToken<Map<String, Map<String, Double>>>() {
        }.getType());
        this.stats = stats;

        Lib.copyTextToClipboard(gson.toJson(stats));

    }

    public class CustomMapDeserializer implements JsonDeserializer<Map<String, Map<String, Double>>> {

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
