package world;

import util.DataParsing;

public class Heightmap {
    private long[] values;
    private static final int VALUES_PER_LONG = 7;
    private static final int BITS_PER_VALUE = 9;
    private static final int LONGS_COUNT = 37;

    public Heightmap(long[] values) {
        
        // Make sure the right amt
        if (values.length != LONGS_COUNT) {
            throw new IllegalArgumentException("Length of values must be " + LONGS_COUNT);
        }

        this.values = values;
    }

    /**
     * Takes in an x and z coordinate and returns the height at that point.
     * The values array is 37 longs. Each long stores 7 values, 9 bits each. The last bit of every long is unused.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the height at the given point
     */
    public int getHeight(int x, int z) {
        if (x < 0 || x >= 16 || z < 0 || z >= 16) {
            throw new IllegalArgumentException("x and z must be between 0 and 15");
        }
        int index = x + z * 16;
        int valueIndex = index / VALUES_PER_LONG;
        int bitIndex = index % VALUES_PER_LONG * BITS_PER_VALUE;
        long value = values[valueIndex] >> bitIndex;
        return ((int) (value & 0x1FF)) - 64;
    }
}
