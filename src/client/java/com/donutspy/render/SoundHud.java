package com.donutspy.render;

import com.donutspy.config.DonutSpyConfig;
import com.donutspy.sound.SoundEvent;
import com.donutspy.sound.SoundHistory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

import java.util.List;

/**
 * Small diagnostic HUD used while testing sound capture.
 */
public final class SoundHud {
    private static final Identifier HUD_ID =
            Identifier.fromNamespaceAndPath("donutspy", "sound_status");

    private SoundHud() {
    }

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                HUD_ID,
                SoundHud::render
        );
    }

    private static void render(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker) {
        DonutSpyConfig config = DonutSpyConfig.current();
        if (!config.hudEnabled) {
            return;
        }

        List<SoundEvent> events = SoundHistory.snapshot();
        boolean recording = config.captureEnabled;

        int left = 8;
        int top = 8;
        int width = 224;
        int height = 48;

        // Dark translucent panel with a subtle border.
        graphics.fill(left, top, left + width, top + height, 0xB010141C);
        graphics.outline(left, top, width, height, 0xFF465466);

        Minecraft minecraft = Minecraft.getInstance();
        String status = recording ? "REC" : "PAUSED";
        int statusColor = recording ? 0xFFFF5555 : 0xFFFFC04D;

        graphics.text(minecraft.font, "DONUTSPY", left + 8, top + 6, 0xFFFFFFFF, true);
        graphics.text(minecraft.font, status, left + 166, top + 6, statusColor, true);
        graphics.text(
                minecraft.font,
                "Captured sounds: " + events.size(),
                left + 8,
                top + 21,
                0xFFE0E6EF,
                true
        );

        String latest = "Latest: none yet";
        if (!events.isEmpty()) {
            SoundEvent event = events.get(0);
            String soundId = event.id();
            if (soundId.length() > 28) {
                soundId = soundId.substring(0, 25) + "...";
            }
            latest = "Latest: " + event.category() + " / " + soundId;
        }

        graphics.text(minecraft.font, latest, left + 8, top + 34, 0xFFB8C5D6, true);
    }
}
