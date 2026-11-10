/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.fabricmc.loader.api.FabricLoader
 */
package com.donutspy.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.fabricmc.loader.api.FabricLoader;

public final class DonutSpyConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("donutspy").resolve("config.json");
    public boolean captureEnabled = true;
    public boolean hudEnabled = true;
    public boolean showOtherSounds = true;
    public int historyLimit = 300;
    public double radarRange = 48.0;
    public boolean showCoordinates = true;
    public boolean soundOutlineEnabled = true;
    public boolean oreOverlayEnabled = true;
    public boolean movementSounds = true;
    public boolean combatSounds = true;
    public boolean transportSounds = true;
    public boolean explosionSounds = true;
    public boolean farmingSounds = true;
    public boolean sculkSounds = true;
    public boolean naturalSounds = false;
    public boolean customSounds = true;
    public long soundMarkerLifetimeMs = 4500L;
    public int maxSoundMarkers = 70;
    private static DonutSpyConfig INSTANCE = new DonutSpyConfig();

    private DonutSpyConfig() {
    }

    public static DonutSpyConfig current() {
        return INSTANCE;
    }

    public static void load() {
        try {
            DonutSpyConfig loaded;
            if (Files.exists(PATH, new LinkOption[0]) && (loaded = (DonutSpyConfig)GSON.fromJson(Files.readString(PATH), DonutSpyConfig.class)) != null) {
                INSTANCE = loaded;
            }
        }
        catch (Exception ignored) {
            INSTANCE = new DonutSpyConfig();
        }
        DonutSpyConfig.INSTANCE.historyLimit = DonutSpyConfig.clamp(DonutSpyConfig.INSTANCE.historyLimit, 50, 1500);
        DonutSpyConfig.INSTANCE.radarRange = Math.max(8.0, Math.min(128.0, DonutSpyConfig.INSTANCE.radarRange));
        DonutSpyConfig.INSTANCE.soundMarkerLifetimeMs = Math.max(1000L, Math.min(15000L, DonutSpyConfig.INSTANCE.soundMarkerLifetimeMs));
        DonutSpyConfig.INSTANCE.maxSoundMarkers = DonutSpyConfig.clamp(DonutSpyConfig.INSTANCE.maxSoundMarkers, 10, 150);
        DonutSpyConfig.save();
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public boolean isSoundCategoryEnabled(String category) {
        return switch (category) {
            case "MOVEMENT" -> this.movementSounds;
            case "COMBAT" -> this.combatSounds;
            case "TRANSPORT" -> this.transportSounds;
            case "EXPLOSIONS" -> this.explosionSounds;
            case "FARMING" -> this.farmingSounds;
            case "SCULK" -> this.sculkSounds;
            case "NATURAL" -> this.naturalSounds;
            default -> this.customSounds && this.showOtherSounds;
        };
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent(), new FileAttribute[0]);
            Files.writeString(PATH, (CharSequence)GSON.toJson((Object)INSTANCE), new OpenOption[0]);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

