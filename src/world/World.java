package world;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import main.DialogManager;

public class World {
	@Expose @SerializedName("Difficulty") public int difficulty;
	@Expose @SerializedName("GameType") public int gameType;
	@Expose @SerializedName("DayTime") public int time;
	@Expose @SerializedName("LastPlayed") public int lastPlayedEpoch;

	public class WorldGenSettings {
		@Expose @SerializedName("seed") public long seed;
	}

	@Expose @SerializedName("WorldGenSettings") public WorldGenSettings worldGenSettings;

	public class Version {
		@Expose @SerializedName("Snapshot") public int snapshot;
		@Expose @SerializedName("Series") public String series;
		@Expose @SerializedName("Id") public String id;
		@Expose @SerializedName("Name") public String name;
	}

	@Expose @SerializedName("Version") public Version version;
	@Expose @SerializedName("GameRules") public Map<String, String> gamerules;
	@Expose @SerializedName("LevelName") public String name;

	// Region files for chunk parsing
	public RegionParser[] regions;
	public File[] regionFiles;

	public interface LoadedEvent {
		void onLoaded();
	}

	public void setRegionFiles(File[] regionFiles, LoadedEvent onFinishedLoading) throws IOException, Exception {
		this.regionFiles = regionFiles;
		this.regions = new RegionParser[regionFiles.length];

		try {
			for (int i = 0; i < regionFiles.length; i++) {
				setRegionFile(regionFiles, i, onFinishedLoading);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the region file at the given index
	 * Begins the parsing of the region file
	 * 
	 * @param regionFiles the region files
	 * @param i the index
	 * @throws IOException if there is an error reading the file
	 * @throws Exception if there is an error parsing the file
	 */
	private void setRegionFile(File[] regionFiles, int i, LoadedEvent onFinishedLoading) throws IOException, Exception {
		this.regions[i] = new RegionParser();

		// start the parsing
		regions[i].startParse(regionFiles[i]);

		// consume the chunks
		new Thread(() -> {
			while (!regions[i].isFinished()) {

				try {
					regions[i].consumeChunk();

					// update the progress meter
					SwingUtilities.invokeLater(() -> {
						DialogManager.setCount(regions[i].getChunkConsumed());
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			DialogManager.close();
			onFinishedLoading.onLoaded();
		}).start();

	}
}
