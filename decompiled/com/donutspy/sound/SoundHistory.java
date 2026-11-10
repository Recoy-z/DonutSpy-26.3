/*
 * Decompiled with CFR 0.152.
 */
package com.donutspy.sound;

import com.donutspy.config.DonutSpyConfig;
import com.donutspy.sound.SoundEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public final class SoundHistory {
    private static final ArrayDeque<SoundEvent> EVENTS = new ArrayDeque();

    private SoundHistory() {
    }

    public static synchronized void add(SoundEvent event) {
        EVENTS.addFirst(event);
        int limit = DonutSpyConfig.current().historyLimit;
        while (EVENTS.size() > limit) {
            EVENTS.removeLast();
        }
    }

    public static synchronized List<SoundEvent> snapshot() {
        return new ArrayList<SoundEvent>(EVENTS);
    }

    public static synchronized void clear() {
        EVENTS.clear();
    }

    public static synchronized int size() {
        return EVENTS.size();
    }
}

