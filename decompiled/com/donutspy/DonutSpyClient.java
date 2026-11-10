/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
 *  net.minecraft.class_2561
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_437
 *  net.minecraft.class_638
 */
package com.donutspy;

import com.donutspy.audit.StaffAudit;
import com.donutspy.config.DonutSpyConfig;
import com.donutspy.gui.DonutSpyScreen;
import com.donutspy.render.WorldOverlayRenderer;
import com.donutspy.sound.SoundHistory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.class_2561;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import net.minecraft.class_638;

public final class DonutSpyClient
implements ClientModInitializer {
    public static final String MOD_ID = "donutspy";
    private static class_304 openGui;
    private static class_304 toggleCapture;
    private static class_304 clearEvents;
    private static class_638 lastWorld;

    public void onInitializeClient() {
        WorldOverlayRenderer.register();
        DonutSpyConfig.load();
        openGui = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.donutspy.open", class_3675.class_307.field_1668, 344, "category.donutspy.controls"));
        toggleCapture = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.donutspy.capture", class_3675.class_307.field_1668, 297, "category.donutspy.controls"));
        clearEvents = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.donutspy.clear", class_3675.class_307.field_1668, 298, "category.donutspy.controls"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.field_1687 != lastWorld) {
                lastWorld = client.field_1687;
                SoundHistory.clear();
            }
            StaffAudit.tick(client);
            WorldOverlayRenderer.tick(client);
            while (openGui.method_1436()) {
                if (client.field_1755 instanceof DonutSpyScreen) {
                    client.method_1507(null);
                    continue;
                }
                if (client.field_1755 != null) continue;
                client.method_1507((class_437)new DonutSpyScreen());
            }
            while (toggleCapture.method_1436()) {
                DonutSpyConfig.current().captureEnabled = !DonutSpyConfig.current().captureEnabled;
                DonutSpyConfig.save();
                if (client.field_1724 == null) continue;
                client.field_1724.method_7353((class_2561)class_2561.method_43470((String)("DonutSpy sound capture: " + (DonutSpyConfig.current().captureEnabled ? "ON" : "OFF"))), true);
            }
            while (clearEvents.method_1436()) {
                SoundHistory.clear();
                if (client.field_1724 == null) continue;
                client.field_1724.method_7353((class_2561)class_2561.method_43470((String)"DonutSpy event history cleared."), true);
            }
        });
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (!DonutSpyConfig.current().hudEnabled) {
                return;
            }
            class_310 client = class_310.method_1551();
            if (client.field_1724 == null) {
                return;
            }
            String status = "DonutSpy " + (DonutSpyConfig.current().captureEnabled ? "\u25cf REC" : "\u25cb PAUSED") + "  Events: " + SoundHistory.size();
            drawContext.method_25303(client.field_1772, status, 8, 8, -4732968);
        });
    }
}

