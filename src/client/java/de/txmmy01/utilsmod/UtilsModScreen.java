package de.txmmy01.utilsmod;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class UtilsModScreen extends Screen {
    private static final int BACKGROUND = 0xF2080808;
    private static final int PANEL = 0xFF151515;
    private static final int PANEL_LIGHT = 0xFF222222;
    private static final int ACCENT = 0xFFFFFFFF;
    private static final int TEXT = 0xFFF4F6FA;
    private static final int MUTED = 0xFFAAAAAA;
    private int blockListScroll;
    private long pageOpenedAt;
    private Page page = Page.DASHBOARD;
    private String selectedModule = "blockglow";
    private EditBox searchBox;
    private String activeSearch = "";

    public UtilsModScreen() {
        super(Component.translatable("screen.utilsmod.title"));
    }

    @Override
    protected void init() {
        pageOpenedAt = System.nanoTime();
        int sidebarWidth = Math.max(112, Math.min(148, width / 5));
        int contentX = sidebarWidth + 14;
        int contentWidth = Math.max(180, width - contentX - 18);
        int buttonX = 18;
        int buttonWidth = sidebarWidth - 30;

        addRenderableWidget(Button.builder(Component.translatable("screen.utilsmod.dashboard"), button -> setPage(Page.DASHBOARD)).bounds(buttonX, 82, buttonWidth, 25).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.utilsmod.modules"), button -> setPage(Page.MODULES)).bounds(buttonX, 116, buttonWidth, 25).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.utilsmod.settings"), button -> setPage(Page.SETTINGS)).bounds(buttonX, 150, buttonWidth, 25).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.utilsmod.close"), button -> onClose()).bounds(width - 104, height - 38, 86, 22).build());

        if (page == Page.MODULES) {
            int cardWidth = Math.max(80, contentWidth - 36);
            int left = contentX + 18;
            addRenderableWidget(Button.builder(Component.literal("BlockGlow  ›"), button -> openModule("blockglow"))
                    .bounds(left, 112, cardWidth, 28).build());
            addRenderableWidget(Button.builder(Component.literal("Visual  ›"), button -> openModule("visual"))
                    .bounds(left, 150, cardWidth, 28).build());
            addRenderableWidget(Button.builder(Component.literal("HUD  ›"), button -> openModule("hud"))
                    .bounds(left, 188, cardWidth, 28).build());
        }

        if (page == Page.MODULE_SETTINGS) {
            addRenderableWidget(Button.builder(Component.literal("‹ Modules"), button -> setPage(Page.MODULES))
                    .bounds(contentX + 18, 100, 92, 23).build());
            int left = contentX + 18;
            int settingsButtonWidth = Math.max(90, Math.min(190, contentWidth - 36));
            int y = 136;
            if (selectedModule.equals("blockglow")) {
                addRenderableWidget(Button.builder(moduleState("BlockGlow", BlockGlowConfig.isEnabled()), button -> {
                    BlockGlowConfig.setEnabled(!BlockGlowConfig.isEnabled()); rebuildWidgets();
                }).bounds(left, y, settingsButtonWidth, 24).build());
                y += 30;
                addRenderableWidget(Button.builder(Component.literal("Render distance: " + BlockGlowConfig.getRenderDistance()), button -> {
                    BlockGlowConfig.cycleRenderDistance(); rebuildWidgets();
                }).bounds(left, y, settingsButtonWidth, 24).build());
                y += 30;
                addRenderableWidget(Button.builder(Component.literal("Intensity: " + BlockGlowConfig.getIntensity()), button -> {
                    BlockGlowConfig.cycleIntensity(); rebuildWidgets();
                }).bounds(left, y, settingsButtonWidth, 24).build());
            } else if (selectedModule.equals("visual")) {
                addRenderableWidget(Button.builder(moduleState("Fullbright", VisualModuleConfig.isFullbrightEnabled()), button -> {
                    VisualModuleConfig.setFullbrightEnabled(!VisualModuleConfig.isFullbrightEnabled()); rebuildWidgets();
                }).bounds(left, y, settingsButtonWidth, 24).build());
                y += 30;
                addRenderableWidget(Button.builder(moduleState("Clear lava", VisualModuleConfig.isClearLavaEnabled()), button -> {
                    VisualModuleConfig.setClearLavaEnabled(!VisualModuleConfig.isClearLavaEnabled()); rebuildWidgets();
                }).bounds(left, y, settingsButtonWidth, 24).build());
                y += 30;
                addRenderableWidget(Button.builder(moduleState("Clear water", VisualModuleConfig.isClearWaterEnabled()), button -> {
                    VisualModuleConfig.setClearWaterEnabled(!VisualModuleConfig.isClearWaterEnabled()); rebuildWidgets();
                }).bounds(left, y, settingsButtonWidth, 24).build());
            } else if (selectedModule.equals("hud")) {
                addRenderableWidget(Button.builder(moduleState("Armor HUD", HudModuleConfig.isArmorHudEnabled()), button -> {
                    HudModuleConfig.setArmorHudEnabled(!HudModuleConfig.isArmorHudEnabled()); rebuildWidgets();
                }).bounds(left, y, settingsButtonWidth, 24).build());
                y += 30;
                addRenderableWidget(Button.builder(moduleState("Potion bars", HudModuleConfig.isPotionBarsEnabled()), button -> {
                    HudModuleConfig.setPotionBarsEnabled(!HudModuleConfig.isPotionBarsEnabled()); rebuildWidgets();
                }).bounds(left, y, settingsButtonWidth, 24).build());
                y += 30;
                addRenderableWidget(Button.builder(moduleState("FPS HUD", HudModuleConfig.isFpsHudEnabled()), button -> {
                    HudModuleConfig.setFpsHudEnabled(!HudModuleConfig.isFpsHudEnabled()); rebuildWidgets();
                }).bounds(left, y, settingsButtonWidth, 24).build());
                y += 30;
                addRenderableWidget(Button.builder(moduleState("CPS counter", HudModuleConfig.isCpsEnabled()), button -> {
                    HudModuleConfig.setCpsEnabled(!HudModuleConfig.isCpsEnabled()); rebuildWidgets();
                }).bounds(left, y, settingsButtonWidth, 24).build());
                y += 30;
                addRenderableWidget(Button.builder(moduleState("Coordinates", HudModuleConfig.isCoordinatesEnabled()), button -> {
                    HudModuleConfig.setCoordinatesEnabled(!HudModuleConfig.isCoordinatesEnabled()); rebuildWidgets();
                }).bounds(left, y, settingsButtonWidth, 24).build());
                y += 30;
                addRenderableWidget(Button.builder(moduleState("Direction", HudModuleConfig.isDirectionEnabled()), button -> {
                    HudModuleConfig.setDirectionEnabled(!HudModuleConfig.isDirectionEnabled()); rebuildWidgets();
                }).bounds(left, y, settingsButtonWidth, 24).build());
            }
        }

        if (page == Page.SETTINGS) {
            addRenderableWidget(Button.builder(
                    Component.translatable(UiConfig.isAnimationsEnabled() ? "screen.utilsmod.animations_on" : "screen.utilsmod.animations_off"),
                    button -> {
                        UiConfig.setAnimationsEnabled(!UiConfig.isAnimationsEnabled());
                        pageOpenedAt = System.nanoTime();
                        rebuildWidgets();
                    }
            ).bounds(contentX + 28, 156, 190, 28).build());
        }
    }

    private Component moduleState(String key, boolean enabled) {
        return Component.translatable(key).append(Component.literal(enabled ? "  ON" : "  OFF"));
    }

    private void setPage(Page nextPage) {
        page = nextPage;
        rebuildWidgets();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (page != Page.MODULE_SETTINGS || !selectedModule.equals("blockglow") || mouseY < 252 || verticalAmount == 0) {
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }
        int sidebarWidth = Math.max(112, Math.min(148, width / 5));
        int contentWidth = Math.max(180, width - (sidebarWidth + 14) - 18);
        int columns = contentWidth >= 560 ? 3 : 2;
        int visibleRows = Math.max(1, (height - 316) / 29);
        int matchingBlocks = 0;
        for (String blockId : BlockGlowConfig.DEFAULT_BLOCKS.keySet()) {
            String displayName = blockId.substring("minecraft:".length()).replace('_', ' ');
            if (activeSearch.isEmpty() || displayName.contains(activeSearch) || blockId.contains(activeSearch)) matchingBlocks++;
        }
        int totalRows = (matchingBlocks + columns - 1) / columns;
        int maxScroll = Math.max(0, totalRows - visibleRows);
        int nextScroll = Math.clamp(blockListScroll + (verticalAmount < 0 ? 1 : -1), 0, maxScroll);
        if (nextScroll != blockListScroll) {
            blockListScroll = nextScroll;
            rebuildWidgets();
        }
        return true;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int sidebarWidth = Math.max(112, Math.min(148, width / 5));
        int contentX = sidebarWidth + 14;
        int contentWidth = Math.max(180, width - contentX - 18);
        int panelBottom = height - 52;

        graphics.fill(0, 0, width, height, BACKGROUND);
        graphics.fill(0, 0, sidebarWidth, height, 0xFF11161D);
        graphics.fill(sidebarWidth - 2, 0, sidebarWidth, height, animated(ACCENT));
        graphics.fill(contentX, 48, contentX + contentWidth, panelBottom, animated(PANEL));
        graphics.fill(contentX, 48, contentX + contentWidth, 51, animated(ACCENT));
        graphics.text(font, Component.literal("UTILS MOD"), 18, 24, animated(TEXT));
        graphics.fill(18, 56, sidebarWidth - 18, 57, 0xFF555555);
        graphics.text(font, Component.translatable("screen.utilsmod.version"), 18, 43, animated(MUTED));
        graphics.text(font, Component.translatable("screen.utilsmod.workspace"), contentX + 18, 18, animated(MUTED));
        graphics.text(font, page.title(), contentX + 18, 66, animated(TEXT));

        switch (page) {
            case DASHBOARD -> drawDashboard(graphics, contentX, contentWidth);
            case MODULES -> drawModules(graphics, contentX, contentWidth);
            case MODULE_SETTINGS -> drawModuleSettings(graphics, contentX, contentWidth);
            case SETTINGS -> drawSettings(graphics, contentX, contentWidth);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    private void drawDashboard(GuiGraphicsExtractor graphics, int x, int contentWidth) {
        int activeCount = (BlockGlowConfig.isEnabled() ? 1 : 0)
                + (VisualModuleConfig.isFullbrightEnabled() ? 1 : 0)
                + (VisualModuleConfig.isClearLavaEnabled() ? 1 : 0)
                + (VisualModuleConfig.isClearWaterEnabled() ? 1 : 0)
                + (HudModuleConfig.isArmorHudEnabled() ? 1 : 0)
                + (HudModuleConfig.isPotionBarsEnabled() ? 1 : 0)
                + (HudModuleConfig.isFpsHudEnabled() ? 1 : 0)
                + (HudModuleConfig.isCpsEnabled() ? 1 : 0)
                + (HudModuleConfig.isCoordinatesEnabled() ? 1 : 0)
                + (HudModuleConfig.isDirectionEnabled() ? 1 : 0);
        String activeModules = Integer.toString(activeCount);
        if (contentWidth < 420) {
            drawCard(graphics, x + 18, 108, contentWidth - 36, Component.translatable("screen.utilsmod.active_modules"), activeModules, "10 utilities");
            drawCard(graphics, x + 18, 190, contentWidth - 36, Component.translatable("screen.utilsmod.game_version"), "26.2", "Fabric");
            drawCard(graphics, x + 18, 272, contentWidth - 36, Component.translatable("screen.utilsmod.release"), "2.1.0", " ");
            return;
        }

        int cardY = 112;
        int gap = 10;
        int cardWidth = (contentWidth - 36 - gap * 2) / 3;
        drawCard(graphics, x + 18, cardY, cardWidth, Component.translatable("screen.utilsmod.active_modules"), activeModules, "8 modules");
        drawCard(graphics, x + 18 + cardWidth + gap, cardY, cardWidth, Component.translatable("screen.utilsmod.game_version"), "26.2", "Fabric");
        drawCard(graphics, x + 18 + (cardWidth + gap) * 2, cardY, cardWidth, Component.translatable("screen.utilsmod.release"), "2.1.0", " ");
        graphics.text(font, Component.translatable("screen.utilsmod.welcome"), x + 20, 216, TEXT);
        graphics.text(font, Component.translatable("screen.utilsmod.welcome_description"), x + 20, 236, MUTED);
        graphics.text(font, Component.translatable("screen.utilsmod.next_step"), x + 20, 274, ACCENT);
        graphics.text(font, Component.translatable("screen.utilsmod.next_step_description"), x + 20, 294, MUTED);
    }

    private void drawModules(GuiGraphicsExtractor graphics, int x, int contentWidth) {
        graphics.fill(x + 18, 100, x + contentWidth - 18, 166, animated(PANEL_LIGHT));
        graphics.fill(x + 18, 100, x + 21, 166, animated(ACCENT));
        graphics.text(font, Component.translatable("screen.utilsmod.module_library_description"), x + 32, 84, animated(MUTED));
        if (minecraft.level != null) {
            graphics.text(font, Component.literal("DIMENSION  " + minecraft.level.dimension()), x + contentWidth - 180, 84, animated(MUTED));
        }
    }

    private void drawModuleSettings(GuiGraphicsExtractor graphics, int x, int contentWidth) {
        graphics.fill(x + 18, 100, x + contentWidth - 18, height - 52, animated(PANEL_LIGHT));
        String name = switch (selectedModule) {
            case "blockglow" -> "BlockGlow";
            case "visual" -> "Visual";
            case "hud" -> "HUD & utilities";
            default -> "Module";
        };
        graphics.text(font, Component.literal("/modules/" + selectedModule + "/(setting)"), x + 32, 82, animated(MUTED));
        graphics.text(font, Component.literal(name + " settings"), x + 32, 116, animated(TEXT));
    }

    private void openModule(String module) {
        selectedModule = module;
        setPage(Page.MODULE_SETTINGS);
    }

    private void drawSettings(GuiGraphicsExtractor graphics, int x, int contentWidth) {
        graphics.fill(x + 18, 108, x + contentWidth - 18, 210, animated(PANEL_LIGHT));
        graphics.text(font, Component.translatable("screen.utilsmod.interface_settings"), x + 32, 124, animated(TEXT));
        graphics.text(font, Component.translatable("screen.utilsmod.animations_description"), x + 32, 140, animated(MUTED));
    }

    private void drawCard(GuiGraphicsExtractor graphics, int x, int y, int cardWidth, Component label, String value, String detail) {
        graphics.fill(x, y, x + cardWidth, y + 76, animated(PANEL_LIGHT));
        graphics.fill(x, y, x + cardWidth, y + 2, animated(ACCENT));
        graphics.text(font, label, x + 10, y + 12, animated(MUTED));
        graphics.text(font, Component.literal(value), x + 10, y + 32, animated(TEXT));
        if (!detail.isBlank()) graphics.text(font, Component.literal(detail), x + 10, y + 52, animated(MUTED));
    }

    private int animated(int color) {
        if (!UiConfig.isAnimationsEnabled()) return color;
        float progress = Math.min(1.0f, (System.nanoTime() - pageOpenedAt) / 220_000_000.0f);
        int alpha = (int) (((color >>> 24) & 0xFF) * progress);
        return (color & 0x00FFFFFF) | (alpha << 24);
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
        DASHBOARD("screen.utilsmod.dashboard"),
        MODULES("screen.utilsmod.modules"),
        MODULE_SETTINGS("screen.utilsmod.modules"),
        SETTINGS("screen.utilsmod.settings");

        private final String titleKey;

        Page(String titleKey) {
            this.titleKey = titleKey;
        }

        private Component title() {
            return Component.translatable(titleKey);
        }
    }
}
