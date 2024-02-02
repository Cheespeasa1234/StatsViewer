package player;

import java.util.HashMap;
import java.util.List;

public class Stats {
    public HashMap<String, Double> killed = new HashMap<String, Double>();
    public HashMap<String, Double> killed_by = new HashMap<String, Double>();
    public HashMap<String, Double> used = new HashMap<String, Double>();
    public HashMap<String, Double> crafted = new HashMap<String, Double>();
    public HashMap<String, Double> dropped = new HashMap<String, Double>();
    public HashMap<String, Double> picked_up = new HashMap<String, Double>();
    public HashMap<String, Double> mined = new HashMap<String, Double>();
    public HashMap<String, Double> broken = new HashMap<String, Double>();
    public HashMap<String, Double> custom = new HashMap<String, Double>();
    public List<HashMap<String, Double>> statsGroups = List.of(
            killed, used, dropped, crafted, killed_by, picked_up, mined, broken, custom);
    public List<String> statsGroupsNames = List.of(
            "killed", "used", "dropped", "crafted", "killed_by", "picked_up", "mined", "broken", "custom");
}
