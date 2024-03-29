package main;

import player.MinecraftPlayer;
import player.UsercachePlayer;
import util.Globals;
import util.Utility;
import world.World;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import components.BottomPanelPlayers;
import components.BottomPanelWorlds;
import components.TopPanel;

public class MainPanel extends JPanel {

	File server;
	TopPanel topPanel;
	BottomPanelPlayers bottomPanelPlayersMode;
	BottomPanelWorlds bottomPanelWorldsMode;

	MinecraftPlayer currentPlayer;
	ArrayList<MinecraftPlayer> players = new ArrayList<MinecraftPlayer>();
	ArrayList<World> worlds = new ArrayList<World>();

	void createTopPanel() {
		topPanel = new TopPanel(Globals.TOP_HEIGHT, () -> {
			this.loadPlayers();
			this.loadWorlds();
			topPanel.loadButton.setVisible(false);
		}, () -> {
			System.exit(0);
		}, () -> {
			bottomPanelPlayersMode.setVisible(true);
			bottomPanelWorldsMode.setVisible(false);
		}, () -> {
			bottomPanelPlayersMode.setVisible(false);
			bottomPanelWorldsMode.setVisible(true);

		});
	}

	void createBottomPanel() {
		bottomPanelPlayersMode = new BottomPanelPlayers();
		bottomPanelWorldsMode = new BottomPanelWorlds();
	}

	public MainPanel() {

		createTopPanel();
		createBottomPanel();

		this.add(topPanel, BorderLayout.NORTH);
		this.add(bottomPanelPlayersMode, BorderLayout.CENTER);
		bottomPanelWorldsMode.setVisible(false);
		this.add(bottomPanelWorldsMode, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(Globals.PREF_W, Globals.PREF_H));

	}

	public void setFile(File server) {
		this.server = server;
		topPanel.statusLabel.setText("Server @ " + server.getAbsolutePath() + "...");
	}

	private class PermsPlayer {
		@Expose public String uuid;
		@Expose public String name;
		@Expose public int level;
		@Expose public boolean bypassesPlayerLimit;
	}

	private void loadWorlds() {

		long start = System.currentTimeMillis();

		// find every folder that has a level.dat file
		File[] worldFolders = server.listFiles((dir, name) -> {
			return new File(dir.getAbsolutePath() + "/" + name + "/level.dat").exists();
		});

		// create the world data
		Gson gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.create();

		for (File worldFolder : worldFolders) {
			String path = worldFolder.getAbsolutePath() + "/level.json";
			String worldName = worldFolder.getName();
			path = path.replace(worldName, ".statsviewer/" + worldName);
			String levelDatString = Utility.fileToString(path);
			JsonObject levelDatJsonObj = gson.fromJson(levelDatString, JsonObject.class);
			World world = gson.fromJson(levelDatJsonObj.get("Data"), World.class);
			File regionFolder = new File(worldFolder.getAbsolutePath() + "/region");
			System.out.println("Region folder: " + regionFolder.getAbsolutePath());
			world.regionFiles = regionFolder.listFiles();
			System.out.println("Region files: " + world.regionFiles.length);

			worlds.add(world);
			bottomPanelWorldsMode.worlds.add(world);
			bottomPanelWorldsMode.listModel.addElement(world.name);
		}

		long dif = System.currentTimeMillis() - start;
		System.out.println("Loaded in " + ((double) dif / 1000) + "s.");
	}

	private void loadPlayers() {

		long start = System.currentTimeMillis();

		File playerDataDirectory = new File(server.getAbsolutePath() + Utility.getSpecialLocation() + "/playerdata");
		File playerStatsDirectory = new File(server.getAbsolutePath() + Utility.getSpecialLocation() + "/stats");
		File playerAdvancementsDirectory = new File(server.getAbsolutePath() + Utility.getSpecialLocation() + "/advancements");

		// get a list of cached user
		File usercacheFile = new File(server.getAbsolutePath() + "/usercache.json");
		List<UsercachePlayer> usercache = new ArrayList<>();
		if (usercacheFile.exists()) {
			try {
				Gson gson = new GsonBuilder()
						.excludeFieldsWithoutExposeAnnotation()
						.create();
				BufferedReader br = new BufferedReader(new FileReader(usercacheFile));
				JsonArray usercacheJson = gson.fromJson(br, JsonArray.class);
				for (JsonElement usercacheElement : usercacheJson) {
					usercache.add(gson.fromJson(usercacheElement, UsercachePlayer.class));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		// create the player data
		Gson gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.create();
		File[] playerDataFiles = playerDataDirectory.listFiles();
		for (File playerFile : playerDataFiles) {
			File statsFile = new File(
					playerStatsDirectory.getAbsolutePath() + "/" + playerFile.getName().replace(".dat", ".json"));
			File advancementsFile = new File(playerAdvancementsDirectory.getAbsolutePath() + "/"
					+ playerFile.getName().replace(".dat", ".json"));
			try {

				String playerFileString = Utility.fileToString(playerFile.getAbsolutePath());
				MinecraftPlayer player = gson
						.fromJson(playerFileString, MinecraftPlayer.class)
						.addUUID()
						.addName(usercache)
						.addStats(statsFile, server)
						.addAdvancements(advancementsFile, server);

				System.out.println("Created player: " + player.UUID + " with fileid: " + playerFile.getName());

				players.add(player);
				bottomPanelPlayersMode.players.add(player);
				bottomPanelPlayersMode.listModel.addElement(player.getName());
			} catch (Exception e) {
				System.out.println("Error reading player file: " + playerFile.getAbsolutePath());
				e.printStackTrace();
			}
		}

		// load whitelist, bans, and ops

		List<PermsPlayer> whitelist = gson.fromJson(Utility.fileToString(server.getAbsolutePath() + "/whitelist.json"),
				new TypeToken<List<PermsPlayer>>() {}.getType());
		List<PermsPlayer> ops = gson.fromJson(Utility.fileToString(server.getAbsolutePath() + "/ops.json"),
				new TypeToken<List<PermsPlayer>>() {}.getType());
		List<PermsPlayer> bans = gson.fromJson(Utility.fileToString(server.getAbsolutePath() + "/banned-players.json"),
				new TypeToken<List<PermsPlayer>>() {}.getType());

		for (PermsPlayer p : whitelist) {
			for (MinecraftPlayer player : players) {
				if (player.UUID.equals(p.uuid)) {
					player.whitelisted = true;
				}
			}
		}
		for (PermsPlayer p : ops) {
			for (MinecraftPlayer player : players) {
				if (player.UUID.equals(p.uuid)) {
					player.op = true;
				}
			}
		}
		for (PermsPlayer p : bans) {
			for (MinecraftPlayer player : players) {
				if (player.UUID.equals(p.uuid)) {
					player.banned = true;
				}
			}
		}

		long dif = System.currentTimeMillis() - start;
		System.out.println("Loaded in " + ((double) dif / 1000) + "s.");
	}
}
