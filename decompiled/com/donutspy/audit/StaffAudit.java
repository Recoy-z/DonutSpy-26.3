/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 */
package com.donutspy.audit;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.class_310;

public final class StaffAudit {
    private static final int MAX_ENTRIES = 100;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final ArrayDeque<String> ENTRIES = new ArrayDeque();
    private static final Set<String> LAST_NAMES = new HashSet<String>();
    private static boolean initialized;

    private StaffAudit() {
    }

    public static synchronized void tick(class_310 client) {
        if (client.method_1562() == null) {
            initialized = false;
            LAST_NAMES.clear();
            return;
        }
        HashSet current = new HashSet();
        client.method_1562().method_2880().forEach(entry -> current.add(entry.method_2966().getName()));
        if (!initialized) {
            LAST_NAMES.clear();
            LAST_NAMES.addAll(current);
            initialized = true;
            StaffAudit.add("Session snapshot: " + current.size() + " visible players");
            return;
        }
        for (String name : current) {
            if (LAST_NAMES.contains(name)) continue;
            StaffAudit.add("PLAYER LIST JOIN: " + name);
        }
        for (String name : LAST_NAMES) {
            if (current.contains(name)) continue;
            StaffAudit.add("PLAYER LIST LEAVE: " + name);
        }
        LAST_NAMES.clear();
        LAST_NAMES.addAll(current);
    }

    private static synchronized void add(String message) {
        ENTRIES.addFirst("[" + LocalTime.now().format(TIME_FORMAT) + "]  " + message);
        while (ENTRIES.size() > 100) {
            ENTRIES.removeLast();
        }
    }

    public static synchronized List<String> snapshot() {
        return new ArrayList<String>(ENTRIES);
    }

    public static synchronized int visiblePlayerCount() {
        return LAST_NAMES.size();
    }

    public static synchronized void clear() {
        ENTRIES.clear();
    }
}

