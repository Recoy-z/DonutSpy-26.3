package com.donutspy.sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Receives notifications from Minecraft's sound engine without modifying
 * the sound engine itself.
 */
public final class SoundCaptureListener implements SoundEventListener {
    public static final SoundCaptureListener INSTANCE = new SoundCaptureListener();

    // Looping sounds can be reported repeatedly by Minecraft. Remember each
    // live SoundInstance once, without keeping it alive after playback ends.
    private final Set<SoundInstance> observedSounds =
            Collections.newSetFromMap(new WeakHashMap<>());

    private SoundCaptureListener() {
    }

    @Override
    public synchronized void onPlaySound(
            SoundInstance sound,
            WeighedSoundEvents soundEvent,
            float range
    ) {
        if (sound == null || !observedSounds.add(sound)) {
            return;
        }

        if (!com.donutspy.config.DonutSpyConfig.current().captureEnabled) {
            return;
        }

        // Relative sounds (UI clicks, music, etc.) have no useful world position.
        if (sound.isRelative()) {
            return;
        }

        double x = sound.getX();
        double y = sound.getY();
        double z = sound.getZ();
        float volume = sound.getVolume();
        float pitch = sound.getPitch();

        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)
                || !Float.isFinite(volume) || !Float.isFinite(pitch)
                || volume <= 0.0F) {
            return;
        }

        SoundEvent event = SoundEvent.from(
                sound.getIdentifier().toString(),
                x, y, z, volume, pitch
        );

        if (com.donutspy.config.DonutSpyConfig.current()
                .isSoundCategoryEnabled(event.category())) {
            SoundHistory.add(event);
        }
    }
}
