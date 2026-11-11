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
 * DonutSpy's module-desk interface. All displayed data is client-side.
 */
public final class DonutSpyScreen extends Screen {
    private static final String[] CATEGORIES = {
            "All Modules", "Visual", "Alerts", "Tracking", "Favorites"
    };

    private static final String[] MODULES = {
            "Sound Detector", "Sound Markers", "HUD", "Ore Overlay",
            "Event History", "World Analysis"
    };

    private static final String[] DESCRIPTIONS = {
            "Detect and classify nearby world sounds.",
            "Display temporary markers for captured sounds.",
            "Show recording status and recent activity.",
            "Local ore analysis for integrated singleplayer.",
            "Review recently captured sound events.",
            "Inspect loaded chunks in your own singleplayer world."
    };

    private String category;
    private String selectedModule;

    public DonutSpyScreen() {
        this("All Modules", "Sound Detector");
    }

    private DonutSpyScreen(String category, String selectedModule) {
        super(Component.literal("DonutSpy"));
        this.category = category;
        this.selectedModule = selectedModule;
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(790, this.width - 28);
        int panelHeight = Math.min(450, this.height - 24);
        int left = (this.width - panelWidth) / 2;
        int top = (this.height - panelHeight) / 2;

        // Header controls
        addButton("Modules", left + panelWidth - 178, top + 13, 66, 20,
                () -> open("All Modules", selectedModule));
        addButton("HUD", left + panelWidth - 106, top + 13, 42, 20,
                () -> open("Visual", "HUD"));
        addButton("×", left + panelWidth - 34, top + 13, 22, 20,
                () -> this.minecraft.gui.setScreen(null));

        // Left workspace navigation
        String[] nav = {"All Modules", "Visual", "Alerts", "Tracking", "Favorites"};
        for (int i = 0; i < nav.length; i++) {
            final String next = nav[i];
            addButton((category.equals(next) ? "›  " : "   ") + next,
                    left + 9, top + 93 + i * 31, 94, 24,
                    () -> open(next, selectedModule));
        }

        // Center module cards
        int centerX = left + 116;
        int centerWidth = Math.max(0, panelWidth - 326);
        int cardY = top + 105;
        String[] visible = visibleModules();
        for (int i = 0; i < visible.length; i++) {
            final String module = visible[i];
            int y = cardY + i * 61;
            addButton(module + (selectedModule.equals(module) ? "   •" : ""),
                    centerX + 7, y, centerWidth - 14, 42,
                    () -> open(category, module));
        }

        // Right-side module settings
        int settingsX = left + panelWidth - 198;
        int settingsY = top + 105;
        if ("Sound Detector".equals(selectedModule)) {
            addToggle("Capture sounds", DonutSpyConfig.current().captureEnabled,
                    settingsX + 8, settingsY + 78, 170, "capture");
            addToggle("Movement", DonutSpyConfig.current().movementSounds,
                    settingsX + 8, settingsY + 112, 170, "movement");
            addToggle("Combat", DonutSpyConfig.current().combatSounds,
                    settingsX + 8, settingsY + 140, 170, "combat");
            addToggle("Transport", DonutSpyConfig.current().transportSounds,
                    settingsX + 8, settingsY + 168, 170, "transport");
            addToggle("Explosions", DonutSpyConfig.current().explosionSounds,
                    settingsX + 8, settingsY + 196, 170, "explosions");
            addToggle("Farming", DonutSpyConfig.current().farmingSounds,
                    settingsX + 8, settingsY + 224, 170, "farming");
            addToggle("Sculk", DonutSpyConfig.current().sculkSounds,
                    settingsX + 8, settingsY + 252, 170, "sculk");
            addToggle("Natural", DonutSpyConfig.current().naturalSounds,
                    settingsX + 8, settingsY + 280, 170, "natural");
            addToggle("Custom", DonutSpyConfig.current().customSounds,
                    settingsX + 8, settingsY + 308, 170, "custom");
        } else if ("Sound Markers".equals(selectedModule)) {
            addToggle("Sound markers", DonutSpyConfig.current().soundOutlineEnabled,
                    settingsX + 8, settingsY + 78, 170, "markers");
            addInfoButton("Marker lifetime: " + DonutSpyConfig.current().soundMarkerLifetimeMs / 1000.0 + "s",
                    settingsX + 8, settingsY + 112, 170);
        } else if ("HUD".equals(selectedModule)) {
            addToggle("Enable HUD", DonutSpyConfig.current().hudEnabled,
                    settingsX + 8, settingsY + 78, 170, "hud");
            addToggle("Show coordinates", DonutSpyConfig.current().showCoordinates,
                    settingsX + 8, settingsY + 112, 170, "coordinates");
        } else if ("Ore Overlay".equals(selectedModule)) {
            addToggle("Ore overlay", DonutSpyConfig.current().oreOverlayEnabled,
                    settingsX + 8, settingsY + 78, 170, "ore");
            addInfoButton("Singleplayer only", settingsX + 8, settingsY + 112, 170);
        } else if ("Event History".equals(selectedModule)) {
            addButton("Clear event history", settingsX + 8, settingsY + 78, 170, 22,
                    SoundHistory::clear);
        }
    }

    private void addButton(String label, int x, int y, int width, int height, Runnable action) {
        // Never register a widget outside the actual GUI viewport. Minecraft 26.3
        // clips some GUI draws, and an off-screen widget can produce a zero-height scissor.
        if (width <= 0 || height <= 0 || x < 0 || y < 0
                || x + width > this.width || y + height > this.height) {
            return;
        }

        this.addRenderableWidget(Button.builder(Component.literal(label),
                button -> action.run()).bounds(x, y, width, height).build());
    }

    private void addToggle(String label, boolean enabled, int x, int y, int width, String option) {
        addButton(label + (enabled ? "     ON" : "     OFF"), x, y, width, 21, () -> {
            toggle(option);
            open(category, selectedModule);
        });
    }

    private void addInfoButton(String label, int x, int y, int width) {
        addButton(label, x, y, width, 21, () -> {});
    }

    private void open(String nextCategory, String nextModule) {
        this.minecraft.gui.setScreen(new DonutSpyScreen(nextCategory, nextModule));
    }

    private String[] visibleModules() {
        return switch (category) {
            case "Visual" -> new String[]{"HUD", "Sound Markers", "Ore Overlay"};
            case "Alerts" -> new String[]{"Sound Detector"};
            case "Tracking" -> new String[]{"Event History", "World Analysis"};
            case "Favorites" -> new String[]{"Sound Detector", "HUD", "Sound Markers"};
            default -> MODULES;
        };
    }

    private static void toggle(String option) {
        DonutSpyConfig c = DonutSpyConfig.current();
        switch (option) {
            case "capture" -> c.captureEnabled = !c.captureEnabled;
            case "hud" -> c.hudEnabled = !c.hudEnabled;
            case "markers" -> c.soundOutlineEnabled = !c.soundOutlineEnabled;
            case "ore" -> c.oreOverlayEnabled = !c.oreOverlayEnabled;
            case "coordinates" -> c.showCoordinates = !c.showCoordinates;
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
        int panelWidth = Math.min(790, this.width - 28);
        int panelHeight = Math.min(450, this.height - 24);
        int left = (this.width - panelWidth) / 2;
        int top = (this.height - panelHeight) / 2;

        // Dim the game world behind the floating module desk.
        graphics.fill(0, 0, this.width, this.height, 0x99000000);

        // Main shell and three-column workspace.
        graphics.fill(left - 2, top - 2, left + panelWidth + 2, top + panelHeight + 2, 0xFF070A10);
        graphics.fill(left, top, left + panelWidth, top + panelHeight, 0xF0181B22);
        graphics.fill(left, top, left + panelWidth, top + 43, 0xFF20242D);
        graphics.fill(left + 1, top + 42, left + panelWidth - 1, top + 44, 0xFF414957);

        int sidebarRight = left + 108;
        int settingsLeft = left + panelWidth - 198;
        graphics.fill(left + 1, top + 44, sidebarRight, top + panelHeight - 1, 0xFF141820);
        graphics.fill(settingsLeft, top + 44, left + panelWidth - 1, top + panelHeight - 1, 0xFF141820);
        graphics.fill(sidebarRight, top + 44, sidebarRight + 1, top + panelHeight - 1, 0xFF303744);
        graphics.fill(settingsLeft - 1, top + 44, settingsLeft, top + panelHeight - 1, 0xFF303744);

        // Brand and header status
        graphics.fill(left + 13, top + 10, left + 31, top + 28, 0xFF536B4B);
        graphics.text(this.font, "D", left + 19, top + 15, 0xFFFFFFFF, true);
        graphics.text(this.font, "DONUT", left + 39, top + 8, 0xFFF1F2E8, true);
        graphics.text(this.font, "MODULE CONTROL", left + 39, top + 21, 0xFFAAB3C0, false);
        String status = DonutSpyConfig.current().captureEnabled ? "● CAPTURING" : "○ PAUSED";
        graphics.text(this.font, status, left + 135, top + 16,
                DonutSpyConfig.current().captureEnabled ? 0xFFE4C96A : 0xFF9CA6B5, true);

        // Column headings and selected module description.
        graphics.text(this.font, "WORKSPACE", left + 13, top + 58, 0xFF9BBBD0, true);
        graphics.text(this.font, "YOUR MODULE DESK", left + 124, top + 57, 0xFFD8DE91, true);
        graphics.text(this.font, selectedModule.toUpperCase(), left + 124, top + 72, 0xFFF1F2F5, true);
        graphics.text(this.font, "Select a module to configure its options.",
                left + 124, top + 86, 0xFFAAB3C0, false);

        int settingsX = left + panelWidth - 188;
        graphics.text(this.font, "MODULE SETTINGS", settingsX, top + 58, 0xFFB8C1CF, true);
        graphics.text(this.font, selectedModule, settingsX, top + 78, 0xFFF0F1F4, true);
        graphics.text(this.font, descriptionFor(selectedModule), settingsX, top + 94,
                0xFFAAB3C0, false);

        // Module cards behind their clickable controls.
        String[] visible = visibleModules();
        int centerX = left + 123;
        int centerWidth = Math.max(0, panelWidth - 326);
        int cardY = top + 105;
        for (int i = 0; i < visible.length; i++) {
            String module = visible[i];
            int y = cardY + i * 61;
            boolean selected = selectedModule.equals(module);
            if (centerWidth >= 100 && y + 45 < top + panelHeight - 30) {
                graphics.fill(centerX + 4, y - 2, centerX + centerWidth - 4, y + 45,
                        selected ? 0xFF343A31 : 0xFF202630);
                if (selected) {
                    graphics.outline(centerX + 4, y - 2, centerWidth - 8, 47, 0xFF9B9B63);
                }
                graphics.text(this.font, module, centerX + 15, y + 7, 0xFFE7EAF0, true);
                graphics.text(this.font, descriptionFor(module), centerX + 15, y + 23,
                        0xFFAAB3C0, false);
                graphics.text(this.font, isEnabled(module) ? "ACTIVE" : "INACTIVE",
                        centerX + centerWidth - 72, y + 15,
                        isEnabled(module) ? 0xFFD9D58A : 0xFF929BA8, true);
            }
        }

        // Settings panel separators and small footer.
        graphics.fill(settingsX, top + 120, left + panelWidth - 14, top + 121, 0xFF303744);
        graphics.fill(left + 12, top + panelHeight - 28, left + panelWidth - 12,
                top + panelHeight - 27, 0xFF303744);
        graphics.text(this.font, "DONUT  /  YOUR WORKSPACE", left + 13,
                top + panelHeight - 18, 0xFF9CA6B5, false);
        graphics.text(this.font, enabledCount() + " modules active",
                left + panelWidth - 112, top + panelHeight - 18, 0xFFB9C2CF, false);

        if ("World Analysis".equals(selectedModule)) {
            if (Minecraft.getInstance().level == null) {
                graphics.text(this.font, "Enter a world to inspect local data.",
                        centerX + 15, top + 180, 0xFFB9C6D6, false);
            } else if (Minecraft.getInstance().getSingleplayerServer() == null) {
                graphics.text(this.font, "Available only in integrated singleplayer.",
                        centerX + 15, top + 180, 0xFFE4C96A, false);
            } else {
                graphics.text(this.font, "Singleplayer world detected.",
                        centerX + 15, top + 180, 0xFFB9C6D6, false);
                graphics.text(this.font, "Chunk analysis is not ported yet.",
                        centerX + 15, top + 196, 0xFFB9C6D6, false);
            }
        } else if ("Event History".equals(selectedModule)) {
            drawRecentEvents(graphics, centerX + 12, top + 180, centerWidth - 24);
        }

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    private void drawRecentEvents(GuiGraphicsExtractor graphics, int x, int y, int width) {
        graphics.text(this.font, "RECENT EVENTS  /  NEWEST FIRST", x, y, 0xFFD8DE91, true);
        List<SoundEvent> events = SoundHistory.snapshot();
        int rowY = y + 20;
        for (int i = 0; i < Math.min(7, events.size()); i++) {
            SoundEvent event = events.get(i);
            graphics.fill(x, rowY - 3, x + width, rowY + 18, 0xFF202630);
            graphics.text(this.font, event.category(), x + 8, rowY + 3,
                    categoryColor(event.category()), true);
            String id = event.id();
            if (id.length() > 34) id = id.substring(0, 31) + "...";
            graphics.text(this.font, id, x + 105, rowY + 3, 0xFFD5DFEA, false);
            rowY += 24;
        }
        if (events.isEmpty()) {
            graphics.text(this.font, "No sounds captured yet.", x + 8, rowY + 3, 0xFFAAB3C0, false);
        }
    }

    private String descriptionFor(String module) {
        for (int i = 0; i < MODULES.length; i++) {
            if (MODULES[i].equals(module)) return DESCRIPTIONS[i];
        }
        return "";
    }

    private boolean isEnabled(String module) {
        DonutSpyConfig c = DonutSpyConfig.current();
        return switch (module) {
            case "Sound Detector" -> c.captureEnabled;
            case "Sound Markers" -> c.soundOutlineEnabled;
            case "HUD" -> c.hudEnabled;
            case "Ore Overlay" -> c.oreOverlayEnabled;
            default -> true;
        };
    }

    private int enabledCount() {
        int count = 0;
        for (String module : MODULES) if (isEnabled(module)) count++;
        return count;
    }

    private static int categoryColor(String category) {
        return switch (category) {
            case "MOVEMENT" -> 0xFFE4A06B;
            case "COMBAT" -> 0xFFE47777;
            case "TRANSPORT" -> 0xFF82BCE0;
            case "EXPLOSIONS" -> 0xFFE4C06B;
            case "FARMING" -> 0xFF91C77F;
            case "SCULK" -> 0xFFB28CFF;
            case "NATURAL" -> 0xFF72C5B2;
            default -> 0xFFC3CAD5;
        };
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
