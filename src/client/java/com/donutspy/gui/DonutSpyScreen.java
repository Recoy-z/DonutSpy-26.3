package com.donutspy.gui;

import com.donutspy.config.DonutSpyConfig;
import com.donutspy.sound.SoundEvent;
import com.donutspy.sound.SoundHistory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * DonutSpy settings screen with a horizontal tab for each client module.
 */
public final class DonutSpyScreen extends Screen {
    private static final String[] TABS = {
            "Sound Detector", "Sound Markers", "HUD",
            "Ore Overlay", "Event History", "World Analysis"
    };

    private static final int BG = 0xF0141820;
    private static final int PANEL = 0xFF1D232D;
    private static final int LINE = 0xFF394554;
    private static final int TEXT = 0xFFE8EDF4;
    private static final int MUTED = 0xFFAAB6C5;
    private static final int ACCENT = 0xFFD7D98A;

    private final int selectedTab;

    public DonutSpyScreen() {
        this(0);
    }

    private DonutSpyScreen(int selectedTab) {
        super(Component.literal("DonutSpy"));
        this.selectedTab = Math.max(0, Math.min(TABS.length - 1, selectedTab));
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(700, this.width - 20);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(10, (this.height - 390) / 2);

        int gap = 4;
        int tabWidth = (panelWidth - 24 - gap * (TABS.length - 1)) / TABS.length;
        int tabsY = top + 55;

        for (int i = 0; i < TABS.length; i++) {
            final int tab = i;
            int x = left + 12 + i * (tabWidth + gap);
            addSafeButton((i == selectedTab ? "• " : "") + TABS[i],
                    x, tabsY, tabWidth, 24,
                    () -> this.minecraft.gui.setScreen(new DonutSpyScreen(tab)));
        }

        int contentLeft = left + 22;
        int contentWidth = panelWidth - 44;
        int contentTop = top + 153;
        int rowWidth = Math.min(270, (contentWidth - 20) / 2);

        switch (selectedTab) {
            case 0 -> {
                addToggle("Sound capture", DonutSpyConfig.current().captureEnabled,
                        contentLeft, contentTop, contentWidth, "capture");
                addToggle("Movement sounds", DonutSpyConfig.current().movementSounds,
                        contentLeft, contentTop + 48, rowWidth, "movement");
                addToggle("Combat sounds", DonutSpyConfig.current().combatSounds,
                        contentLeft + rowWidth + 12, contentTop + 48, rowWidth, "combat");
                addToggle("Transport sounds", DonutSpyConfig.current().transportSounds,
                        contentLeft, contentTop + 82, rowWidth, "transport");
                addToggle("Explosions", DonutSpyConfig.current().explosionSounds,
                        contentLeft + rowWidth + 12, contentTop + 82, rowWidth, "explosions");
                addToggle("Farming", DonutSpyConfig.current().farmingSounds,
                        contentLeft, contentTop + 116, rowWidth, "farming");
                addToggle("Sculk", DonutSpyConfig.current().sculkSounds,
                        contentLeft + rowWidth + 12, contentTop + 116, rowWidth, "sculk");
                addToggle("Natural sounds", DonutSpyConfig.current().naturalSounds,
                        contentLeft, contentTop + 150, rowWidth, "natural");
                addToggle("Other sounds", DonutSpyConfig.current().customSounds,
                        contentLeft + rowWidth + 12, contentTop + 150, rowWidth, "custom");
            }
            case 1 -> {
                addToggle("Enable sound markers", DonutSpyConfig.current().soundOutlineEnabled,
                        contentLeft, contentTop, contentWidth, "markers");
                addInfo(contentLeft, contentTop + 48, "Marker lifetime",
                        (DonutSpyConfig.current().soundMarkerLifetimeMs / 1000.0) + " seconds");
                addInfo(contentLeft, contentTop + 88, "Maximum markers",
                        String.valueOf(DonutSpyConfig.current().maxSoundMarkers));
            }
            case 2 -> {
                addToggle("Enable DonutSpy HUD", DonutSpyConfig.current().hudEnabled,
                        contentLeft, contentTop, contentWidth, "hud");
                addToggle("Show coordinates", DonutSpyConfig.current().showCoordinates,
                        contentLeft, contentTop + 48, contentWidth, "coordinates");
            }
            case 3 -> {
                addToggle("Enable ore overlay", DonutSpyConfig.current().oreOverlayEnabled,
                        contentLeft, contentTop, contentWidth, "ore");
                addInfo(contentLeft, contentTop + 55, "Availability",
                        "Integrated singleplayer only");
            }
            case 4 -> {
                addButton("Clear event history", contentLeft, contentTop, 190, 24,
                        SoundHistory::clear);
                addInfo(contentLeft, contentTop + 48, "Captured events",
                        String.valueOf(SoundHistory.size()));
            }
            case 5 -> {
                // Intentionally informational until the safe singleplayer scanner is ported.
            }
        }

        addSafeButton("Close", this.width / 2 - 45, Math.min(this.height - 30, top + 370),
                90, 22, () -> this.minecraft.gui.setScreen(null));
    }

    private void addSafeButton(String label, int x, int y, int width, int height, Runnable action) {
        if (width <= 0 || height <= 0 || x < 0 || y < 0
                || x + width > this.width || y + height > this.height) {
            return;
        }
        Button hitbox = Button.builder(Component.literal(label),
                button -> action.run()).bounds(x, y, width, height).build();
        // Keep the real clickable widget, but draw our own flat client-style control.
        hitbox.setAlpha(0.0F);
        this.addRenderableWidget(hitbox);
    }

    private void addButton(String label, int x, int y, int width, int height, Runnable action) {
        addSafeButton(label, x, y, width, height, action);
    }

    private void addToggle(String label, boolean enabled, int x, int y, int width, String option) {
        addButton(label + (enabled ? "     ON" : "     OFF"), x, y, width, 25, () -> {
            toggle(option);
            this.minecraft.gui.setScreen(new DonutSpyScreen(selectedTab));
        });
    }

    private void addInfo(int x, int y, String label, String value) {
        // Informational rows are rendered by extractRenderState.
    }

    private static void toggle(String option) {
        DonutSpyConfig c = DonutSpyConfig.current();
        switch (option) {
            case "capture" -> c.captureEnabled = !c.captureEnabled;
            case "markers" -> c.soundOutlineEnabled = !c.soundOutlineEnabled;
            case "hud" -> c.hudEnabled = !c.hudEnabled;
            case "coordinates" -> c.showCoordinates = !c.showCoordinates;
            case "ore" -> c.oreOverlayEnabled = !c.oreOverlayEnabled;
            case "movement" -> c.movementSounds = !c.movementSounds;
            case "combat" -> c.combatSounds = !c.combatSounds;
            case "transport" -> c.transportSounds = !c.transportSounds;
            case "explosions" -> c.explosionSounds = !c.explosionSounds;
            case "farming" -> c.farmingSounds = !c.farmingSounds;
            case "sculk" -> c.sculkSounds = !c.sculkSounds;
            case "natural" -> c.naturalSounds = !c.naturalSounds;
            case "custom" -> c.customSounds = !c.customSounds;
        }
        DonutSpyConfig.save();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int panelWidth = Math.min(700, this.width - 20);
        int panelHeight = Math.min(390, this.height - 20);
        int left = (this.width - panelWidth) / 2;
        int top = (this.height - panelHeight) / 2;
        int right = left + panelWidth;
        int bottom = top + panelHeight;

        graphics.fill(0, 0, this.width, this.height, 0x99070B12);
        graphics.fill(left - 2, top - 2, right + 2, bottom + 2, 0xFF080B10);
        graphics.fill(left, top, right, bottom, BG);
        graphics.fill(left, top, right, top + 43, PANEL);
        graphics.fill(left + 1, top + 42, right - 1, top + 44, LINE);

        // Header
        graphics.fill(left + 17, top + 12, left + 39, top + 34, 0xFF536B4B);
        graphics.centeredText(this.font, "D", left + 28, top + 19, 0xFFFFFFFF);
        graphics.text(this.font, "DONUTSPY", left + 49, top + 11, TEXT, true);
        graphics.text(this.font, "CLIENT MODULE SETTINGS", left + 49, top + 25, MUTED, false);
        String status = DonutSpyConfig.current().captureEnabled ? "● CAPTURING" : "○ PAUSED";
        graphics.text(this.font, status, right - 105, top + 17,
                DonutSpyConfig.current().captureEnabled ? ACCENT : MUTED, true);

        // Tab strip and active-tab underline
        int gap = 4;
        int tabWidth = (panelWidth - 24 - gap * (TABS.length - 1)) / TABS.length;
        int tabsY = top + 55;
        for (int i = 0; i < TABS.length; i++) {
            int x = left + 12 + i * (tabWidth + gap);
            boolean active = i == selectedTab;
            graphics.fill(x, tabsY - 3, x + tabWidth, tabsY + 25,
                    active ? 0xFF354237 : 0xFF222A35);
            graphics.outline(x, tabsY - 3, tabWidth, 28,
                    active ? 0xFF77865E : 0xFF3A4654);
            if (active) {
                graphics.fill(x + 5, tabsY + 23, x + tabWidth - 5, tabsY + 25, ACCENT);
            }
            graphics.centeredText(this.font, TABS[i], x + tabWidth / 2, tabsY + 7,
                    active ? 0xFFF0F1D7 : TEXT);
        }

        // Content area
        int contentLeft = left + 22;
        int contentTop = top + 103;
        int contentWidth = panelWidth - 44;
        graphics.text(this.font, TABS[selectedTab].toUpperCase(), contentLeft, contentTop, ACCENT, true);
        graphics.text(this.font, description(selectedTab), contentLeft, contentTop + 17, MUTED, false);
        graphics.fill(contentLeft, contentTop + 36, contentLeft + contentWidth,
                contentTop + 37, LINE);

        // Flat, compact setting rows. Invisible button hitboxes sit over these controls.
        DonutSpyConfig cfg = DonutSpyConfig.current();
        if (selectedTab == 0) {
            drawToggleRow(graphics, contentLeft, contentTop + 52, contentWidth, "Sound capture", cfg.captureEnabled);
            drawToggleRow(graphics, contentLeft, contentTop + 88, (contentWidth - 12) / 2, "Movement sounds", cfg.movementSounds);
            drawToggleRow(graphics, contentLeft + (contentWidth + 12) / 2, contentTop + 88, (contentWidth - 12) / 2, "Combat sounds", cfg.combatSounds);
            drawToggleRow(graphics, contentLeft, contentTop + 122, (contentWidth - 12) / 2, "Transport sounds", cfg.transportSounds);
            drawToggleRow(graphics, contentLeft + (contentWidth + 12) / 2, contentTop + 122, (contentWidth - 12) / 2, "Explosions", cfg.explosionSounds);
            drawToggleRow(graphics, contentLeft, contentTop + 156, (contentWidth - 12) / 2, "Farming", cfg.farmingSounds);
            drawToggleRow(graphics, contentLeft + (contentWidth + 12) / 2, contentTop + 156, (contentWidth - 12) / 2, "Sculk", cfg.sculkSounds);
            drawToggleRow(graphics, contentLeft, contentTop + 190, (contentWidth - 12) / 2, "Natural sounds", cfg.naturalSounds);
            drawToggleRow(graphics, contentLeft + (contentWidth + 12) / 2, contentTop + 190, (contentWidth - 12) / 2, "Other sounds", cfg.customSounds);
        } else if (selectedTab == 1) {
            drawToggleRow(graphics, contentLeft, contentTop + 52, contentWidth, "Enable sound markers", cfg.soundOutlineEnabled);
        } else if (selectedTab == 2) {
            drawToggleRow(graphics, contentLeft, contentTop + 52, contentWidth, "Enable DonutSpy HUD", cfg.hudEnabled);
            drawToggleRow(graphics, contentLeft, contentTop + 88, contentWidth, "Show coordinates", cfg.showCoordinates);
        } else if (selectedTab == 3) {
            drawToggleRow(graphics, contentLeft, contentTop + 52, contentWidth, "Enable ore overlay", cfg.oreOverlayEnabled);
        } else if (selectedTab == 4) {
            graphics.fill(contentLeft, contentTop + 52, contentLeft + 190, contentTop + 80, 0xFF293440);
            graphics.outline(contentLeft, contentTop + 52, 190, 28, 0xFF526173);
            graphics.centeredText(this.font, "Clear event history", contentLeft + 95, contentTop + 61, TEXT);
        }

        if (selectedTab == 1) {
            drawInfoRow(graphics, contentLeft, contentTop + 112, "Marker lifetime",
                    (DonutSpyConfig.current().soundMarkerLifetimeMs / 1000.0) + " seconds");
            drawInfoRow(graphics, contentLeft, contentTop + 152, "Maximum markers",
                    String.valueOf(DonutSpyConfig.current().maxSoundMarkers));
        } else if (selectedTab == 3) {
            drawInfoRow(graphics, contentLeft, contentTop + 112, "Availability",
                    "Integrated singleplayer only");
        } else if (selectedTab == 4) {
            drawInfoRow(graphics, contentLeft, contentTop + 112, "Captured events",
                    String.valueOf(SoundHistory.size()));
            drawEvents(graphics, contentLeft, contentTop + 155, contentWidth);
        } else if (selectedTab == 5) {
            Minecraft mc = Minecraft.getInstance();
            String message;
            if (mc.level == null) {
                message = "Enter a world to inspect local data.";
            } else if (mc.getSingleplayerServer() == null) {
                message = "Available only in integrated singleplayer.";
            } else {
                message = "Singleplayer world detected. Chunk analysis is not ported yet.";
            }
            drawInfoRow(graphics, contentLeft, contentTop + 58, "World analysis", message);
            graphics.text(this.font, "DonutSpy will not scan hidden ores on multiplayer servers.",
                    contentLeft, contentTop + 102, MUTED, false);
        }

        // Close control and understated footer.
        graphics.fill(this.width / 2 - 45, bottom - 55, this.width / 2 + 45, bottom - 31, 0xFF293440);
        graphics.outline(this.width / 2 - 45, bottom - 55, 90, 24, 0xFF526173);
        graphics.centeredText(this.font, "Close", this.width / 2, bottom - 47, TEXT);

        graphics.fill(left + 14, bottom - 31, right - 14, bottom - 30, LINE);
        graphics.text(this.font, "DONUTSPY  /  " + (selectedTab + 1) + " OF " + TABS.length,
                left + 18, bottom - 20, MUTED, false);
        graphics.text(this.font, "SETTINGS SAVE AUTOMATICALLY",
                right - 170, bottom - 20, MUTED, false);

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    private void drawToggleRow(GuiGraphicsExtractor graphics, int x, int y, int width,
                               String label, boolean enabled) {
        graphics.fill(x, y, x + width, y + 29, 0xFF222B36);
        graphics.outline(x, y, width, 29, 0xFF394654);
        graphics.text(this.font, label, x + 10, y + 9, TEXT, false);
        int switchWidth = 34;
        int switchX = x + width - switchWidth - 9;
        graphics.fill(switchX, y + 6, switchX + switchWidth, y + 23,
                enabled ? 0xFF65734B : 0xFF414B59);
        graphics.fill(switchX + (enabled ? 18 : 2), y + 8,
                switchX + (enabled ? 30 : 14), y + 21, 0xFFE9EDF0);
        graphics.text(this.font, enabled ? "ON" : "OFF",
                switchX - 27, y + 10, enabled ? ACCENT : MUTED, true);
    }

    private String description(int tab) {
        return switch (tab) {
            case 0 -> "Detect and classify sounds played at world positions.";
            case 1 -> "Configure temporary visual markers for captured sounds.";
            case 2 -> "Control the DonutSpy on-screen status display.";
            case 3 -> "Local ore information for your own singleplayer world.";
            case 4 -> "Review the latest captured sound events.";
            default -> "Inspect local world data when playing singleplayer.";
        };
    }

    private void drawInfoRow(GuiGraphicsExtractor graphics, int x, int y, String label, String value) {
        graphics.fill(x, y, x + 360, y + 31, 0xFF202630);
        graphics.text(this.font, label, x + 10, y + 5, MUTED, false);
        graphics.text(this.font, value, x + 180, y + 5, TEXT, true);
    }

    private void drawEvents(GuiGraphicsExtractor graphics, int x, int y, int width) {
        List<SoundEvent> events = SoundHistory.snapshot();
        int rowY = y;
        for (int i = 0; i < Math.min(6, events.size()); i++) {
            SoundEvent event = events.get(i);
            graphics.fill(x, rowY, x + width, rowY + 22, 0xFF202630);
            graphics.text(this.font, event.category(), x + 9, rowY + 6, TEXT, true);
            String id = event.id();
            if (id.length() > 45) id = id.substring(0, 42) + "...";
            graphics.text(this.font, id, x + 112, rowY + 6, MUTED, false);
            rowY += 25;
        }
        if (events.isEmpty()) {
            graphics.text(this.font, "No sounds captured yet.", x + 10, rowY + 5, MUTED, false);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
