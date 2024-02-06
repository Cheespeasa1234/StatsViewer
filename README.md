# Minecraft StatsViewer
Minecraft StatsViewer - or StatsViewer - is a (mostly) standalone Java program that reads minecraft server / world files and displays statistics and other information in a human-consumable format.
StatsViewer uses Java Swing (AWT) to have a fairly consistent and usable interface that easily keeps track of the information it processes.

## Feature List
* Previously opened server storage
* User & UUID display
* Display users' inventory, advancements, statistics, and filenames
* Sort all information by related values, such as slots, A-Z, and count
* High performance allowing for rapid development and expansion
* Detailed and extensive library of code to make it easy to expand codebase

## Dependencies
* Python 3, in path using "python" or "python3"
* The pip package "nbtlib"
* A JDK / JRE able to run Java 17+ programs

## Installation
* Install any dependencies and have them accessible in path
* Download the jarfile and put it in a directory to run it that is not a minecraft server
* Run the program, and select the folder of a minecraft server
    * has server.properties, and usercache.json in it
    * has a world folder, which has a level.dat folder
    * has write permissions so that a .statsviewer directory can be made

# Documentation

Most important files have extensive documentation with examples on how to use it.
Some information can not be fit into standard documentation format, so it is provided below.

## Source File Structure

The StatsViewer program is organized into three packages with (mostly) separate purposes.
Here is a brief overview of the packages and their contents:
* `main` - The main package
    * `main.DependencyChecker.java` - Static class that checks that python and nbtlib are available.
    * `main.Globals.java` - Static class that holds global variables and constants.
    * `main.Lib.java` - Static class that holds library functions and classes, and manages globals.
    * `main.ListPanel.java` - An extension of the JPanel class that makes it easier to display lists of items with sorting features and dynamic formatting.
    * `main.QuantityLabel.java` - An extension of the JLabel class that makes it easier to display labels with built in data, solely compatible with ListPanel.
    * `main.StatsViewer.java` - The main class that runs the program and manages the GUI.

* `pages` - The AWT component package
    * `pages.BlankPanel.java` - First page that displays the open server and previous server buttons.
    * `pages.BottomPanel.java` - The component of the second page that contains the actual interactive content.
    * `pages.MainPanel.java` - The container component which is the second page. It contains the top panel and the bottom panel and manages the layout.
    * `pages.PlayerView.java` - A wrapper component that displays all the information about a user. It includes the inventory, advancements, and statistics tabs, and the user's name and UUID.
    * `pages.TopPanel.java` - The top bar on the main page that allows you to exit, and shows the loading speed.

* `player` - The player data package
    * `player.Advancement.java` - A class that holds the data for an advancement. Used by the deserializer.
    * `player.Inventory.java` - A class that holds the data for an inventory. Used by the deserializer.
    * `player.Item.java` - The class that holds the info of an item, and provides methods for display and comparison.
    * `player.MinecraftPlayer.java` - The class that contains all info about a player, and is used by the PlayerView class to display the information. It is partially created with the deserializer, and partially created with some methods. It is the main class of the package.
    * `player.UsercachePlayer.java` - A class that holds the data for a usercache entry. Used by the deserializer. Only used to store UUID and name linking.

* Other source files
    * `de-nbt.py` - Python script that uses nbtlib to turn NBT files into JSON files. Also roughly formats everything.
    * `format-stat.py` - Python script that formats the statistics JSON file into a format that can be parsed lazily by the statistics functions.
    * `recent_directories.txt` - A file that stores the last 5 directories that were opened by the program. It is used to display the buttons on the first page.
    * `src/main/icon.png` - The icon of the program. Unused.

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

Even though the values in the quantity label are named `name`, `count`, and `slot`, they can easily be modified visually, and the `QuantityLabel` class can easily be expanded. If there are any issues with this, submit a PR or a bug report.

### `MinecraftPlayer`

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

Thank you for reading through the documentation. If you have any questions or comments, reach out to me in the social media platforms in my profile. Submit a bug report or a PR if there are any issues or features you would like to see (or not see). Have a great day!

Nate Levison, February 2024
```