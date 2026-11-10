/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
 *  net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
 *  net.minecraft.class_1921
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_638
 *  net.minecraft.class_761
 */
package com.donutspy.render;

import com.donutspy.config.DonutSpyConfig;
import com.donutspy.sound.SoundEvent;
import com.donutspy.sound.SoundHistory;
import com.donutspy.world.WorldAnalysis;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.class_1921;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_638;
import net.minecraft.class_761;

public final class WorldOverlayRenderer {
    private static int tickCounter;
    private static class_638 cachedWorld;
    private static List<class_2338> cachedOrePositions;

    private WorldOverlayRenderer() {
    }

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(WorldOverlayRenderer::render);
    }

    public static void tick(class_310 client) {
        if (client.field_1687 != cachedWorld) {
            cachedWorld = client.field_1687;
            cachedOrePositions = List.of();
        }
        if (++tickCounter % 60 != 0) {
            return;
        }
        if (client.field_1724 == null || client.field_1687 == null || !client.method_1542() || client.method_1576() == null || !DonutSpyConfig.current().oreOverlayEnabled) {
            cachedOrePositions = List.of();
            return;
        }
        cachedOrePositions = WorldAnalysis.findNearbyOres(client, 1);
    }

    private static void render(WorldRenderContext context) {
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || client.field_1687 == null) {
            return;
        }
        class_4587 matrices = context.matrixStack();
        if (matrices == null || context.consumers() == null || context.camera() == null) {
            return;
        }
        class_243 camera = context.camera().method_19326();
        class_4588 lines = context.consumers().getBuffer(class_1921.method_23594());
        DonutSpyConfig config = DonutSpyConfig.current();
        if (config.soundOutlineEnabled && config.captureEnabled) {
            double maxRangeSq = config.radarRange * config.radarRange;
            long now = System.currentTimeMillis();
            int drawn = 0;
            for (SoundEvent event : SoundHistory.snapshot()) {
                double dz;
                double dy;
                double dx;
                double distanceSq;
                long age;
                if (drawn >= config.maxSoundMarkers) break;
                if (!config.isSoundCategoryEnabled(event.category()) || (age = now - event.timestamp()) < 0L || age > config.soundMarkerLifetimeMs || (distanceSq = (dx = event.x() - client.field_1724.method_23317()) * dx + (dy = event.y() - client.field_1724.method_23318()) * dy + (dz = event.z() - client.field_1724.method_23321()) * dz) > maxRangeSq) continue;
                float alpha = Math.max(0.25f, 1.0f - (float)age / (float)config.soundMarkerLifetimeMs);
                float[] color = WorldOverlayRenderer.soundColor(event.category());
                class_238 marker = new class_238(event.x() - 0.22, event.y() - 0.22, event.z() - 0.22, event.x() + 0.22, event.y() + 0.22, event.z() + 0.22).method_989(-camera.field_1352, -camera.field_1351, -camera.field_1350);
                class_761.method_22982((class_4587)matrices, (class_4588)lines, (class_238)marker, (float)color[0], (float)color[1], (float)color[2], (float)alpha);
                ++drawn;
            }
        }
        if (config.oreOverlayEnabled && client.field_1687 == cachedWorld && client.method_1542() && client.method_1576() != null) {
            int drawn = 0;
            for (class_2338 pos : cachedOrePositions) {
                if (drawn >= 220) break;
                class_2248 block = client.field_1687.method_8320(pos).method_26204();
                if (WorldAnalysis.oreName(block) == null) continue;
                float[] color = WorldOverlayRenderer.oreColor(block);
                class_238 box = new class_238(pos).method_1014(0.003).method_989(-camera.field_1352, -camera.field_1351, -camera.field_1350);
                class_761.method_22982((class_4587)matrices, (class_4588)lines, (class_238)box, (float)color[0], (float)color[1], (float)color[2], (float)0.92f);
                ++drawn;
            }
        }
    }

    private static float[] soundColor(String category) {
        float[] fArray;
        switch (category) {
            case "MOVEMENT": {
                float[] fArray2 = new float[3];
                fArray2[0] = 1.0f;
                fArray2[1] = 0.74f;
                fArray = fArray2;
                fArray2[2] = 0.24f;
                break;
            }
            case "COMBAT": {
                float[] fArray3 = new float[3];
                fArray3[0] = 1.0f;
                fArray3[1] = 0.32f;
                fArray = fArray3;
                fArray3[2] = 0.36f;
                break;
            }
            case "TRANSPORT": {
                float[] fArray4 = new float[3];
                fArray4[0] = 0.28f;
                fArray4[1] = 0.68f;
                fArray = fArray4;
                fArray4[2] = 1.0f;
                break;
            }
            case "EXPLOSIONS": {
                float[] fArray5 = new float[3];
                fArray5[0] = 1.0f;
                fArray5[1] = 0.48f;
                fArray = fArray5;
                fArray5[2] = 0.2f;
                break;
            }
            case "FARMING": {
                float[] fArray6 = new float[3];
                fArray6[0] = 0.35f;
                fArray6[1] = 0.88f;
                fArray = fArray6;
                fArray6[2] = 0.48f;
                break;
            }
            case "SCULK": {
                float[] fArray7 = new float[3];
                fArray7[0] = 0.7f;
                fArray7[1] = 0.42f;
                fArray = fArray7;
                fArray7[2] = 1.0f;
                break;
            }
            case "NATURAL": {
                float[] fArray8 = new float[3];
                fArray8[0] = 0.3f;
                fArray8[1] = 0.85f;
                fArray = fArray8;
                fArray8[2] = 0.82f;
                break;
            }
            default: {
                float[] fArray9 = new float[3];
                fArray9[0] = 0.82f;
                fArray9[1] = 0.86f;
                fArray = fArray9;
                fArray9[2] = 0.91f;
            }
        }
        return fArray;
    }

    private static float[] oreColor(class_2248 block) {
        if (block == class_2246.field_10442 || block == class_2246.field_29029) {
            return new float[]{0.25f, 0.92f, 1.0f};
        }
        if (block == class_2246.field_10013 || block == class_2246.field_29220) {
            return new float[]{0.2f, 1.0f, 0.5f};
        }
        if (block == class_2246.field_22109) {
            return new float[]{1.0f, 0.48f, 0.18f};
        }
        if (block == class_2246.field_10571 || block == class_2246.field_29026) {
            return new float[]{1.0f, 0.82f, 0.2f};
        }
        if (block == class_2246.field_10212 || block == class_2246.field_29027) {
            return new float[]{0.82f, 0.88f, 0.95f};
        }
        if (block == class_2246.field_10090 || block == class_2246.field_29028) {
            return new float[]{0.28f, 0.48f, 1.0f};
        }
        if (block == class_2246.field_10080 || block == class_2246.field_29030) {
            return new float[]{1.0f, 0.25f, 0.25f};
        }
        return new float[]{0.65f, 0.72f, 0.8f};
    }

    static {
        cachedOrePositions = List.of();
    }
}

