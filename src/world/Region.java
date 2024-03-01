package world;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import main.DialogManager;
import util.DataParsing;

/**
 * Represents a region in the world.
 * Contains the region's position, chunks, and locators.
 * Parses region file chunk by chunk. Region owner is responsible for updating the progress bar, and
 * checking if the file is finished.
 *
 * @see Region#isFinished()
 * @see Region#consumeChunk()
 * @see Chunk
 * @see World
 * @author Nate Levison
 */
public class Region {

    private static final int SECTOR_SIZE = 4096;
    private static final int CHUNK_COUNT = 1024;

    public int x, z;

    public Chunk[] chunks = new Chunk[CHUNK_COUNT];
    private Locator[] locators = new Locator[CHUNK_COUNT];
    private byte[] header = new byte[SECTOR_SIZE];

    /**
     * Represents a chunk locator in the region file.
     * Contains the offset and size of the chunk.
     */
    private static class Locator {

        public byte offx4, offx2, offx1, sectorCount;

        public Locator(byte offx4, byte offx2, byte offx1, byte sectorCount) {
            this.offx4 = offx4;
            this.offx2 = offx2;
            this.offx1 = offx1;
            this.sectorCount = sectorCount;
        }

        public int offsetBytes() {
            int result = (offx4 & 0xFF) << 16 |
                    (offx2 & 0xFF) << 8 |
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

    private int chunkConsumed = 0;
    public File fileConsumed = null;

    /**
     * Get the number of chunks consumed
     * 
     * @return the number of chunks consumed
     */
    public int getChunkConsumed() {
        return chunkConsumed;
    }

    /**
     * Returns whether the file has been fully parsed
     * 
     * @return whether the file has been fully parsed
     */
    public boolean isFinished() {
        return chunkConsumed == CHUNK_COUNT;
    }

    /**
     * Consume the next chunk, decompress it, and store it in the chunks array
     * Used to parse the region file one by one
     * Also updates the progress bar
     * 
     * @throws IOException if there is an error reading the file
     */
    public void consumeChunk() throws IOException {
        FileInputStream fis = new FileInputStream(fileConsumed.getAbsolutePath());
        fis.skip(locators[chunkConsumed].offsetBytes());

        byte[] metadata = new byte[5];
        fis.read(metadata);

        byte[] data = new byte[locators[chunkConsumed].sizeBytes()];
        fis.read(data);

        if (metadata[0] == 0x00 && metadata[1] == 0x00 && metadata[2] == 0x00 && metadata[3] == 0x00
                && metadata[4] == 0x00) {
            chunks[chunkConsumed] = null;
        } else {
            byte compression = metadata[4];
            if (compression != 0x02) {
                System.err.println("Invalid compression type: " + String.format("0x%02X 0x%02X 0x%02X 0x%02X 0x%02X",
                        metadata[0], metadata[1], metadata[2], metadata[3], metadata[4], compression));
            } else {
                byte[] decompressed = DataParsing.decompressZlib(data);
                chunks[chunkConsumed] = new Chunk(decompressed);
            }
        }

        fis.close();
        chunkConsumed++;
    }

    /**
     * Start parsing the region file, and show the progress bar
     * 
     * @param file the region file to parse
     * @throws IOException if there is an error reading the file
     * @throws Exception if there is an error parsing the file
     */
    public Region(File file) throws IOException, Exception {
        // Open the file
        fileConsumed = file;
        FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        String[] split = file.getName().split("\\.");
        int regionx = Integer.parseInt(split[1]);
        int regionz = Integer.parseInt(split[2]);
        this.x = regionx;
        this.z = regionz;
        DialogManager.show(1024);

        // Read the locators, skip timestamps
        fis.read(header);
        fis.skip(SECTOR_SIZE);
        fis.close();
        for (int byteIdx = 0; byteIdx < header.length; byteIdx += 4) {
            int locatorIdx = byteIdx / 4;
            locators[locatorIdx] = new Locator(header[byteIdx], header[byteIdx + 1], header[byteIdx + 2],
                    header[byteIdx + 3]);
        }
    }

}
