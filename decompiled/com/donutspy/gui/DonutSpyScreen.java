/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 */
package com.donutspy.gui;

import com.donutspy.audit.StaffAudit;
import com.donutspy.config.DonutSpyConfig;
import com.donutspy.sound.SoundEvent;
import com.donutspy.sound.SoundHistory;
import com.donutspy.world.WorldAnalysis;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;

public final class DonutSpyScreen
extends class_437 {
    private static final String[] TABS = new String[]{"Dashboard", "Sound Radar", "Event Log", "World Analysis", "Session Audit", "Settings"};
    private static final String[] CATEGORIES = new String[]{"MOVEMENT", "COMBAT", "TRANSPORT", "EXPLOSIONS", "FARMING", "SCULK", "NATURAL", "CUSTOM"};
    private int selectedTab;
    private int scroll;

    public DonutSpyScreen() {
        super((class_2561)class_2561.method_43470((String)"DonutSpy"));
    }

    public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
        ctx.method_25294(0, 0, this.field_22789, this.field_22790, -653915632);
        int panelW = Math.min(1040, this.field_22789 - 28);
        int panelH = Math.min(660, this.field_22790 - 28);
        int left = (this.field_22789 - panelW) / 2;
        int top = (this.field_22790 - panelH) / 2;
        int sideW = 164;
        int mainX = left + sideW + 22;
        int mainY = top + 78;
        int mainW = panelW - sideW - 44;
        int mainH = panelH - 112;
        ctx.method_25294(left - 1, top - 1, left + panelW + 1, top + panelH + 1, -13286059);
        ctx.method_25294(left, top, left + panelW, top + panelH, -15920099);
        ctx.method_25294(left, top, left + panelW, top + 58, -15392981);
        ctx.method_25294(left, top + 57, left + panelW, top + 59, -14272183);
        ctx.method_25294(left + 18, top + 15, left + 42, top + 39, -14394025);
        ctx.method_25300(this.field_22793, "D", left + 30, top + 22, -4653072);
        ctx.method_25303(this.field_22793, "DONUTSPY", left + 52, top + 15, -1510921);
        ctx.method_25303(this.field_22793, "CLIENT MODULE DESK  /  v0.4.1", left + 52, top + 31, -8087126);
        DonutSpyConfig cfg = DonutSpyConfig.current();
        String capture = cfg.captureEnabled ? "\u25cf CAPTURING" : "\u25cb PAUSED";
        this.pill(ctx, left + panelW - 135, top + 18, 112, 25, capture, cfg.captureEnabled ? -14595517 : -12962768, cfg.captureEnabled ? -7478068 : -15990);
        ctx.method_25294(left + 10, top + 70, left + sideW, top + panelH - 16, -15721947);
        this.label(ctx, "WORKSPACE", left + 22, top + 82, -8940635);
        for (int i = 0; i < TABS.length; ++i) {
            boolean active;
            int yy = top + 105 + i * 43;
            boolean bl = active = this.selectedTab == i;
            if (active) {
                ctx.method_25294(left + 17, yy - 5, left + sideW - 7, yy + 29, -14662849);
                ctx.method_25294(left + 17, yy - 5, left + 20, yy + 29, -9250366);
            } else if (mouseX >= left + 17 && mouseX <= left + sideW - 7 && mouseY >= yy - 5 && mouseY <= yy + 29) {
                ctx.method_25294(left + 17, yy - 5, left + sideW - 7, yy + 29, -15062730);
            }
            ctx.method_25303(this.field_22793, this.tabGlyph(i), left + 27, yy + 5, active ? -4917527 : -8415833);
            ctx.method_25303(this.field_22793, TABS[i], left + 48, yy + 5, active ? -1507336 : -5917245);
        }
        ctx.method_25294(left + 20, top + panelH - 70, left + sideW - 10, top + panelH - 69, -14206905);
        this.label(ctx, "SHORTCUTS", left + 22, top + panelH - 57, -8940635);
        this.label(ctx, "RIGHT SHIFT  MENU", left + 22, top + panelH - 40, -6706504);
        this.label(ctx, "F8  CAPTURE  /  F9  CLEAR", left + 22, top + panelH - 26, -6706504);
        switch (this.selectedTab) {
            case 0: {
                this.dashboard(ctx, mainX, mainY, mainW, mainH);
                break;
            }
            case 1: {
                this.radar(ctx, mainX, mainY, mainW, mainH);
                break;
            }
            case 2: {
                this.eventLog(ctx, mainX, mainY, mainW, mainH);
                break;
            }
            case 3: {
                this.world(ctx, mainX, mainY, mainW, mainH);
                break;
            }
            case 4: {
                this.audit(ctx, mainX, mainY, mainW, mainH);
                break;
            }
            case 5: {
                this.settings(ctx, mainX, mainY, mainW, mainH);
            }
        }
        ctx.method_25303(this.field_22793, "DonutSpy only visualizes sounds received by your client.", mainX, top + panelH - 23, -9929072);
        super.method_25394(ctx, mouseX, mouseY, delta);
    }

    private void dashboard(class_332 ctx, int x, int y, int w, int h) {
        this.heading(ctx, "YOUR MODULE DESK", x, y);
        this.muted(ctx, "Live overview of your client-side diagnostics.", x, y + 18);
        int gap = 10;
        int cardW = (w - gap * 2) / 3;
        this.statCard(ctx, x, y + 42, cardW, 72, "SOUND EVENTS", String.valueOf(SoundHistory.size()), "RECENT HISTORY");
        this.statCard(ctx, x + cardW + gap, y + 42, cardW, 72, "SOUND RADAR", DonutSpyConfig.current().soundOutlineEnabled && DonutSpyConfig.current().captureEnabled ? "ACTIVE" : "PAUSED / OFF", "IN-WORLD MARKERS");
        this.statCard(ctx, x + (cardW + gap) * 2, y + 42, cardW, 72, "ORE VISUALIZER", DonutSpyConfig.current().oreOverlayEnabled ? "LOCAL ONLY" : "OFF", "SINGLEPLAYER");
        this.section(ctx, x, y + 132, w, Math.min(166, h - 178), "RECENT SOUND ACTIVITY");
        List<SoundEvent> events = SoundHistory.snapshot();
        int yy = y + 168;
        for (int i = 0; i < Math.min(4, events.size()); ++i) {
            SoundEvent event = events.get(i);
            this.eventRow(ctx, x + 12, yy, w - 24, event);
            yy += 29;
        }
        if (events.isEmpty()) {
            this.muted(ctx, "Nothing captured yet. Walk around or trigger a nearby sound.", x + 16, yy + 5);
        }
        int footerY = y + h - 34;
        this.pill(ctx, x, footerY, 132, 25, "SOUND CAPTURE", -14996937, -6307371);
        this.pill(ctx, x + 142, footerY, 100, 25, DonutSpyConfig.current().captureEnabled ? "ENABLED" : "PAUSED", DonutSpyConfig.current().captureEnabled ? -14595517 : -12962768, DonutSpyConfig.current().captureEnabled ? -7478068 : -15990);
    }

    private void radar(class_332 ctx, int x, int y, int w, int h) {
        this.heading(ctx, "SOUND DETECTOR", x, y);
        this.muted(ctx, "Colored outlines mark the position of sounds played by your client.", x, y + 18);
        this.section(ctx, x, y + 48, w, h - 58, "LIVE RADAR");
        int radius = Math.min(145, Math.max(72, (h - 92) / 2));
        int cx = x + radius + 48;
        int cy = y + 48 + (h - 58) / 2;
        ctx.method_25294(cx - radius - 12, cy - radius - 12, cx + radius + 12, cy + radius + 12, -15721176);
        for (int ring = 1; ring <= 3; ++ring) {
            int rr = radius * ring / 3;
            for (int dx = -rr; dx <= rr; dx += 2) {
                int dy = (int)Math.sqrt(Math.max(0, rr * rr - dx * dx));
                ctx.method_25294(cx + dx, cy - dy, cx + dx + 2, cy - dy + 1, -14072752);
                ctx.method_25294(cx + dx, cy + dy, cx + dx + 2, cy + dy + 1, -14072752);
            }
        }
        ctx.method_25294(cx - radius, cy, cx + radius + 1, cy + 1, -14271671);
        ctx.method_25294(cx, cy - radius, cx + 1, cy + radius + 1, -14271671);
        ctx.method_25294(cx - 4, cy - 4, cx + 5, cy + 5, -8527670);
        class_310 client = class_310.method_1551();
        if (client.field_1724 != null) {
            double range = DonutSpyConfig.current().radarRange;
            for (SoundEvent e : SoundHistory.snapshot()) {
                double dz;
                double dx;
                double dist;
                if (!DonutSpyConfig.current().isSoundCategoryEnabled(e.category()) || System.currentTimeMillis() - e.timestamp() > DonutSpyConfig.current().soundMarkerLifetimeMs || (dist = Math.sqrt((dx = e.x() - client.field_1724.method_23317()) * dx + (dz = e.z() - client.field_1724.method_23321()) * dz)) > range) continue;
                int px = cx + (int)Math.round(dx / range * (double)(radius - 7));
                int pz = cy + (int)Math.round(dz / range * (double)(radius - 7));
                int color = this.categoryColor(e.category());
                ctx.method_25294(px - 4, pz - 4, px + 5, pz + 5, -16314344);
                ctx.method_25294(px - 2, pz - 2, px + 3, pz + 3, color);
            }
        }
        int rightX = Math.max(cx + radius + 24, x + w / 2 + 28);
        this.label(ctx, "CATEGORY FILTERS", rightX, y + 82, -8332088);
        int ly = y + 101;
        for (String cat : CATEGORIES) {
            ctx.method_25294(rightX, ly + 2, rightX + 9, ly + 11, this.categoryColor(cat));
            ctx.method_25303(this.field_22793, cat, rightX + 18, ly + 2, -3023386);
            ly += 20;
        }
        this.pill(ctx, rightX, ly + 3, 160, 25, "RANGE  " + (int)DonutSpyConfig.current().radarRange + " BLOCKS", -14864838, -4863787);
        this.muted(ctx, "North-up  \u2022  +X right  \u2022  +Z down", rightX, ly + 34);
    }

    private void eventLog(class_332 ctx, int x, int y, int w, int h) {
        this.heading(ctx, "EVENT LOG", x, y);
        this.muted(ctx, "Newest first  \u2022  category labels are best-effort guesses.", x, y + 18);
        this.pill(ctx, x + w - 104, y - 2, 96, 24, "F9  CLEAR", -12965584, -24152);
        this.section(ctx, x, y + 48, w, h - 58, "CAPTURED EVENTS  /  " + SoundHistory.size());
        List<SoundEvent> events = SoundHistory.snapshot();
        int visible = Math.max(1, (h - 116) / 31);
        int yy = y + 94;
        for (int i = this.scroll; i < Math.min(events.size(), this.scroll + visible); ++i) {
            this.eventRow(ctx, x + 10, yy, w - 20, events.get(i));
            yy += 31;
        }
        if (events.isEmpty()) {
            this.muted(ctx, "No captured events. Check that sound capture is enabled.", x + 16, yy + 8);
        }
        this.muted(ctx, "Scroll to browse  \u2022  " + (String)(events.isEmpty() ? "0" : this.scroll + 1 + "\u2013" + Math.min(events.size(), this.scroll + visible)) + " of " + events.size(), x + 10, y + h - 8);
    }

    private void world(class_332 ctx, int x, int y, int w, int h) {
        this.heading(ctx, "WORLD ANALYSIS", x, y);
        this.muted(ctx, "Local singleplayer diagnostics only. No seed prediction or remote-world scanning.", x, y + 18);
        this.section(ctx, x, y + 48, w, h - 58, "LOADED CHUNK INSPECTION");
        class_310 client = class_310.method_1551();
        String dimension = client.field_1687 == null ? "Not in a world" : client.field_1687.method_27983().method_29177().toString();
        this.infoRow(ctx, x + 14, y + 78, w - 28, "DIMENSION", dimension);
        this.infoRow(ctx, x + 14, y + 106, w - 28, "SEED PREDICTION", "Not implemented");
        this.infoRow(ctx, x + 14, y + 134, w - 28, "SCAN AREA", "3 \u00d7 3 already-loaded chunks");
        this.pill(ctx, x + 14, y + 166, 190, 26, "RUN LOCAL ORE SCAN", -14595517, -6493997);
        WorldAnalysis.ScanResult result = WorldAnalysis.lastResult();
        this.infoRow(ctx, x + 14, y + 202, w - 28, "LAST RESULT", result.status());
        this.infoRow(ctx, x + 14, y + 230, w - 28, "CHUNKS READ", String.valueOf(result.chunksScanned()));
        int colY = y + 260;
        int col = 0;
        int maxRows = Math.max(1, Math.min(8, (h - 286) / 20));
        int maxEntries = maxRows * 2;
        for (Map.Entry<String, Integer> entry : result.counts().entrySet()) {
            if (col >= maxEntries) break;
            int colX = x + 14 + col % 2 * (w / 2 - 2);
            this.infoRow(ctx, colX, colY + col / 2 * 20, w / 2 - 18, entry.getKey(), String.valueOf(entry.getValue()));
            ++col;
        }
        if (result.counts().size() > maxEntries) {
            this.muted(ctx, "More ore types are recorded in the scan result.", x + 14, y + h - 24);
        }
    }

    private void audit(class_332 ctx, int x, int y, int w, int h) {
        this.heading(ctx, "SESSION AUDIT", x, y);
        this.muted(ctx, "Visible player-list activity only; no hidden or vanished-player detection.", x, y + 18);
        this.section(ctx, x, y + 48, w, h - 58, "PUBLIC PLAYER LIST");
        this.infoRow(ctx, x + 14, y + 88, w - 28, "VISIBLE PLAYERS", String.valueOf(StaffAudit.visiblePlayerCount()));
        List<String> entries = StaffAudit.snapshot();
        int yy = y + 132;
        int maxEntries = Math.max(1, Math.min(15, (h - 150) / 28));
        for (int i = 0; i < Math.min(maxEntries, entries.size()); ++i) {
            ctx.method_25294(x + 10, yy, x + w - 10, yy + 25, i % 2 == 0 ? -15260623 : -15458517);
            String text = entries.get(i);
            ctx.method_25303(this.field_22793, (String)(text.length() > 95 ? text.substring(0, 92) + "..." : text), x + 18, yy + 8, -3549214);
            yy += 28;
        }
        if (entries.isEmpty()) {
            this.muted(ctx, "No session events recorded yet.", x + 16, yy + 8);
        }
    }

    private void settings(class_332 ctx, int x, int y, int w, int h) {
        this.heading(ctx, "MODULE SETTINGS", x, y);
        this.muted(ctx, "Click a module to enable or disable it. Changes save automatically.", x, y + 18);
        int leftW = Math.max(250, (w - 18) / 2);
        int rightX = x + leftW + 18;
        this.section(ctx, x, y + 48, leftW, h - 58, "GENERAL MODULES");
        String[] coreNames = new String[]{"Sound capture", "HUD status", "Sound outlines", "Local ore outlines", "Radar range"};
        int[] coreY = new int[]{y + 88, y + 135, y + 182, y + 229, y + 276};
        DonutSpyConfig c = DonutSpyConfig.current();
        String[] values = new String[]{this.on(c.captureEnabled), this.on(c.hudEnabled), this.on(c.soundOutlineEnabled), this.on(c.oreOverlayEnabled), (int)c.radarRange + " blocks  \u203a"};
        for (int i = 0; i < coreNames.length; ++i) {
            this.toggleRow(ctx, x + 10, coreY[i], leftW - 20, coreNames[i], values[i], i == 4);
        }
        int rightW = w - leftW - 18;
        this.section(ctx, rightX, y + 48, rightW, h - 58, "SOUND DETECTOR CATEGORIES");
        int categoryW = (rightW - 30) / 2;
        for (int i = 0; i < CATEGORIES.length; ++i) {
            int col = i % 2;
            int row = i / 2;
            int xx = rightX + 10 + col * (categoryW + 8);
            int yy = y + 91 + row * 57;
            this.compactToggleRow(ctx, xx, yy, categoryW, this.titleCase(CATEGORIES[i]), this.on(DonutSpyScreen.categoryEnabled(c, CATEGORIES[i])));
        }
        this.muted(ctx, "Ore outlines scan local singleplayer only; no multiplayer ore reveal.", x, y + h - 12);
    }

    private static boolean categoryEnabled(DonutSpyConfig c, String category) {
        return switch (category) {
            case "MOVEMENT" -> c.movementSounds;
            case "COMBAT" -> c.combatSounds;
            case "TRANSPORT" -> c.transportSounds;
            case "EXPLOSIONS" -> c.explosionSounds;
            case "FARMING" -> c.farmingSounds;
            case "SCULK" -> c.sculkSounds;
            case "NATURAL" -> c.naturalSounds;
            default -> c.customSounds;
        };
    }

    private void eventRow(class_332 ctx, int x, int y, int w, SoundEvent e) {
        ctx.method_25294(x, y, x + w, y + 26, -15260623);
        ctx.method_25294(x, y, x + 3, y + 26, this.categoryColor(e.category()));
        Object name = e.id().toString();
        if (((String)name).length() > 39) {
            name = ((String)name).substring(0, 36) + "...";
        }
        ctx.method_25303(this.field_22793, (String)name, x + 10, y + 8, -2300436);
        String meta = e.category() + "  \u2022  " + String.format(Locale.ROOT, "%.0f, %.0f, %.0f", e.x(), e.y(), e.z());
        int mw = this.field_22793.method_1727(meta);
        ctx.method_25303(this.field_22793, meta, x + w - mw - 10, y + 8, this.categoryColor(e.category()));
    }

    private void statCard(class_332 ctx, int x, int y, int w, int h, String title, String value, String subtitle) {
        ctx.method_25294(x, y, x + w, y + h, -15260623);
        ctx.method_25294(x, y, x + w, y + 2, -12748689);
        this.label(ctx, title, x + 12, y + 12, -7231561);
        ctx.method_25303(this.field_22793, value, x + 12, y + 31, -1968144);
        this.label(ctx, subtitle, x + 12, y + 54, -9336679);
    }

    private void section(class_332 ctx, int x, int y, int w, int h, String title) {
        ctx.method_25294(x, y, x + w, y + h, -15655897);
        ctx.method_25294(x, y, x + w, y + 1, -14009270);
        this.label(ctx, title, x + 12, y + 13, -8201016);
    }

    private void toggleRow(class_332 ctx, int x, int y, int w, String name, String value, boolean action) {
        int color;
        ctx.method_25294(x, y, x + w, y + 34, -15062730);
        ctx.method_25303(this.field_22793, name, x + 10, y + 12, -2826264);
        int n = action ? -14403513 : (color = "ON".equals(value) ? -14395057 : -12963790);
        int textColor = action ? -4206373 : ("ON".equals(value) ? -6559018 : -19276);
        this.pill(ctx, x + w - 91, y + 5, 82, 24, value, color, textColor);
    }

    private void compactToggleRow(class_332 ctx, int x, int y, int w, String name, String value) {
        ctx.method_25294(x, y, x + w, y + 34, -15062730);
        ctx.method_25303(this.field_22793, name, x + 8, y + 12, -2826264);
        int bg = "ON".equals(value) ? -14395057 : -12963790;
        int fg = "ON".equals(value) ? -6559018 : -19276;
        this.pill(ctx, x + w - 49, y + 5, 42, 24, value, bg, fg);
    }

    private void infoRow(class_332 ctx, int x, int y, int w, String leftText, String rightText) {
        ctx.method_25294(x, y, x + w, y + 25, -15194573);
        ctx.method_25303(this.field_22793, leftText, x + 8, y + 8, -6574146);
        Object clipped = rightText;
        int max = w - this.field_22793.method_1727(leftText) - 38;
        while (this.field_22793.method_1727((String)clipped) > max && ((String)clipped).length() > 4) {
            clipped = ((String)clipped).substring(0, ((String)clipped).length() - 4) + "...";
        }
        ctx.method_25303(this.field_22793, (String)clipped, x + w - this.field_22793.method_1727((String)clipped) - 8, y + 8, -3611170);
    }

    private void pill(class_332 ctx, int x, int y, int w, int h, String text, int bg, int fg) {
        ctx.method_25294(x, y, x + w, y + h, bg);
        ctx.method_25300(this.field_22793, text, x + w / 2, y + (h - 8) / 2, fg);
    }

    private void heading(class_332 ctx, String text, int x, int y) {
        ctx.method_25303(this.field_22793, text, x, y, -1576201);
    }

    private void muted(class_332 ctx, String text, int x, int y) {
        ctx.method_25303(this.field_22793, text, x, y, -8350555);
    }

    private void label(class_332 ctx, String text, int x, int y, int color) {
        ctx.method_25303(this.field_22793, text, x, y, color);
    }

    private String tabGlyph(int tab) {
        return switch (tab) {
            case 0 -> "\u25c6";
            case 1 -> "\u25ce";
            case 2 -> "\u2261";
            case 3 -> "\u25c7";
            case 4 -> "\u25c9";
            default -> "\u2699";
        };
    }

    private String on(boolean enabled) {
        return enabled ? "ON" : "OFF";
    }

    private String titleCase(String s) {
        return s.substring(0, 1) + s.substring(1).toLowerCase(Locale.ROOT);
    }

    private int categoryColor(String category) {
        return switch (category) {
            case "MOVEMENT" -> -15253;
            case "COMBAT" -> -37515;
            case "TRANSPORT" -> -10045185;
            case "EXPLOSIONS" -> -30136;
            case "FARMING" -> -8988277;
            case "SCULK" -> -4879361;
            case "NATURAL" -> -10758970;
            default -> -4996660;
        };
    }

    public boolean method_25402(double mouseX, double mouseY, int button) {
        int panelW = Math.min(1040, this.field_22789 - 28);
        int panelH = Math.min(660, this.field_22790 - 28);
        int left = (this.field_22789 - panelW) / 2;
        int top = (this.field_22790 - panelH) / 2;
        int sideW = 164;
        int mainX = left + sideW + 22;
        int mainY = top + 78;
        int mainW = panelW - sideW - 44;
        int mainH = panelH - 112;
        for (int i = 0; i < TABS.length; ++i) {
            int yy = top + 105 + i * 43;
            if (!(mouseX >= (double)(left + 17)) || !(mouseX <= (double)(left + sideW - 7)) || !(mouseY >= (double)(yy - 5)) || !(mouseY <= (double)(yy + 29))) continue;
            this.selectedTab = i;
            this.scroll = 0;
            return true;
        }
        DonutSpyConfig c = DonutSpyConfig.current();
        if (this.selectedTab == 2 && mouseX >= (double)(mainX + mainW - 104) && mouseX <= (double)(mainX + mainW - 8) && mouseY >= (double)(mainY - 2) && mouseY <= (double)(mainY + 22)) {
            SoundHistory.clear();
            this.scroll = 0;
            return true;
        }
        if (this.selectedTab == 3 && mouseX >= (double)(mainX + 14) && mouseX <= (double)(mainX + 204) && mouseY >= (double)(mainY + 166) && mouseY <= (double)(mainY + 192)) {
            WorldAnalysis.scanLoadedChunks(class_310.method_1551());
            return true;
        }
        if (this.selectedTab == 5) {
            int leftW = Math.max(250, (mainW - 18) / 2);
            int rightX = mainX + leftW + 18;
            int[] cy = new int[]{mainY + 88, mainY + 135, mainY + 182, mainY + 229, mainY + 276};
            if (mouseX >= (double)(mainX + 10) && mouseX <= (double)(mainX + leftW - 10)) {
                for (int i = 0; i < cy.length; ++i) {
                    if (!(mouseY >= (double)cy[i]) || !(mouseY <= (double)(cy[i] + 34))) continue;
                    switch (i) {
                        case 0: {
                            c.captureEnabled = !c.captureEnabled;
                            break;
                        }
                        case 1: {
                            c.hudEnabled = !c.hudEnabled;
                            break;
                        }
                        case 2: {
                            c.soundOutlineEnabled = !c.soundOutlineEnabled;
                            break;
                        }
                        case 3: {
                            c.oreOverlayEnabled = !c.oreOverlayEnabled;
                            break;
                        }
                        case 4: {
                            c.radarRange = c.radarRange >= 128.0 ? 24.0 : c.radarRange + 16.0;
                        }
                    }
                    DonutSpyConfig.save();
                    return true;
                }
            }
            int rightW = mainW - leftW - 18;
            int categoryW = (rightW - 30) / 2;
            if (mouseX >= (double)(rightX + 10) && mouseX <= (double)(mainX + mainW - 10)) {
                for (int i = 0; i < CATEGORIES.length; ++i) {
                    int col = i % 2;
                    int row = i / 2;
                    int xx = rightX + 10 + col * (categoryW + 8);
                    int yy = mainY + 91 + row * 57;
                    if (!(mouseX >= (double)xx) || !(mouseX <= (double)(xx + categoryW)) || !(mouseY >= (double)yy) || !(mouseY <= (double)(yy + 34))) continue;
                    switch (CATEGORIES[i]) {
                        case "MOVEMENT": {
                            c.movementSounds = !c.movementSounds;
                            break;
                        }
                        case "COMBAT": {
                            c.combatSounds = !c.combatSounds;
                            break;
                        }
                        case "TRANSPORT": {
                            c.transportSounds = !c.transportSounds;
                            break;
                        }
                        case "EXPLOSIONS": {
                            c.explosionSounds = !c.explosionSounds;
                            break;
                        }
                        case "FARMING": {
                            c.farmingSounds = !c.farmingSounds;
                            break;
                        }
                        case "SCULK": {
                            c.sculkSounds = !c.sculkSounds;
                            break;
                        }
                        case "NATURAL": {
                            c.naturalSounds = !c.naturalSounds;
                            break;
                        }
                        default: {
                            c.customSounds = !c.customSounds;
                        }
                    }
                    DonutSpyConfig.save();
                    return true;
                }
            }
        }
        return super.method_25402(mouseX, mouseY, button);
    }

    public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.selectedTab == 2) {
            int panelH = Math.min(660, this.field_22790 - 28);
            int mainH = panelH - 112;
            int visible = Math.max(1, (mainH - 116) / 31);
            this.scroll = Math.max(0, Math.min(Math.max(0, SoundHistory.size() - visible), this.scroll + (verticalAmount < 0.0 ? 3 : -3)));
            return true;
        }
        return super.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public boolean method_25404(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.method_25419();
            return true;
        }
        return super.method_25404(keyCode, scanCode, modifiers);
    }

    public boolean method_25421() {
        return false;
    }
}

