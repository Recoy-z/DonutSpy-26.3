/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1923
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2818
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 */
package com.donutspy.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2818;
import net.minecraft.class_310;
import net.minecraft.class_638;

public final class WorldAnalysis {
    private static volatile ScanResult lastResult = new ScanResult("Not scanned", 0, Map.of(), System.currentTimeMillis());

    private WorldAnalysis() {
    }

    public static ScanResult lastResult() {
        return lastResult;
    }

    public static ScanResult scanLoadedChunks(class_310 client) {
        if (!WorldAnalysis.isLocalWorld(client)) {
            return WorldAnalysis.set("Available only in integrated singleplayer", 0, Map.of());
        }
        class_638 world = client.field_1687;
        class_1923 center = client.field_1724.method_31476();
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<String, Integer>();
        for (String name : List.of("Diamond", "Deepslate diamond", "Emerald", "Deepslate emerald", "Gold", "Deepslate gold", "Iron", "Deepslate iron", "Lapis", "Deepslate lapis", "Redstone", "Deepslate redstone", "Coal", "Deepslate coal", "Ancient debris")) {
            counts.put(name, 0);
        }
        int chunks = 0;
        class_2338.class_2339 pos = new class_2338.class_2339();
        for (int cx = center.field_9181 - 1; cx <= center.field_9181 + 1; ++cx) {
            for (int cz = center.field_9180 - 1; cz <= center.field_9180 + 1; ++cz) {
                class_2818 chunk = world.method_2935().method_21730(cx, cz);
                if (chunk == null) continue;
                ++chunks;
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        for (int y = world.method_31607(); y <= world.method_31607() + world.method_31605() - 1; ++y) {
                            pos.method_10103((cx << 4) + x, y, (cz << 4) + z);
                            class_2248 block = chunk.method_8320((class_2338)pos).method_26204();
                            String name = WorldAnalysis.oreName(block);
                            if (name == null) continue;
                            counts.put(name, (Integer)counts.get(name) + 1);
                        }
                    }
                }
            }
        }
        return WorldAnalysis.set("Local loaded-chunk scan complete", chunks, counts);
    }

    public static List<class_2338> findNearbyOres(class_310 client, int radiusChunks) {
        if (!WorldAnalysis.isLocalWorld(client)) {
            return List.of();
        }
        class_638 world = client.field_1687;
        class_1923 center = client.field_1724.method_31476();
        ArrayList<class_2338> found = new ArrayList<class_2338>();
        class_2338.class_2339 pos = new class_2338.class_2339();
        int minY = Math.max(world.method_31607(), client.field_1724.method_31478() - 16);
        int maxY = Math.min(world.method_31607() + world.method_31605() - 1, client.field_1724.method_31478() + 16);
        for (int cx = center.field_9181 - radiusChunks; cx <= center.field_9181 + radiusChunks; ++cx) {
            for (int cz = center.field_9180 - radiusChunks; cz <= center.field_9180 + radiusChunks; ++cz) {
                class_2818 chunk = world.method_2935().method_21730(cx, cz);
                if (chunk == null) continue;
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        for (int y = minY; y <= maxY; ++y) {
                            pos.method_10103((cx << 4) + x, y, (cz << 4) + z);
                            if (WorldAnalysis.oreName(chunk.method_8320((class_2338)pos).method_26204()) == null) continue;
                            found.add(pos.method_10062());
                            if (found.size() < 120) continue;
                            return List.copyOf(found);
                        }
                    }
                }
            }
        }
        return List.copyOf(found);
    }

    private static boolean isLocalWorld(class_310 client) {
        return client != null && client.field_1724 != null && client.field_1687 != null && client.method_1542() && client.method_1576() != null;
    }

    public static String oreName(class_2248 block) {
        if (block == class_2246.field_10442) {
            return "Diamond";
        }
        if (block == class_2246.field_29029) {
            return "Deepslate diamond";
        }
        if (block == class_2246.field_10013) {
            return "Emerald";
        }
        if (block == class_2246.field_29220) {
            return "Deepslate emerald";
        }
        if (block == class_2246.field_10571) {
            return "Gold";
        }
        if (block == class_2246.field_29026) {
            return "Deepslate gold";
        }
        if (block == class_2246.field_10212) {
            return "Iron";
        }
        if (block == class_2246.field_29027) {
            return "Deepslate iron";
        }
        if (block == class_2246.field_10090) {
            return "Lapis";
        }
        if (block == class_2246.field_29028) {
            return "Deepslate lapis";
        }
        if (block == class_2246.field_10080) {
            return "Redstone";
        }
        if (block == class_2246.field_29030) {
            return "Deepslate redstone";
        }
        if (block == class_2246.field_10418) {
            return "Coal";
        }
        if (block == class_2246.field_29219) {
            return "Deepslate coal";
        }
        if (block == class_2246.field_22109) {
            return "Ancient debris";
        }
        return null;
    }

    private static ScanResult set(String status, int chunks, Map<String, Integer> counts) {
        lastResult = new ScanResult(status, chunks, Collections.unmodifiableMap(new LinkedHashMap<String, Integer>(counts)), System.currentTimeMillis());
        return lastResult;
    }

    public record ScanResult(String status, int chunksScanned, Map<String, Integer> counts, long timestamp) {
    }
}

