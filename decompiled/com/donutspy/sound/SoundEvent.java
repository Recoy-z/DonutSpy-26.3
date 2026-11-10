/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2960
 */
package com.donutspy.sound;

import java.util.Locale;
import net.minecraft.class_2960;

public record SoundEvent(class_2960 id, double x, double y, double z, float volume, float pitch, long timestamp, String category) {
    public static SoundEvent from(class_2960 id, double x, double y, double z, float volume, float pitch) {
        String path = id.method_12832().toLowerCase(Locale.ROOT);
        String category = SoundEvent.classify(path);
        return new SoundEvent(id, x, y, z, volume, pitch, System.currentTimeMillis(), category);
    }

    private static String classify(String path) {
        if (SoundEvent.contains(path, "sculk", "warden", "sensor", "shrieker", "catalyst")) {
            return "SCULK";
        }
        if (SoundEvent.contains(path, "explode", "explosion", "tnt", "firework")) {
            return "EXPLOSIONS";
        }
        if (SoundEvent.contains(path, "attack", "hurt", "death", "hit", "arrow", "bow", "crossbow", "trident", "shield", "critical", "sword", "mace", "armor")) {
            return "COMBAT";
        }
        if (SoundEvent.contains(path, "minecart", "boat", "raft", "horse", "camel", "saddle", "elytra", "bamboo_raft", "chest_boat")) {
            return "TRANSPORT";
        }
        if (SoundEvent.contains(path, "crop", "farmland", "hoe", "harvest", "compost", "bone_meal", "shear", "cow", "sheep", "chicken", "pig", "goat", "fishing", "bucket")) {
            return "FARMING";
        }
        if (SoundEvent.contains(path, "step", "walk", "swim", "sprint", "jump", "land", "fall", "climb")) {
            return "MOVEMENT";
        }
        if (SoundEvent.contains(path, "rain", "thunder", "weather", "ambient", "cave", "water", "lava", "wind", "fire", "grass", "leaves", "underwater", "ocean")) {
            return "NATURAL";
        }
        return "CUSTOM";
    }

    private static boolean contains(String value, String ... terms) {
        for (String term : terms) {
            if (!value.contains(term)) continue;
            return true;
        }
        return false;
    }
}

