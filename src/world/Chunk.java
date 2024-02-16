package world;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.NBTInputStream;
import util.DataParsing;
import world.RegionParser.Locator;

public class Chunk {

    public Locator locator;
    public int x, y, z;

    public Chunk(byte[] decompressedData, Locator locator) throws IOException {
        this.locator = locator;
        NamedTag nbtTag = new NBTInputStream(new ByteArrayInputStream(decompressedData)).readTag(64);

        JsonObject json;

        Gson gson = new Gson();
        json = gson.fromJson(nbtTag.getTag().toString(64), JsonObject.class);
        json = DataParsing.collapse(json).getAsJsonObject();

        this.x = json.get("xPos").getAsInt();
        this.y = json.get("yPos").getAsInt();
        this.z = json.get("zPos").getAsInt();
    }
}