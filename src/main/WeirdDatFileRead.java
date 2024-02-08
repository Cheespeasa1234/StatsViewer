package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeirdDatFileRead {
	public static void main(String[] args) {
		// get every file in the directory
		File dir = new File("/Users/nlevison25/server/decoyworld/region/");
		File parsedDir = new File("/Users/nlevison25/server/decoyworld/region_parsed/");

		parsedDir.mkdirs();

		int count = dir.listFiles((dir1, name) -> name.endsWith(".mca")).length;
        int i = 0;

        // Number of threads to use for parallel processing
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (File file : dir.listFiles()) {
			String output = parsedDir.getAbsolutePath() + "/" + file.getName().replace(".mca", ".json");
            if (file.getName().endsWith(".mca")) {
                i++;
                final int fileIndex = i;
                executor.submit(() -> {
                    long start = System.currentTimeMillis();
                    System.out.println("Parsing " + file.getName());
                    // Call your parsing method here
                    convertDatToJson(file.getAbsolutePath(), output);
                    System.out.println("Parsed " + file.getName() + " in " + (System.currentTimeMillis() - start) + "ms - " + fileIndex + "/" + count);
                });
            }
        }

        // Shut down the executor after all tasks are complete
        executor.shutdown();
	}

	public static void convertDatToJson(String datFilePath, String jsonOutputPath) {
        try {
            // Read the .dat file
            FileInputStream fis = new FileInputStream(datFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            // Create a JSON array to store the data
            JSONArray jsonArray = new JSONArray();

            String line;
            while ((line = br.readLine()) != null) {
                // Assuming each line of the .dat file represents a JSON object
                JSONObject jsonObject = new JSONObject(line);
                jsonArray.put(jsonObject);
            }

            // Write the JSON array to the output file
            try (FileWriter fileWriter = new FileWriter(jsonOutputPath)) {
                fileWriter.write(jsonArray.toString(4)); // Use 4 spaces for indentation
            }

            // Close readers
            br.close();
            fis.close();

            System.out.println("Conversion completed successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}