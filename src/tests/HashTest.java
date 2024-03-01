package tests;

public class HashTest {
    public static short fnv1aHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;

        for (int i = 0; i < str.length(); i++) {
            hash ^= str.charAt(i);
            hash *= p;
        }

        return (short) (hash & 0xFFFF);
    }

    public static short builtinHash(String str) {
        return (short) (str.hashCode() & 0xFFFF);
    }

    public static void main(String[] args) {
        // make 100 random strings
        String[] strings = new String[100];
        for (int i = 0; i < 100; i++) {
            strings[i] = "";
            for (int j = 0; j < 10; j++) {
                strings[i] += (char) (Math.random() * 26 + 97);
            }
        }

        long start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            fnv1aHash(strings[i]);
        }
        long end = System.nanoTime();
        System.out.println("fnv1aHash: " + (end - start) / 1000000.0 + "ms");

        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            builtinHash(strings[i]);
        }
        end = System.nanoTime();
        System.out.println("builtinHash: " + (end - start) / 1000000.0 + "ms");
    }
}