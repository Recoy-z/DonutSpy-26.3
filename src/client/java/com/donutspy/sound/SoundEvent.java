package com.donutspy.sound;

import java.util.Locale;

public record SoundEvent(
        String id,
        double x,
        double y,
        double z,
        float volume,
        float pitch,
        long timestamp,
        String category
) {
    public static SoundEvent from(String id, double x, double y, double z, float volume, float pitch) {
        String path = id.toLowerCase(Locale.ROOT);
        return new SoundEvent(id, x, y, z, volume, pitch, System.currentTimeMillis(), classify(path));
    }

    private static String classify(String path) {
        if (contains(path, "sculk", "warden", "sensor", "shrieker", "catalyst")) return "SCULK";
        if (contains(path, "explode", "explosion", "tnt", "firework")) return "EXPLOSIONS";
        if (contains(path, "attack", "hurt", "death", "hit", "arrow", "bow", "crossbow", "trident", "shield", "critical", "sword", "mace", "armor")) return "COMBAT";
        if (contains(path, "minecart", "boat", "raft", "horse", "camel", "saddle", "elytra")) return "TRANSPORT";
        if (contains(path, "crop", "farmland", "hoe", "harvest", "compost", "bone_meal", "shear", "cow", "sheep", "chicken", "pig", "goat", "fishing", "bucket")) return "FARMING";
        if (contains(path, "step", "walk", "swim", "sprint", "jump", "land", "fall", "climb")) return "MOVEMENT";
        if (contains(path, "rain", "thunder", "weather", "ambient", "cave", "water", "lava", "wind", "fire", "grass", "leaves", "underwater", "ocean")) return "NATURAL";
        return "CUSTOM";
    }

    private static boolean contains(String value, String... terms) {
        for (String term : terms) if (value.contains(term)) return true;
        return false;
    }
}