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
 * Main DonutSpy control panel. All information shown here is client-side.
 */
public final class DonutSpyScreen extends Screen {
    private static final String[] TABS = {"Overview", "Event Log", "Settings", "World Analysis"};
    private final int selectedTab;

    public DonutSpyScreen() {
        this(0);
    }

    private DonutSpyScreen(int selectedTab) {
        super(Component.literal("DonutSpy"));
        this.selectedTab = selectedTab;
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(760, this.width - 32);
        int left = (this.width - panelWidth) / 2;
        int tabY = Math.max(24, (this.height - 430) / 2 + 58);
        int tabWidth = (panelWidth - 32) / TABS.length;

        for (int i = 0; i < TABS.length; i++) {
            final int tab = i;
            this.addRenderableWidget(Button.builder(
                    Component.literal((i == selectedTab ? "• " : "") + TABS[i]),
                    button -> this.minecraft.setScreen(new DonutSpyScreen(tab))
            ).bounds(left + 16 + i * tabWidth, tabY, tabWidth - 5, 22).build());
        }

        int contentY = tabY + 42;
        if (selectedTab == 0) {
            addActionButton("Open Event Log", left + 18, contentY + 180, 150,
                    () -> this.minecraft.setScreen(new DonutSpyScreen(1)));
            addActionButton("Toggle Capture", left + 180, contentY + 180, 150,
                    () -> toggle("capture"));
            addActionButton("Clear History", left + 342, contentY + 180, 130,
                    SoundHistory::clear);
        } else if (selectedTab == 1) {
            addActionButton("Clear Event History", left + 18, this.height - 58, 160,
                    SoundHistory::clear);
        } else if (selectedTab == 2) {
            addActionButton("Capture: " + on(DonutSpyConfig.current().captureEnabled),
                    left + 18, contentY, 210, () -> toggle("capture"));
            addActionButton("HUD: " + on(DonutSpyConfig.current().hudEnabled),
                    left + 240, contentY, 210, () -> toggle("hud"));
            addActionButton("Sound markers: " + on(DonutSpyConfig.current().soundOutlineEnabled),
                    left + 18, contentY + 34, 210, () -> toggle("markers"));
            addActionButton("Ore overlay: " + on(DonutSpyConfig.current().oreOverlayEnabled),
                    left + 240, contentY + 34, 210, () -> toggle("ore"));
            addActionButton("Movement: " + on(DonutSpyConfig.current().movementSounds),
                    left + 18, contentY + 88, 150, () -> toggle("movement"));
            addActionButton("Combat: " + on(DonutSpyConfig.current().combatSounds),
                    left + 180, contentY + 88, 150, () -> toggle("combat"));
            addActionButton("Transport: " + on(DonutSpyConfig.current().transportSounds),
                    left + 342, contentY + 88, 150, () -> toggle("transport"));
            addActionButton("Explosions: " + on(DonutSpyConfig.current().explosionSounds),
                    left + 18, contentY + 122, 150, () -> toggle("explosions"));
            addActionButton("Farming: " + on(DonutSpyConfig.current().farmingSounds),
                    left + 180, contentY + 122, 150, () -> toggle("farming"));
            addActionButton("Sculk: " + on(DonutSpyConfig.current().sculkSounds),
                    left + 342, contentY + 122, 150, () -> toggle("sculk"));
            addActionButton("Natural: " + on(DonutSpyConfig.current().naturalSounds),
                    left + 18, contentY + 156, 150, () -> toggle("natural"));
            addActionButton("Other: " + on(DonutSpyConfig.current().customSounds),
                    left + 180, contentY + 156, 150, () -> toggle("custom"));
        }

        this.addRenderableWidget(Button.builder(Component.literal("Close"),
                button -> this.minecraft.setScreen(null))
                .bounds(this.width / 2 - 45, this.height - 30, 90, 20).build());
    }

    private void addActionButton(String label, int x, int y, int width, Runnable action) {
        this.addRenderableWidget(Button.builder(Component.literal(label), button -> {
            action.run();
            if (selectedTab == 2) {
                this.minecraft.setScreen(new DonutSpyScreen(2));
            }
        }).bounds(x, y, width, 22).build());
    }

    private static void toggle(String option) {
        DonutSpyConfig c = DonutSpyConfig.current();
        switch (option) {
            case "capture" -> c.captureEnabled = !c.captureEnabled;
            case "hud" -> c.hudEnabled = !c.hudEnabled;
            case "markers" -> c.soundOutlineEnabled = !c.soundOutlineEnabled;
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

    private static String on(boolean value) {
        return value ? "ON" : "OFF";
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int panelWidth = Math.min(760, this.width - 32);
        int panelHeight = Math.min(500, this.height - 24);
        int left = (this.width - panelWidth) / 2;
        int top = (this.height - panelHeight) / 2;

        graphics.fill(0, 0, this.width, this.height, 0xB9000000);
        graphics.fill(left - 1, top - 1, left + panelWidth + 1, top + panelHeight + 1, 0xFF384454);
        graphics.fill(left, top, left + panelWidth, top + panelHeight, 0xF0101722);
        graphics.fill(left, top, left + panelWidth, top + 48, 0xFF182535);
        graphics.fill(left, top + 47, left + panelWidth, top + 49, 0xFF52677F);

        graphics.fill(left + 18, top + 12, left + 44, top + 38, 0xFF405D79);
        graphics.centeredText(this.font, "D", left + 31, top + 20, 0xFFFFFFFF);
        graphics.text(this.font, "DONUTSPY", left + 54, top + 12, 0xFFFFFFFF, true);
        graphics.text(this.font, "CLIENT MODULE DESK  /  0.5.0", left + 54, top + 27, 0xFF9EADBF, false);

        String status = DonutSpyConfig.current().captureEnabled ? "● CAPTURING" : "○ PAUSED";
        graphics.text(this.font, status, left + panelWidth - 112, top + 19,
                DonutSpyConfig.current().captureEnabled ? 0xFFFF6B6B : 0xFFFFC66D, true);

        int contentY = top + 126;
        if (selectedTab == 0) {
            drawOverview(graphics, left, contentY, panelWidth);
        } else if (selectedTab == 1) {
            drawEvents(graphics, left, contentY, panelWidth);
        } else if (selectedTab == 2) {
            graphics.text(this.font, "CONFIGURATION", left + 20, contentY - 8, 0xFFEAF0F8, true);
            graphics.text(this.font, "Use the buttons below to change settings. Changes save automatically.",
                    left + 20, contentY + 10, 0xFF9EADBF, false);
        } else {
            graphics.text(this.font, "WORLD ANALYSIS", left + 20, contentY - 8, 0xFFEAF0F8, true);
            if (Minecraft.getInstance().level == null) {
                graphics.text(this.font, "Enter a world to inspect local data.", left + 20, contentY + 18, 0xFFB9C6D6, false);
            } else if (Minecraft.getInstance().getSingleplayerServer() == null) {
                graphics.text(this.font, "Available only in integrated singleplayer.", left + 20, contentY + 18, 0xFFFFC66D, false);
            } else {
                graphics.text(this.font, "Singleplayer world detected. Chunk analysis is not ported yet.",
                        left + 20, contentY + 18, 0xFFB9C6D6, false);
            }
        }

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    private void drawOverview(GuiGraphicsExtractor graphics, int left, int y, int panelWidth) {
        DonutSpyConfig c = DonutSpyConfig.current();
        int cardWidth = (panelWidth - 56) / 3;
        drawCard(graphics, left + 18, y, cardWidth, "SOUND EVENTS", String.valueOf(SoundHistory.size()));
        drawCard(graphics, left + 28 + cardWidth, y, cardWidth,
                "SOUND MARKERS", c.soundOutlineEnabled ? "ENABLED" : "DISABLED");
        drawCard(graphics, left + 38 + cardWidth * 2, y, cardWidth,
                "WORLD ANALYSIS", "SINGLEPLAYER");
        graphics.text(this.font, "RECENT SOUND ACTIVITY", left + 20, y + 82, 0xFFEAF0F8, true);

        List<SoundEvent> events = SoundHistory.snapshot();
        int rowY = y + 104;
        for (int i = 0; i < Math.min(6, events.size()); i++) {
            SoundEvent event = events.get(i);
            graphics.fill(left + 18, rowY - 3, left + panelWidth - 18, rowY + 19, 0xFF1B2735);
            graphics.text(this.font, event.category(), left + 28, rowY + 3, categoryColor(event.category()), true);
            String name = event.id();
            if (name.length() > 48) name = name.substring(0, 45) + "...";
            graphics.text(this.font, name, left + 116, rowY + 3, 0xFFD5DFEA, false);
            rowY += 26;
        }
        if (events.isEmpty()) {
            graphics.text(this.font, "No sounds captured yet. Walk around or trigger a nearby sound.",
                    left + 28, rowY + 4, 0xFF9EADBF, false);
        }
    }

    private void drawEvents(GuiGraphicsExtractor graphics, int left, int y, int panelWidth) {
        graphics.text(this.font, "EVENT HISTORY  /  NEWEST FIRST", left + 20, y - 8, 0xFFEAF0F8, true);
        List<SoundEvent> events = SoundHistory.snapshot();
        int rowY = y + 18;
        for (int i = 0; i < Math.min(11, events.size()); i++) {
            SoundEvent event = events.get(i);
            graphics.fill(left + 18, rowY - 3, left + panelWidth - 18, rowY + 22, 0xFF1B2735);
            graphics.text(this.font, event.category(), left + 28, rowY + 4, categoryColor(event.category()), true);
            String id = event.id();
            if (id.length() > 34) id = id.substring(0, 31) + "...";
            graphics.text(this.font, id, left + 116, rowY + 4, 0xFFD5DFEA, false);
            String coords = String.format(java.util.Locale.ROOT, "%.0f %.0f %.0f", event.x(), event.y(), event.z());
            graphics.text(this.font, coords, left + panelWidth - 112, rowY + 4, 0xFF9EADBF, false);
            rowY += 27;
        }
        if (events.isEmpty()) {
            graphics.text(this.font, "No events captured yet.", left + 24, rowY + 8, 0xFF9EADBF, false);
        }
    }

    private void drawCard(GuiGraphicsExtractor graphics, int x, int y, int width, String title, String value) {
        graphics.fill(x, y, x + width, y + 62, 0xFF1B2735);
        graphics.text(this.font, title, x + 10, y + 10, 0xFF9EADBF, false);
        graphics.text(this.font, value, x + 10, y + 32, 0xFFEAF0F8, true);
    }

    private static int categoryColor(String category) {
        return switch (category) {
            case "MOVEMENT" -> 0xFFFF9A55;
            case "COMBAT" -> 0xFFFF626D;
            case "TRANSPORT" -> 0xFF72B9FF;
            case "EXPLOSIONS" -> 0xFFFFC15C;
            case "FARMING" -> 0xFF8BD17C;
            case "SCULK" -> 0xFFB28CFF;
            case "NATURAL" -> 0xFF62C9B2;
            default -> 0xFFC3CAD5;
        };
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
