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
import world.Region.Locator;

import util.Utility;

public class Chunk {

	public int x, y, z;
	public String biome;
	public String[] biomePallete;
	public byte[][] biomeMap;
	public ArrayList<String> structures = new ArrayList<String>();

	public void setBiome(JsonObject json) {

		JsonArray sections = json.getAsJsonArray("sections");
		JsonObject section = sections.get(10).getAsJsonObject();
		if (!section.has("biomes")) {
			biomeMap = new byte[16][16];
			biomePallete = new String[1];
			System.out.println("NO BIOMES NO BIOMES NO BIOMES NO BIOMES");
			return;
		}
		JsonObject biomes = section.getAsJsonObject("biomes");
		JsonArray pallete = biomes.getAsJsonArray("palette");

		// get the biome pallete
		biomePallete = new String[pallete.size()];
		for (int j = 0; j < pallete.size(); j++) {
			biomePallete[j] = pallete.get(j).getAsString();
		}

		if (biomePallete.length == 1) {
			System.out.println("BIOME PALLETE IS 1 LONG! SETTING TO HOMOGENEOUS");
			biomeMap = new byte[16][16];
			return;
		}

		JsonElement compressed = biomes.get("data");
		long[] data;
		// if it's a list, get the list
		if (compressed.isJsonArray()) {
			JsonArray compressedArray = compressed.getAsJsonArray();
			data = new long[compressedArray.size()];
			for (int i = 0; i < compressedArray.size(); i++) {
				data[i] = compressedArray.get(i).getAsLong();
			}
		} else {
			data = new long[1];
			data[0] = compressed.getAsLong();
		}

		byte[] indices = DataParsing.splitIntegers(data, DataParsing.bitSpaceRequired(biomePallete.length));
		if (indices.length != 64) {
			System.out.println("BIOME MAP IS NOT 64 LONG! SETTING TO VOID");
			biomeMap = new byte[16][16];
			return;
		}

		// there are 64 indicies returned, map them to sections of 2x2 in a 16x16 grid
		biomeMap = halfResolution(indices);

	}

	public static byte[][] halfResolution(byte[] originalArray) {
		
		if (originalArray.length != 64) throw new IllegalArgumentException("Array must be 64 long");
		
        byte[][] newArray = new byte[16][16];
        
		for (int i = 0; i < 64; i++) {
			int x = i % 16;
			int z = i / 16;
			int x2 = x / 2;
			int z2 = z / 2;
			newArray[x2][z2] = originalArray[i];
		}
        
        return newArray;
    }

	private void addStructure(String structureName, int x, int z) {
		if (x == this.x && z == this.z) {
			structures.add(structureName);
		}
	}

	public Chunk(byte[] regionData) throws IOException {
		NamedTag nbtTag = new NBTInputStream(new ByteArrayInputStream(regionData)).readTag(64);

		JsonObject json;
		Gson gson = new Gson();
		json = gson.fromJson(nbtTag.getTag().toString(64), JsonObject.class);
		json = DataParsing.collapse(json).getAsJsonObject();

		this.x = json.get("xPos").getAsInt();
		this.y = json.get("yPos").getAsInt();
		this.z = json.get("zPos").getAsInt();

		setBiome(json);
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