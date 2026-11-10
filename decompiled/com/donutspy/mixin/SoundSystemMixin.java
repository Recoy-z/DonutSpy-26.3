/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1113
 *  net.minecraft.class_1140
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.donutspy.mixin;

import com.donutspy.config.DonutSpyConfig;
import com.donutspy.sound.SoundEvent;
import com.donutspy.sound.SoundHistory;
import net.minecraft.class_1113;
import net.minecraft.class_1140;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_1140.class})
public abstract class SoundSystemMixin {
    @Inject(method={"play(Lnet/minecraft/client/sound/SoundInstance;)V"}, at={@At(value="HEAD")})
    private void donutspy$capture(class_1113 sound, CallbackInfo ci) {
        if (!DonutSpyConfig.current().captureEnabled) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || client.field_1687 == null || sound.method_4787()) {
            return;
        }
        double x = sound.method_4784();
        double y = sound.method_4779();
        double z = sound.method_4778();
        float volume = sound.method_4781();
        float pitch = sound.method_4782();
        if (!(Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z) && Float.isFinite(volume) && Float.isFinite(pitch) && !(volume <= 0.0f))) {
            return;
        }
        SoundEvent event = SoundEvent.from(sound.method_4775(), x, y, z, volume, pitch);
        if (!DonutSpyConfig.current().showOtherSounds && "OTHER".equals(event.category())) {
            return;
        }
        SoundHistory.add(event);
    }
}

