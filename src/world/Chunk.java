package world;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.NBTInputStream;
import util.DataParsing;
import world.RegionParser.Locator;

import util.Utility;

public class Chunk {

    public int x, y, z;
    public String biome;
	public ArrayList<String> structures = new ArrayList<String>();

    public String getBiome(JsonObject json) {

        HashMap<String, Integer> biomeCount = new HashMap<String, Integer>();

        JsonArray sections = json.getAsJsonArray("sections");
        for (int i = 0; i < sections.size(); i++) {
            int x = i % 16;
            int z = i / 16;
            JsonObject section = sections.get(i).getAsJsonObject();
            JsonObject biomes = section.getAsJsonObject("biomes");
            if (biomes == null) {
                continue;
            }
            JsonArray pallete = biomes.getAsJsonArray("palette");
            String biomeName = pallete.get(0).getAsString();
            if (biomeCount.containsKey(biomeName)) {
                biomeCount.put(biomeName, biomeCount.get(biomeName) + 1);
            } else {
                biomeCount.put(biomeName, 1);
            }
        }

        int max = 0;
        String maxBiome = "";
        for (String biome : biomeCount.keySet()) {
            if (biomeCount.get(biome) > max) {
                max = biomeCount.get(biome);
                maxBiome = biome;
            }
        }

        return maxBiome;
    }

	private void addStructure(String structureName, int x, int z) {
		if (x == this.x && z == this.z) {
			structures.add(structureName);
		}
	}

	public void setRegionData(byte[] regionData) throws IOException {
		NamedTag nbtTag = new NBTInputStream(new ByteArrayInputStream(regionData)).readTag(64);
	
		JsonObject json;
		Gson gson = new Gson();
		json = gson.fromJson(nbtTag.getTag().toString(64), JsonObject.class);
		json = DataParsing.collapse(json).getAsJsonObject();
	
		this.x = json.get("xPos").getAsInt();
		this.y = json.get("yPos").getAsInt();
		this.z = json.get("zPos").getAsInt();
	
		this.biome = getBiome(json);
		JsonObject structures = json.getAsJsonObject("structures");

		if (structures.has("References")) {
			JsonObject references = structures.getAsJsonObject("References");
			// get every key
			for (String key : references.keySet()) {
				JsonElement pos = references.get(key);
				if (pos.isJsonArray()) {
					JsonArray positions = pos.getAsJsonArray();
					for (int i = 0; i < positions.size(); i++) {
						long position = positions.get(i).getAsLong();
						int first32 = (int) (position >> 32);
						int last32 = (int) (position & 0xFFFFFFFF);
						addStructure(key, first32, last32);
					}
				} else {
					long position = pos.getAsLong();
					int first32 = (int) (position >> 32);
					int last32 = (int) (position & 0xFFFFFFFF);
					addStructure(key, first32, last32);
				}
			}

		} else {
			System.out.println("No references");
		}
	}
}