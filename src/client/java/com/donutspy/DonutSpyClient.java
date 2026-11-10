package com.donutspy;

import com.donutspy.config.DonutSpyConfig;
import com.donutspy.sound.SoundHistory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class DonutSpyClient implements ClientModInitializer {
    private Object lastWorld;

    @Override
    public void onInitializeClient() {
        DonutSpyConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Object currentWorld = client.level;
            if (currentWorld != lastWorld) {
                lastWorld = currentWorld;
                SoundHistory.clear();
            }
        });
    }
}