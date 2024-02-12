package world;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.json.JSONObject;

import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

public class RegionParser {

    private static final int SECTOR_SIZE = 4096;
    private static final int CHUNK_COUNT = 1024;

    public static class Chunk {
        // data in nbt form
        public CompoundTag nbt;
        
        public Locator locator;

        public int chunkX, chunkZ;


        public Chunk(byte[] decompressedData, Locator locator) throws IOException {
            NamedTag nbtTag = new NBTInputStream(new ByteArrayInputStream(decompressedData)).readTag(64);
            this.nbt = (CompoundTag) nbtTag.getTag();
            this.locator = locator;            
        }
        
        public String toNBTString() {
            return this.nbt.toString(64);
        }

        public void parseNBT() {
            if (this.nbt == null) throw new NullPointerException("NBT data has not been parsed yet");
            
            // parse important info
            this.chunkX = this.nbt.getInt("xPos");
            this.chunkZ = this.nbt.getInt("zPos");
        }

        public String getBiome() {
            
            HashMap<String, Integer> biomeCounts = new HashMap<String, Integer>();

            ListTag<?> sections = this.nbt.getListTag("sections");
            for (Tag<?> section : sections) {
                CompoundTag sec = (CompoundTag) section;
                CompoundTag biomes = sec.getCompoundTag("biomes");
                if (biomes == null) return "null";
                ListTag<?> palette = biomes.getListTag("palette");
                StringTag biome = (StringTag) palette.get(0);
                String biomeName = biome.getValue();
                if (biomeCounts.containsKey(biomeName)) {
                    biomeCounts.put(biomeName, biomeCounts.get(biomeName) + 1);
                } else {
                    biomeCounts.put(biomeName, 1);
                }
            }

            String mostCommonBiome = "";
            int mostCommonCount = 0;
            for (String biome : biomeCounts.keySet()) {
                int count = biomeCounts.get(biome);
                if (count > mostCommonCount) {
                    mostCommonCount = count;
                    mostCommonBiome = biome;
                }
            }

            return mostCommonBiome;
        }
    }

    public static class Locator {
        public byte offx4, offx2, offx1, sectorCount;

        public Locator(byte offx4, byte offx2, byte offx1, byte sectorCount) {
            this.offx4 = offx4;
            this.offx2 = offx2;
            this.offx1 = offx1;
            this.sectorCount = sectorCount;
        }

        public int offsetBytes() {
            int result = (offx4 & 0xFF) << 16 |
                        (offx2 & 0xFF) << 8  |
                        (offx1 & 0xFF);
            return result * SECTOR_SIZE;
        }

        public int sizeBytes() {
            return sectorCount * SECTOR_SIZE;
        }

        @Override public String toString() {
            return String.format("ChunkLocator[0x%02X 0x%02X 0x%02X 0x%02X]", offx4, offx2, offx1, sectorCount);
        }
    }

    private Chunk[] chunks = new Chunk[CHUNK_COUNT];
    private Locator[] locators = new Locator[CHUNK_COUNT];
    private byte[] header = new byte[SECTOR_SIZE];

    public byte[] decompressZlib(byte[] compressedData) {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(compressedData);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compressedData.length);

            byte[] buffer = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }

            outputStream.close();
            inflater.end();

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void parse(String filePath, String dumpLocation) throws IOException, Exception {

        System.out.println("Parsing " + filePath);
        System.out.println();

        // Open the file
        FileInputStream fis = new FileInputStream(filePath);

        // Read the locators, skip timestamps
        fis.read(header);
        fis.skip(SECTOR_SIZE);
        fis.close();
        for (int byteIdx = 0; byteIdx < header.length; byteIdx += 4) {
            int locatorIdx = byteIdx / 4;
            locators[locatorIdx] = new Locator(header[byteIdx], header[byteIdx + 1], header[byteIdx + 2], header[byteIdx + 3]);
        }

        double decompressTime = 0;
        double parseTime = 0;
        double validateTime = 0;
        double writeTime = 0;

        // Read the chunks
        for (int chunkIdx = 0; chunkIdx < CHUNK_COUNT; chunkIdx++) {
            fis = new FileInputStream(filePath);
            fis.skip(locators[chunkIdx].offsetBytes());

            byte[] metadata = new byte[5];
            fis.read(metadata);

            byte[] data = new byte[locators[chunkIdx].sizeBytes()];
            fis.read(data);

            byte compression = metadata[4];
            if (compression != 0x02) {
                System.err.println("Invalid compression type: " + String.format("0x%02X", compression));
                System.exit(-1);
            } else {

                // decompress the data
                long start = System.currentTimeMillis();
                byte[] decompressed = decompressZlib(data);
                Chunk chunk = new Chunk(decompressed, locators[chunkIdx]);
                chunks[chunkIdx] = chunk;
                decompressTime += System.currentTimeMillis() - start;

                // parse the NBT data
                start = System.currentTimeMillis();
                chunk.parseNBT();
                parseTime += System.currentTimeMillis() - start;

                // make sure the file and the folder exist
                start = System.currentTimeMillis();
                File folder = new File(dumpLocation);
                File dump = new File(dumpLocation + "/chunk_" + chunk.chunkX + "_" + chunk.chunkZ + ".json");
                folder.mkdirs();
                dump.createNewFile();
                validateTime += System.currentTimeMillis() - start;

                // write to the file
                // start = System.currentTimeMillis();
                // BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(dump));
                // writer.write(chunk.toNBTString());
                // writer.close();
                // writeTime += System.currentTimeMillis() - start;

            }

            fis.close();

            double progress = (double) chunkIdx / CHUNK_COUNT;
            System.out.printf("\r%.2f%%", progress * 100);
            System.out.flush();
        }
        System.out.println("\nDone");

        double averageDecompress = decompressTime / CHUNK_COUNT;
        double averageParse = parseTime / CHUNK_COUNT;
        double averageValidate = validateTime / CHUNK_COUNT;
        double averageWrite = writeTime / CHUNK_COUNT;

        System.out.println("Average decompress time: " + averageDecompress + "ms");
        System.out.println("Average parse time: " + averageParse + "ms");
        System.out.println("Average validate time: " + averageValidate + "ms");
        System.out.println("Average write time: " + averageWrite + "ms");

    }

    public static void main(String[] args) {
        RegionParser parser = new RegionParser();
        try {
            parser.parse("D:\\decoy_server\\region\\r.0.0.mca", "D:\\decoy_server\\region_dump\\r.0.0\\");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
