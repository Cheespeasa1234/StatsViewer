package main;

import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

public class RegionParser {

    private static final int SECTOR_SIZE = 4096;
    private static final int CHUNK_COUNT = 1024;

    public static class Chunk {
        public NamedTag nbt;
        public Locator locator;

        public Chunk(byte[] decompressedData, Locator locator) throws IOException {
            NamedTag nbtTag = new NBTInputStream(new ByteArrayInputStream(decompressedData)).readTag(64);
            this.nbt = nbtTag;
            this.locator = locator;            
        }
        
        public String toNBTString() {
            return this.nbt.getTag().toString(64);
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

    public void parse(String filePath, String dump_location) throws IOException, Exception {

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
                byte[] decompressed = decompressZlib(data);
                chunks[chunkIdx] = new Chunk(decompressed, locators[chunkIdx]);
                // TODO: Dumping
            }
            fis.close();
        }

    }
}
