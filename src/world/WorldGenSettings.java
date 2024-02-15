package world;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WorldGenSettings {
	@Expose public int bonus_chest;
	@Expose public long seed;
	@Expose public int generate_features;

	@Expose @SerializedName("dimensions") public Dimensions dimensions;

	public class Dimensions {
		@Expose @SerializedName("minecraft:overworld") public Dimension overworld;
		@Expose @SerializedName("minecraft:the_nether") public Dimension the_nether;
		@Expose @SerializedName("minecraft:the_end") public Dimension the_end;
	}

	public class Dimension {
		@Expose public Generator generator;
		@Expose public String type;
	}

	public class Generator {
		@Expose public String settings;
		@Expose public BiomeSource biome_source;
		@Expose public String type;
	}

	public class BiomeSource {
		@Expose public String preset;
		@Expose public String type;
	}
}
