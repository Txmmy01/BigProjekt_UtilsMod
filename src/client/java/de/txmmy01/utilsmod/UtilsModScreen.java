package de.txmmy01.utilsmod;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class UtilsModScreen extends Screen {
    private static final int BACKGROUND = 0xFF101010;
    private static final int PANEL = 0xFF1B1B1B;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int MUTED = 0xFFB0B0B0;

    private Page page = Page.DASHBOARD;
    private String selectedModule = "hud";
    private int buttonHeight = 20;
    private int buttonStep = 25;

    public UtilsModScreen() {
        super(Component.literal("UtilsMod"));
    }

    @Override
    protected void init() {
        clearWidgets();

        int rowCount = visibleRowCount();
        int top = 76;
        int closeY = Math.max(0, height - 24);
        int available = Math.max(0, closeY - top - 8);
        buttonHeight = Math.clamp((available - Math.max(0, rowCount - 1) * 2) / Math.max(1, rowCount), 8, 22);
        buttonStep = buttonHeight + 2;

        int buttonWidth = Math.max(1, Math.min(220, width - 32));
        int left = (width - buttonWidth) / 2;

        if (page != Page.DASHBOARD) {
            addRenderableWidget(Button.builder(Component.literal("‹ Back"), button -> {
                page = page == Page.MODULE_SETTINGS ? Page.MODULES : Page.DASHBOARD;
                rebuildWidgets();
            }).bounds(left, 44, buttonWidth, Math.min(20, buttonHeight)).build());
        }

        switch (page) {
            case DASHBOARD -> {
                addButton("Modules", left, top, buttonWidth, buttonHeight, () -> changePage(Page.MODULES));
                addButton("Settings", left, top + buttonStep, buttonWidth, buttonHeight, () -> changePage(Page.SETTINGS));
            }
            case MODULES -> {
                addButton("BlockGlow", left, top, buttonWidth, buttonHeight, () -> openModule("blockglow"));
                addButton("Visual", left, top + buttonStep, buttonWidth, buttonHeight, () -> openModule("visual"));
                addButton("HUD & Utilities", left, top + buttonStep * 2, buttonWidth, buttonHeight, () -> openModule("hud"));
            }
            case MODULE_SETTINGS -> {
                int y = top;
                if (selectedModule.equals("blockglow")) {
                    y = addToggle("BlockGlow", BlockGlowConfig.isEnabled(), left, y, buttonWidth, buttonHeight, () -> BlockGlowConfig.setEnabled(!BlockGlowConfig.isEnabled()));
                    y = addAction("Render distance: " + BlockGlowConfig.getRenderDistance(), left, y, buttonWidth, buttonHeight, BlockGlowConfig::cycleRenderDistance);
                    addAction("Intensity: " + BlockGlowConfig.getIntensity(), left, y, buttonWidth, buttonHeight, BlockGlowConfig::cycleIntensity);
                } else if (selectedModule.equals("visual")) {
                    y = addToggle("Fullbright", VisualModuleConfig.isFullbrightEnabled(), left, y, buttonWidth, buttonHeight, () -> VisualModuleConfig.setFullbrightEnabled(!VisualModuleConfig.isFullbrightEnabled()));
                    y = addToggle("Clear lava", VisualModuleConfig.isClearLavaEnabled(), left, y, buttonWidth, buttonHeight, () -> VisualModuleConfig.setClearLavaEnabled(!VisualModuleConfig.isClearLavaEnabled()));
                    addToggle("Clear water", VisualModuleConfig.isClearWaterEnabled(), left, y, buttonWidth, buttonHeight, () -> VisualModuleConfig.setClearWaterEnabled(!VisualModuleConfig.isClearWaterEnabled()));
                } else {
                    int gap = 6;
                    int columnWidth = Math.max(1, (buttonWidth - gap) / 2);
                    addButton("Armor HUD  " + (HudModuleConfig.isArmorHudEnabled() ? "ON" : "OFF"), left, y, columnWidth, buttonHeight, () -> HudModuleConfig.setArmorHudEnabled(!HudModuleConfig.isArmorHudEnabled()));
                    addButton("Potion bars  " + (HudModuleConfig.isPotionBarsEnabled() ? "ON" : "OFF"), left + columnWidth + gap, y, columnWidth, buttonHeight, () -> HudModuleConfig.setPotionBarsEnabled(!HudModuleConfig.isPotionBarsEnabled()));
                    y += buttonStep;
                    addButton("FPS HUD  " + (HudModuleConfig.isFpsHudEnabled() ? "ON" : "OFF"), left, y, columnWidth, buttonHeight, () -> HudModuleConfig.setFpsHudEnabled(!HudModuleConfig.isFpsHudEnabled()));
                    addButton("CPS  " + (HudModuleConfig.isCpsEnabled() ? "ON" : "OFF"), left + columnWidth + gap, y, columnWidth, buttonHeight, () -> HudModuleConfig.setCpsEnabled(!HudModuleConfig.isCpsEnabled()));
                    y += buttonStep;
                    addButton("Coordinates  " + (HudModuleConfig.isCoordinatesEnabled() ? "ON" : "OFF"), left, y, columnWidth, buttonHeight, () -> HudModuleConfig.setCoordinatesEnabled(!HudModuleConfig.isCoordinatesEnabled()));
                    addButton("Direction  " + (HudModuleConfig.isDirectionEnabled() ? "ON" : "OFF"), left + columnWidth + gap, y, columnWidth, buttonHeight, () -> HudModuleConfig.setDirectionEnabled(!HudModuleConfig.isDirectionEnabled()));
                }
            }
            case SETTINGS -> addToggle("GUI animations", UiConfig.isAnimationsEnabled(), left, top, buttonWidth, buttonHeight, () -> UiConfig.setAnimationsEnabled(!UiConfig.isAnimationsEnabled()));
        }

        addRenderableWidget(Button.builder(Component.literal("Close"), button -> onClose())
                .bounds(left, closeY, buttonWidth, Math.min(20, buttonHeight)).build());
    }

    private int visibleRowCount() {
        return switch (page) {
            case DASHBOARD -> 2;
            case MODULES -> 3;
            case MODULE_SETTINGS -> 3;
            case SETTINGS -> 1;
        };
    }

    private void addButton(String label, int x, int y, int w, int h, Runnable action) {
        addRenderableWidget(Button.builder(Component.literal(label), button -> {
            action.run();
            rebuildWidgets();
        }).bounds(x, y, w, h).build());
    }

    private int addToggle(String label, boolean enabled, int x, int y, int w, int h, Runnable action) {
        addButton(label + (enabled ? "   ON" : "   OFF"), x, y, w, h, action);
        return y + buttonStep;
    }

    private int addAction(String label, int x, int y, int w, int h, Runnable action) {
        addButton(label, x, y, w, h, action);
        return y + buttonStep;
    }

    private void changePage(Page next) {
        page = next;
        rebuildWidgets();
    }

    private void openModule(String module) {
        selectedModule = module;
        changePage(Page.MODULE_SETTINGS);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, BACKGROUND);
        int panelWidth = Math.max(1, Math.min(420, width - 24));
        int panelLeft = (width - panelWidth) / 2;
        graphics.fill(panelLeft, 16, panelLeft + panelWidth, Math.max(17, height - 16), PANEL);
        graphics.text(font, Component.literal("UTILS MOD"), panelLeft + 16, 22, TEXT);
        String heading = switch (page) {
            case DASHBOARD -> "Dashboard";
            case MODULES -> "Modules";
            case MODULE_SETTINGS -> selectedModule.toUpperCase(java.util.Locale.ROOT) + " SETTINGS";
            case SETTINGS -> "Settings";
        };
        graphics.text(font, Component.literal(heading), panelLeft + 16, 66, TEXT);
        graphics.text(font, Component.literal("Minecraft 26.2  •  Fabric"), panelLeft + 16, height - 28, MUTED);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.gui.setScreen(null);
    }

    private enum Page {
        DASHBOARD,
        MODULES,
        MODULE_SETTINGS,
        SETTINGS
    }
}
