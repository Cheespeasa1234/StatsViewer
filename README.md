# Minecraft StatsViewer
Minecraft StatsViewer - or StatsViewer - is a (mostly) standalone Java program that reads minecraft server / world files and displays statistics and other information in a human-consumable format. It also displays biomes and structures.
StatsViewer uses Java Swing (AWT) to have a fairly consistent and usable interface that easily keeps track of the information it processes.

## Contents
1. [Feature List](#feature-list)
2. [Dependencies](#dependencies)
3. [Installation](#installation)
3. [Quick Start](#quick-start)
4. [The Problem](#the-problem)
5. [Audience](#audience)
6. [Documentation](#documentation)
    - [Source File Structure](#source-file-structure)
    - [Library Resources](#library-resources)
        - [`ListPanel` and `QuantityLabel`](#listpanel-and-quantitylabel)
        - [`MinecraftPlayer` class](#minecraftplayer-class)
        - [`World` class](#world-class)
    - [Contribution](#contribution)
        - [How to code in this project](#how-to-code-in-this-project)
        - [Future Plans](#future-plans)
        - [Acknowledgements](#acknowledgements)


## Feature List
* Previously opened server storage
* User & UUID display
* Display users' inventory, advancements, statistics, and filenames
* Display world's seed, name, version, and gamerules
* Display biomes and structures
* Threading and progress info to keep the UI responsive
* Sort all information by related values, such as slots, A-Z, and count
* High performance allowing for rapid development and expansion
* Detailed and extensive library of code to make it easy to expand codebase

## Dependencies
* A JDK / JRE able to run Java 17+ programs
    * If you haven't installed, you can get it [here](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
    * Make sure you have the `java` command in your path
    * Use `java --version` to check if it is installed

## Installation
* Install any dependencies and have them accessible in path
* Download the jarfile and put it in a directory to run it that is not a minecraft server
* Run the program, and select the folder of a minecraft server
    * has server.properties, and usercache.json in it
    * has a world folder, which has a level.dat folder
    * has write permissions so that a .statsviewer directory can be made

## Quick Start
1. Download the jarfile from the releases page
2. Run the jarfile
3. Select the directory of a minecraft server
4. Press "Confirm" to load in the data
5. Select a user or world to view the information
6. Press "Exit" to close the program
7. In future runs, the server will be saved in the "Recent Servers" list

# The Problem
This program serves to solve a quite niche problem that I found in the way minecraft servers and minecraft worlds are stored. I was making a scoreboard on my server to keep track of how many sticks someone has crafted (something to do with fletchers, don't ask). The issue is, when creating the scoreboard, it doesn't automatically populate- instead, the admin needs to check his own stats, and check the stats files of all the users of the server. This is slow, annoying, and sometimes inconsistent.

_TLDR_: I wanted to see the statistics of all the users of my server at a glance, and I didn't want to have to go through the stats files of all the users to do it. So I made this program.

## Audience
This is a program that visualizes information that a server admin might want to see at a glance. It not only shows statistics, which was the original purpose, but it shows inventory / enderchest content, achievements, world information, and gamerules, all in a nicely organized cross-platform view. It is completely standalone, because programs that require installation and ingrain themselves into your system are my pet peeve. The only thing the program stores is decompressed .dat files in a new directory in your server files, which makes it easier to debug and to do any other work yourself.

# Documentation

Most important files have extensive documentation with examples on how to use it.
Some information can not be fit into standard documentation format, so it is provided below.

## Source File Structure

The StatsViewer program is organized into three packages with (mostly) separate purposes.
Here is a brief overview of the packages and their contents:
* `main` - The main package. Contains the main class, and the classes that manage the GUI and the program.
    * `BlankPanel.java` - The first page of the program. It handles server selection and tracking.
    * `DialogManager.java` - The static class that manages the progress bar popups.
    * `MainPanel.java` - The second page of the program. It displays player info, world info, and handles most interactions.
    * `StatsViewer.javav` - The main class. It starts the program and handles the main GUI.

* `util` - The utility package. Contains libraries, utilities, and global state management.
    * `DataParsing.java` - Contains utility functions for files, JSON parsing, and NBT parsing.
    * `Globals.java` - The main manager of global state and constants. It is mostly static.
    * `Utility.java` - The main library of utility functions. It is mostly static.

* `components` - The AWT component package. Each class is a component that is used in the main GUI.
    * `BottomPanelPlayers.java` - The bottom section for the players view. Handles the player list and the player view.
    * `BottomPanelWorlds.java` - The same as `BottomPanelPlayers`, but for worlds.
    * `ListPanel.java` - A utility component that lists strings, and provides dynamic sorting functionality.
    * `PlayerView.java` - A wrapper class for the `MinecraftPlayer` class. It displays the player's information.
    * `QuantityLabel.java` - A utility component, which is a JLabel but with more data for sorting purposes.
    * `TopPanel.java` - The top section of the program. Contains the server selection and the exit button.
    * `WorldMapPanel.java` - The panel that displays the biome and structure map, and region selection.
    * `WorldView.java` - A wrapper class for the `World` class. It displays the world's information.

* `player` - The player data package. Mostly static classes, but the Item class is not.
    * `Advancement.java` - The class that stores advancement data. Deserialized by Gson.
    * `Inventory.java` - The class that stores inventory data. Deserialized by Gson.
    * `MinecraftPlayer.java` - Contains a player's inventory, stats, and caching info.
    * `Item.java` - The class that stores item data. Deserialized by Gson.
    * `UsercachePlayer.java` - The class that stores usercache data. Deserialized by Gson.

* `world` - The world data package. Mostly static classes.
    * `Chunk.java` - The class that stores chunk data, and file locators.
    * `RegionParser.java` - The class that handles all decompression and parsing of region files.
    * `World.java` - The class that stores world data.
    * `WorldGenSettings.java` - The class that stores world generation settings. Deserialized by Gson.

## Library Resources

### `ListPanel` and `QuantityLabel`

You can use the `ListPanel` in conjunction with the `QuantityLabel` objects to make a dynamic and sortable list of entries, with many helper functions to make things easier. Here is a basic example of making a `ListPanel` that keeps track of names, and allows sorting alphabetically.

```java
// a given list of items to display
String[] names = { "Bob", "Jane", "Sam", "Al", "Joe", "Jeff", ...};

// create the panel
ListPanel namePanel = new ListPanel(
    500, 250, // set the dimensions of the panel
    ListPanel.ALL_AZ_OPTIONS, // use the alphabetical sorting options
    ListPanel.SORT_AZ // select sort A->Z as default
);
namePanel.add(new JLabel("Name List")); // add a title

// add the labels, sortable by name and length
for (String name : names) {
    namePanel.addLabel(name, name.length());
}

// add the panel wherever you want
frame.add(namePanel);
```

The class also allows you to update the list of items whenever you want, by sorting it, appending it, removing entries, or just clearing it.

```java
// add previous code...

// sort the labels
namePanel.sortLabels(ListPanel.SORT_ZA);
namePanel.sortLabels(ListPanel.SORT_SLOT_LEAST);

// add some labels
namePanel.addLabel("Ethan", "Ethan".length());
namePanel.addLabel("Ben", "Ben".length());

// clear the labels
namePanel.clearLabels();
```

As you may have noticed, there are quite a few ways to add labels to the `ListPanel`. Here is a list of every option.

```java
storePanel.addLabel("Apple", 2.99, 20); // Name, count, and slot
storePanel.addLabel("Grapes", 4.99); // Name, count
storePanel.addLabel(new JLabel("Orange")); // JLabel object
storePanel.addPanel(new QuantityLabel(...)); // QuantityLabel object
```

Even though the values in the quantity label are named `name`, `count`, and `slot`, they can easily be modified visually, and the `QuantityLabel` class can easily be expanded. It is strongly recommended to wrap the ListPanel into a JPanel, because otherwise, the ListPanel will not display properly. This can easily be done by adding the ListPanel to a JPanel, and then adding the JPanel to the frame.

```java
JPanel panel = new JPanel();
panel.add(namePanel);
frame.add(panel);
```

### `MinecraftPlayer` class

The `MinecraftPlayer` class is a class that manages the deserialization and storage of information related to a player. It stores data from the `playerdata`, `stats`, and `advancements` folder, as well as making basic calculations from data found in `level.dat` and `usercache.json`. In order to fully populate the player object, there are some steps to deserialization and parsing, shown below.

```java
// load in files
File usercacheFile = new File("path/to/usercache.json");
File playerFile = new File("path/to/playerdata/player.json");
File statsFile = new File("path/to/stats/player.json");
File advancementsFile = new File("path/to/advencements/player.json");

// create the gson parser
Gson gson = new GsonBuilder()
    .excludeFieldsWithoutExposeAnnotation()
    .create();
    
// read in the usercache file and create a usercache
List<UsercachePlayer> usercache = new ArrayList<UsercachePlayer>();
JsonArray usercacheJson = gson.fromJson(
    Lib.fileToString(usercacheFile), 
    JsonArray.class
);
for (JsonElement usercacheElement : usercacheJson) {
    usercache.add(gson.fromJson(usercacheElement, UsercachePlayer.class));
}

// Create the player
MinecraftPlayer player = gson
    .fromJson(Lib.fileToString(playerFile), MinecraftPlayer.class)
    .addUUID()
    .addName(usercache)
    .addStats(statsFile, server)
    .addAdvancements(advancementsFile, server);
```

As seen above, the Gson library does much of the heavy lifting of reading raw JSON files. However, by default, Gson does not handle NBT files, so the `de-nbt.py` script is used to convert the NBT files into JSON files. It can easily be accessed with `Lib.execute("python3", "de-nbt.py", "original.nbt", "new.json")`. The `MinecraftPlayer` class also has many methods to display the information in a human-readable format, and to compare the information with other players.

### `World` class

The `World` class is a class that manages the deserialization and storage of information related to a world. It stores data from the `level.dat` file, as well as making basic calculations from data found in the `world` folder. It is entirely deserialized with Gson, and is very simple to use. The only thing that needs to be done is to reformat the NBT files into JSON files, as per usual, and then make sure to use the `formatEpoch` method to convert the epoch time into a human-readable format.

```java
World world = gson.fromJson(Lib.fileToString(levelFile), World.class); // Create the world
String formattedTime = Lib.formatEpoch(world.lastPlayed); // format the last played time
```

Lastly, the `PlayerView` and `WorldView` classes are simple wrapper classes that when used in conjunction with Java Swing, display a Player or World object, respectively. They include all useful information about the object, and are easily expandable. As they are wrapper classes, they can easily be used, as seen below.

```java
// For players
PlayerView playerView = new PlayerView();
playerView.setPlayer(player);

// For worlds
WorldView worldView = new WorldView();
worldView.setWorld(world);
```

# Contribution

This project is fully open sourced, so just make a PR if you want to change something. The Issues tab is also great, so put any requests or bugs there. I plan on improving this program but I am at a bit of a dead end.

## How to code in this project

This section is about where a bug might be:
- __Something with the display of a player__: `MinecraftPlayer`, `PlayerView`
- __Something with the display of a world__: `World`, `WorldView`
- __Something with files not being read__: `Globals`, `Utility`
- __Something with the main UI components__: `ListPanel`, `QuantityLabel`, the `components` module, `UsercachePlayer`

Those are the most common bugs I found during developement, and what file(s) they tended to be caused by. 
Now, here are the core design principles of this project. I try to follow these while coding, so if you contribute, you should as well:
1. Keep things componentized
   - Each file / function does ONLY what it is meant to do. This means no .draw, .update, or .doEverything
   - The `MinecraftPlayer` class is just for storing players, the `World` class is just for storing worlds, etc
2. Library functions stay isolated and somewhat pure
   - The `Lib` class has no internal state
   - Any library functions / helper functions in the project go there
3. Use TABS / FOUR SPACES!
   - I am in the process of auto-formatting the whole project so yeah
   - Tabs > four spaces, I will die on this hill
4. Make useful code
   - Even if your code isn't exactly the best, it isn't perfect, thats fine
   - Code is meant to run fast, and correctly. If your code does that, great
   - __Coding is fun__. Don't ruin that for me ;)

## Future Plans
- A way to edit the world's gamerules
- Some information from region and entity files (don't know what yet)
- A way to edit the player's inventory and stats (worried, because it might delete the player's data so I need to be careful)
- more interesting formatting for the `other` tab in stats (make cm -> km, etc)
- performance improvements / logging

## Acknowledgements

Thank you for reading through the documentation. If you have any questions or comments, reach out to me in the social media platforms in my profile. Submit a bug report or a PR if there are any issues or features you would like to see (or not see). Have a great day!

The liscense for Gson can be viewed [here](licenses/LICENSE_GSON.md). 
The liscense for nbtlib can be viewed [here](licenses/LICENSE_NBT.md).
The liscense for this project can be viewed [here](licenses/LICENSE.md).

This project is not affiliated with, endorsed by, or associated with Mojang Studios or Microsoft, thecreators and owners of Minecraft. Minecraft is a trademark of Mojang Studios, and all related assets andintellectual property belong to their respective owners. This project is an independent creation developed by Nathaniel Levison and is not sponsored, authorized, or approved by Mojang Studios or Microsoft.

Nate Levison, February 2024
