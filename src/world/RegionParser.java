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

import util.DataParsing;

public class RegionParser {

    private static final int SECTOR_SIZE = 4096;
    private static final int CHUNK_COUNT = 1024;

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

        @Override public String toString() {
            return String.format("ChunkLocator[0x%02X 0x%02X 0x%02X 0x%02X]", offx4, offx2, offx1, sectorCount);
        }
    }

    public Chunk[] chunks = new Chunk[CHUNK_COUNT];
    public Locator[] locators = new Locator[CHUNK_COUNT];
    public byte[] header = new byte[SECTOR_SIZE];

    public static ByteArrayOutputStream decompressZlib_old(byte[] compressedData)
            throws DataFormatException, IOException {
        // Skip the first five bytes
        byte[] compressedDataWithoutHeader = new byte[compressedData.length - 5];
        for (int i = 0; i < compressedDataWithoutHeader.length; i++) {
            compressedDataWithoutHeader[i] = compressedData[i + 5];
        }

        // Create a new Inflater to decompress the data
        Inflater inflater = new Inflater();
        inflater.setInput(compressedDataWithoutHeader);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compressedDataWithoutHeader.length);

        byte[] buffer = new byte[SECTOR_SIZE];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                if (count == 0 && inflater.needsInput()) {
                    throw new DataFormatException("Incomplete input data");
                }
                outputStream.write(buffer, 0, count);
            }
        } catch (DataFormatException e) {
            System.err.println("Error decompressing data: " + e.getMessage());
            System.err.println("Compressed data length: " + compressedData.length);
            System.err.println("Buffer content: " + Arrays.toString(compressedData));
            throw e; // Rethrow the exception
        } finally {
            inflater.end();
            outputStream.close();
        }

        return outputStream;
    }

    public void parse(File file) throws IOException, Exception {

        // Open the file
        FileInputStream fis = new FileInputStream(file.getAbsolutePath());

        JDialog dialog = new JDialog();
        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JProgressBar progressBar = new JProgressBar(0, 2048);
        progressBar.setIndeterminate(true);
        dialog.add(progressBar);
        dialog.setVisible(true);

        // Read the locators, skip timestamps
        fis.read(header);
        fis.skip(SECTOR_SIZE);
        fis.close();
        for (int byteIdx = 0; byteIdx < header.length; byteIdx += 4) {
            int locatorIdx = byteIdx / 4;
            locators[locatorIdx] = new Locator(header[byteIdx], header[byteIdx + 1], header[byteIdx + 2],
                    header[byteIdx + 3]);
            progressBar.setValue(locatorIdx);
            System.out.println("Locator " + locatorIdx + ": " + locators[locatorIdx]);
        }

        // Read the chunks
        for (int chunkIdx = 0; chunkIdx < CHUNK_COUNT; chunkIdx++) {
            fis = new FileInputStream(file.getAbsolutePath());
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
                byte[] decompressed = DataParsing.decompressZlib(data);
                chunks[chunkIdx] = new Chunk(decompressed, locators[chunkIdx]);
            }
            fis.close();

            final int x = chunkIdx + 1024;
            progressBar.setValue(x);
            System.out.println("Chunk " + chunkIdx + ": " + chunks[chunkIdx]);
        }

        dialog.dispose();

    }

}
