package player;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UsercachePlayer {
    @Expose public String name;
    @Expose @SerializedName("uuid") public String UUID;
    @Expose public String expiresOn;
}
