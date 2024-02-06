package player;

import java.util.Map;

public class Advancement {
    public Map<String, String> criteria;
    public boolean done;

    public String getCompleted() {
        String s = criteria.values().toArray(new String[0])[0];
        return s.substring(0, s.length() - 6);
    }
}
