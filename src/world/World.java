package world;

import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
}
