package com.donutspy;

import com.donutspy.config.DonutSpyConfig;
import com.donutspy.sound.SoundHistory;
import com.donutspy.sound.SoundCaptureListener;
import com.donutspy.render.SoundHud;
import net.minecraft.client.Minecraft;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class DonutSpyClient implements ClientModInitializer {
    private Object lastWorld;

    @Override
    public void onInitializeClient() {
        DonutSpyConfig.load();
        SoundHud.register();

        // Minecraft's SoundManager is not ready during mod entrypoint initialization.
        // Wait until the client has fully started before registering our listener.
        ClientLifecycleEvents.CLIENT_STARTED.register(client ->
                client.getSoundManager().addListener(SoundCaptureListener.INSTANCE)
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Object currentWorld = client.level;
            if (currentWorld != lastWorld) {
                lastWorld = currentWorld;
                SoundHistory.clear();
            }
        });
    }
}