package player;

import java.util.Map;

/**
 * A class to represent a Minecraft player's advancements
 * Created by deserializing a player's advancements JSON file with Gson
 * @see MinecraftPlayer
 * @see MinecraftPlayer#addAdvancements(File, File)
 * @author Nate Levison, February 2024
 */
public class Advancement {
    public Map<String, String> criteria;
    public boolean done;

    public String getCompleted() {
        String s = criteria.values().toArray(new String[0])[0];
        return s.substring(0, s.length() - 6);
    }
}
