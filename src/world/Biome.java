package world;

import java.awt.Color;

public class Biome {
    public String owner;
    public String name;
    public String formalizedName;
    public Color color;
    public Biome(String owner, String name, Color color) {
        this.owner = owner;
        this.name = name;
        this.formalizedName = formalizeString(name);
        this.color = color;
    }

    private static String formalizeString(String input) {
        char[] chars = input.toCharArray();
        boolean capitalizeNext = true;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '_') {
                chars[i] = ' ';
                capitalizeNext = true;
            } else if (capitalizeNext && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                capitalizeNext = false;
            } else if (Character.isWhitespace(chars[i])) {
                capitalizeNext = true;
            } else {
                chars[i] = Character.toLowerCase(chars[i]);
                capitalizeNext = false;
            }
        }

        return new String(chars);
    }
}
