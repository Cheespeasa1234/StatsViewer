package world;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import main.DialogManager;
import util.DataParsing;

public class RegionParser {
    
    private static final int SECTOR_SIZE = 4096;
    private static final int CHUNK_COUNT = 1024;

    public Chunk[] chunks = new Chunk[CHUNK_COUNT];
    public Locator[] locators = new Locator[CHUNK_COUNT];
    public byte[] header = new byte[SECTOR_SIZE];
    
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
                    (offx2 & 0xFF) << 8 |
                    (offx1 & 0xFF);
            return result * SECTOR_SIZE;
        }

        public int sizeBytes() {
            return sectorCount * SECTOR_SIZE;
        }

        @Override
        public String toString() {
            return String.format("ChunkLocator[0x%02X 0x%02X 0x%02X 0x%02X]", offx4, offx2, offx1, sectorCount);
        }
    }

    private int chunkConsumed = 0;
    private File fileConsumed = null;

    public int getChunkConsumed() { return chunkConsumed; }
    public boolean isFinished() { return chunkConsumed == CHUNK_COUNT - 1; }
    public void consumeChunk() throws IOException {
        chunkConsumed++;
        FileInputStream fis = new FileInputStream(fileConsumed.getAbsolutePath());
        fis.skip(locators[chunkConsumed].offsetBytes());

        byte[] metadata = new byte[5];
        fis.read(metadata);

        byte[] data = new byte[locators[chunkConsumed].sizeBytes()];
        fis.read(data);

        byte compression = metadata[4];
        if (compression != 0x02) {
            System.err.println("Invalid compression type: " + String.format("0x%02X", compression));
            System.exit(-1);
        } else {
            byte[] decompressed = DataParsing.decompressZlib(data);
            chunks[chunkConsumed] = new Chunk(decompressed, locators[chunkConsumed]);
        }
        fis.close();
    }
    public void startParse(File file) throws IOException, Exception {
        // Open the file
        fileConsumed = file;
        FileInputStream fis = new FileInputStream(file.getAbsolutePath());
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
