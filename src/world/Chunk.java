package world;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.NBTInputStream;
import util.DataParsing;

public class Chunk {

	public int x, y, z;
	public String biome;
	public String[] biomePallete;
	public byte[][] biomeMap;
	public ArrayList<String> structures = new ArrayList<String>();

	public void setBiome(JsonObject json) {

		JsonArray sections = json.getAsJsonArray("sections");
		JsonElement topSection = sections.get(sections.size() - 1);
		JsonElement biomeData = topSection.get("biomes").getAsJsonObject("biomes");


		
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