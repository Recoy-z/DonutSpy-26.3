package com.donutspy.sound;

import com.donutspy.config.DonutSpyConfig;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public final class SoundHistory {
    private static final ArrayDeque<SoundEvent> EVENTS = new ArrayDeque<>();

    private SoundHistory() {}

    public static synchronized void add(SoundEvent event) {
        EVENTS.addFirst(event);
        while (EVENTS.size() > DonutSpyConfig.current().historyLimit) {
            EVENTS.removeLast();
        }
    }

    public static synchronized List<SoundEvent> snapshot() {
        return new ArrayList<>(EVENTS);
    }

    public static synchronized void clear() {
        EVENTS.clear();
    }

    public static synchronized int size() {
        return EVENTS.size();
    }
}