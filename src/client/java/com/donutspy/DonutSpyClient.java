package com.donutspy;

import com.donutspy.config.DonutSpyConfig;
import com.donutspy.gui.DonutSpyScreen;
import com.donutspy.sound.SoundHistory;
import com.donutspy.sound.SoundCaptureListener;
import com.donutspy.render.SoundHud;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

public final class DonutSpyClient implements ClientModInitializer {
    private static final KeyMapping OPEN_MENU = new KeyMapping(
            "key.donutspy.open",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_RSHIFT,
            KeyMapping.Category.MISC
    );

    private Object lastWorld;

    @Override
    public void onInitializeClient() {
        DonutSpyConfig.load();
        SoundHud.register();
        KeyMappingHelper.registerKeyMapping(OPEN_MENU);

        // Minecraft's SoundManager is not ready during mod entrypoint initialization.
        ClientLifecycleEvents.CLIENT_STARTED.register(client ->
                client.getSoundManager().addListener(SoundCaptureListener.INSTANCE)
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Object currentWorld = client.level;
            if (currentWorld != lastWorld) {
                lastWorld = currentWorld;
                SoundHistory.clear();
            }

            while (OPEN_MENU.consumeClick()) {
                if (client.screen instanceof DonutSpyScreen) {
                    client.setScreen(null);
                } else if (client.screen == null) {
                    client.setScreen(new DonutSpyScreen());
                }
            }
        });
    }
}