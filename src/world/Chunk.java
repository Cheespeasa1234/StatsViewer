package world;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NamedTag;
import util.DataParsing;

/**
 * Represents a chunk in the world.
 * Contains the chunk's position, biome map, and structures.
 * 
 * @see Region
 * @see World
 * @author Nate Levison
 */
public class Chunk {

	public int x, y, z;
	public String[] biomePallete;
	public byte[][][] biomeMap;
	public ArrayList<String> structures = new ArrayList<String>();

	/**
	 * Sets the biomePallete and biomeMap during loading.
	 * Responsible for all pallete splitting and sets the state of biomePallete, and biomeMap.
	 * 
	 * @param json the top-level json object of the chunk
	 */
	private void setBiome(JsonObject json) {

		biomeMap = new byte[16][16][16];

		JsonArray sections = json.getAsJsonArray("sections");

		int validSectionIndex = sections.size() - 1;
		while (validSectionIndex >= 0) {
			JsonObject section = sections.get(validSectionIndex).getAsJsonObject();
			if (section.has("biomes")) {
				break;
			}
			validSectionIndex--;
		}
		JsonObject topSection = sections.get(validSectionIndex).getAsJsonObject();
		JsonObject biomeData = topSection.getAsJsonObject("biomes");

		JsonArray palette = biomeData.getAsJsonArray("palette");
		biomePallete = new String[palette.size()];
		for (int i = 0; i < palette.size(); i++) {
			biomePallete[i] = palette.get(i).getAsString();
		}

		// If the pallete is one long, the whole chunk is the same biome
		if (biomePallete.length == 1) {
			return;
		}

		JsonElement rawData = biomeData.get("data");
		long[] rawDataList;
		if (rawData.isJsonArray()) {
			JsonArray rawDataArray = rawData.getAsJsonArray();
			rawDataList = new long[rawDataArray.size()];
			for (int i = 0; i < rawDataArray.size(); i++) {
				rawDataList[i] = rawDataArray.get(i).getAsLong();
			}
		} else {
			rawDataList = new long[1];
			rawDataList[0] = rawData.getAsLong();
		}

		int bitSpace = DataParsing.bitSpaceRequired(biomePallete.length - 1);
		byte[] indices = DataParsing.splitIntegers(rawDataList, bitSpace);

		// there are 64 indexes, they must be evenly distributed to a 16x16x16 grid
		int elementsPerIndex = (16 * 16 * 16) / indices.length;

		// Assign indices to biomeMap
		int index = 0;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					biomeMap[i][j][k] = indices[index / elementsPerIndex];
					index++;
				}
			}
		}

	}

	/**
	 * Adds a structure segment to the list of structures if it is in the chunk.
	 * If so, it is identified as the structure origin and is added.
	 * 
	 * @param structureName the resource name of the structure
	 * @param x the chunk x of the structure segment
	 * @param z the chunk z of the structure segment
	 */
	private void addStructure(String structureName, int x, int z) {
		if (x == this.x && z == this.z) {
			structures.add(structureName);
		}
	}

	/**
	 * Creates a chunk from decompressed NBT data.
	 * The decompressed data must not have any headers either.
	 * The top-level tag is read, and must contain the xPos, yPos, zPos, and structures.
	 * This chunk will be null if it has not been generated in the region.
	 * Throws an IOException either if it can not parse the bytes as NBT.
	 * 
	 * @param regionData the region data
	 * @throws IOException if there is an error converting the data
	 */
	public Chunk(byte[] regionData) throws IOException {
		NamedTag nbtTag = new NBTInputStream(new ByteArrayInputStream(regionData)).readTag(64);

		JsonObject json;
		Gson gson = new Gson();
		json = gson.fromJson(nbtTag.getTag().toString(64), JsonObject.class);
		json = DataParsing.collapse(json).getAsJsonObject();

		this.x = json.get("xPos").getAsInt();
		this.y = json.get("yPos").getAsInt();
		this.z = json.get("zPos").getAsInt();

		this.setBiome(json);
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
						this.addStructure(key, first32, last32);
					}
				} else {
					long position = pos.getAsLong();
					int first32 = (int) (position >> 32);
					int last32 = (int) (position & 0xFFFFFFFF);
					this.addStructure(key, first32, last32);
				}
			}

		} else {
			System.out.println("No references");
		}
	}
}