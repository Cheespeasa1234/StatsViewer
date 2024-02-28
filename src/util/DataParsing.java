package util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

public class DataParsing {

    // This took way too long to figure out, im gonna go jerk off now
    public static JsonElement collapse(JsonElement element) {
        if (element.isJsonPrimitive()) {
            return element;
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            if (array.size() == 0) {
                return JsonNull.INSTANCE;
            } else if (array.size() == 1) {
                return collapse(array.get(0));
            } else {
                for (int i = 0; i < array.size(); i++) {
                    array.set(i, collapse(array.get(i)));
                }
                return array;
            }
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            // if it has a "type" and "value" key, it's a tag that can be collapsed
            // if it has a "type" and "list" key, the entries in the list must be collapsed
            if (object.has("type") && object.has("list")) {
                JsonArray list = object.getAsJsonArray("list");
                JsonElement newElement = new JsonArray();
                for (int i = 0; i < list.size(); i++) {
                    ((JsonArray) newElement).add(collapse(list.get(i)));
                }
                return newElement;
            } else if (object.has("type") && object.has("value")) {
                return collapse(object.get("value"));
            } else {
                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                    object.add(entry.getKey(), collapse(entry.getValue()));
                }
                return object;
            }
        }
        return element;
    }

    /**
     * Takes in a .dat file in NBT format, and converts it to a JSON file.
     * Uses the system's python instance to convert.
     * 
     * @param fileIn
     * The full path of the file to convert
     * @param fileOut
     * The full path of the location to put the new file
     * @return void
     */
    public static void convertNBT(String fileIn, String fileOut) {
        try {
            InputStream inputStream;
            if (isGzipped(fileIn)) {
                // Read the compressed NBT data from the input file
                inputStream = new GZIPInputStream(new FileInputStream(fileIn));

            } else {
                inputStream = new FileInputStream(fileIn);
            }
            // Wrap the compressed stream with NBTInputStream
            NBTInputStream nbtInputStream = new NBTInputStream(inputStream);

            // Read the NBT data
            NamedTag compoundTag = nbtInputStream.readTag(64);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement obj = gson.fromJson(convertToJSON(compoundTag), JsonElement.class);

            // Collapse the JSON
            obj = collapse(obj);
            String json = gson.toJson(obj);

            // Write the JSON data to the output file
            Files.write(Paths.get(fileOut), json.getBytes());

            // Close input stream
            nbtInputStream.close();
        } catch (IOException e) {
            // Print error message and stack trace
            System.out.println("Error converting " + fileIn + " to " + fileOut);
            e.printStackTrace();
        }
    }

    public static boolean isGzipped(String filePath) {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            // Read the first two bytes of the file
            byte[] header = new byte[2];
            int bytesRead = inputStream.read(header, 0, 2);

            if (bytesRead != 2) {
                // If less than two bytes are read, it can't be gzipped
                return false;
            }

            // Check if the first two bytes match the gzip magic number
            return (header[0] == (byte) 0x1f) && (header[1] == (byte) 0x8b);
        } catch (IOException e) {
            // Error reading file or file not found, consider it not gzipped
            return false;
        }
    }

    public static String convertToJSON(NamedTag tag) {
        try {
            // Parse the NamedTag from the NBT data
            Tag<?> rootTag = tag.getTag();

            // Check if the root tag is a compound tag
            if (rootTag instanceof CompoundTag) {
                // Convert the CompoundTag to JSON string
                return ((CompoundTag) rootTag).toString();
            } else {
                throw new IllegalArgumentException("Root tag is not a CompoundTag");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle the error gracefully in your application
        }
    }

    /**
     * Splits a string into tokens, based on how JSON is formatted.
     * Splits by all commas, and colons only if the colon isn't in between quotes.
     * Examples:
     * 
     * <pre>
     * <br>splitPressedJSON("\"minecraft:cobblestone\":5") = ["\"minecraft: cobblestone\"", "5"]
     * <br>splitPressedJSON("\"minecraft:cobblestone\":5,"\"minecraft:ender_pearl\":18923, hello") = ["\"minecraft: cobblestone\"", "5", "\"minecraft:ender_pearl\"", "18923", "hello"]
     * <br>splitPressedJSON("\"minecraft:cobblestone\":5, hello") = ["\"minecraft: cobblestone\"", "5", "hello"]
     * </pre>
     */

    public static List<String> splitPressedJSON(String input) {
        List<String> result = new ArrayList<String>();

        Pattern pattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String match = matcher.group();
            if (!match.isEmpty()) {
                // if the match has a comma, get rid of that
                if (match.charAt(match.length() - 1) == ',')
                    result.add(match.substring(0, match.length() - 1));
                else
                    result.add(match);
            }
        }

        return result;
    }

    public static byte[] decompressZlib(byte[] compressedData) {
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

    /**
     * Returns the number of bits required to represent the given number.
     * 
     * @param num The number to represent
     * @return The number of bits required to represent the number
     */
    public static int bitSpaceRequired(int num) {
        if (num == 0) {
            return 1;
        }
        int numBits = 0;
        while (num != 0) {
            numBits++;
            num >>>= 1; // Unsigned right shift, discards the leftmost bit (fills with 0)
        }
        return numBits;
    }


    /**
     * Splits a list of signed integers into 64 indices of bit size provided.
     * For example, splitIntegers([-1729127166320252928L], 3) returns [1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0]
     * This is used for splitting compressed data into indices that access a pallete.
     * 
     * @param array The compressed data, typically 1-2 integers.
     * @param n The size of the bit indices to split the integers into.
     * @return A list of integers, each representing an index.
    */
    public static byte[] splitIntegers(long[] array, int n) {
        // Join binary data with each number being 64 bits
        StringBuilder binaryString = new StringBuilder();
        for (long num : array) {
            // Convert number to 64-bit binary representation
            String binaryNum = Long.toBinaryString(num);
            // Pad with leading zeros if necessary
            while (binaryNum.length() < 64) {
                binaryNum = "0" + binaryNum;
            }
            binaryString.append(binaryNum);
        }

        // Split binary data into chunks of size n and reverse each chunk
        List<String> binaryChunks = new ArrayList<>();
        for (int i = 0; i < binaryString.length(); i += n) {
            String chunk = binaryString.substring(i, Math.min(i + n, binaryString.length()));
            // Reverse the chunk
            StringBuilder reversedChunk = new StringBuilder(chunk).reverse();
            binaryChunks.add(reversedChunk.toString());
        }

        // Convert binary chunks to numbers and store them in byte array
        byte[] result = new byte[binaryChunks.size()];
        for (int i = 0; i < binaryChunks.size(); i++) {
            String binaryChunk = binaryChunks.get(i);
            result[i] = (byte) Integer.parseInt(binaryChunk, 2);
        }

        return result;
    }

}
