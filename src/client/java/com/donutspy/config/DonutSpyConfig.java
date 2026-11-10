package com.donutspy.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DonutSpyConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir()
            .resolve("donutspy").resolve("config.json");

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

    private static DonutSpyConfig instance = new DonutSpyConfig();

    private DonutSpyConfig() {}

    public static DonutSpyConfig current() {
        return instance;
    }

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                DonutSpyConfig loaded = GSON.fromJson(Files.readString(PATH), DonutSpyConfig.class);
                if (loaded != null) instance = loaded;
            }
        } catch (Exception ignored) {
            instance = new DonutSpyConfig();
        }

        instance.historyLimit = clamp(instance.historyLimit, 50, 1500);
        instance.radarRange = Math.max(8.0, Math.min(128.0, instance.radarRange));
        instance.soundMarkerLifetimeMs = Math.max(1000L, Math.min(15000L, instance.soundMarkerLifetimeMs));
        instance.maxSoundMarkers = clamp(instance.maxSoundMarkers, 10, 150);
        save();
    }

    public boolean isSoundCategoryEnabled(String category) {
        return switch (category) {
            case "MOVEMENT" -> movementSounds;
            case "COMBAT" -> combatSounds;
            case "TRANSPORT" -> transportSounds;
            case "EXPLOSIONS" -> explosionSounds;
            case "FARMING" -> farmingSounds;
            case "SCULK" -> sculkSounds;
            case "NATURAL" -> naturalSounds;
            default -> customSounds && showOtherSounds;
        };
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(instance));
        } catch (IOException ignored) {
            // Config saving must not crash the client.
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}