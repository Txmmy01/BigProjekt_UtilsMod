package de.txmmy01.utilsmod;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class UtilsModScreen extends Screen {
    private static final int BACKGROUND = 0xF20D1015;
    private static final int PANEL = 0xFF171C24;
    private static final int PANEL_LIGHT = 0xFF202733;
    private static final int ACCENT = 0xFFFF8A32;
    private static final int TEXT = 0xFFF4F6FA;
    private static final int MUTED = 0xFF9BA6B5;
    private static boolean animationsEnabled = true;
    private long pageOpenedAt;
    private Page page = Page.DASHBOARD;
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
            addRenderableWidget(Button.builder(
                    Component.literal("BlockGlow: " + (BlockGlowConfig.isEnabled() ? "ON" : "OFF")),
                    button -> {
                        BlockGlowConfig.setEnabled(!BlockGlowConfig.isEnabled());
                        rebuildWidgets();
                    }
            ).bounds(contentX + 18, 108, 126, 24).build());
            addRenderableWidget(Button.builder(
                    Component.literal("Distance: " + BlockGlowConfig.getRenderDistance()),
                    button -> {
                        BlockGlowConfig.cycleRenderDistance();
                        rebuildWidgets();
                    }
            ).bounds(contentX + 152, 108, 112, 24).build());
            addRenderableWidget(Button.builder(
                    Component.literal("Intensity: " + BlockGlowConfig.getIntensity()),
                    button -> {
                        BlockGlowConfig.cycleIntensity();
                        rebuildWidgets();
                    }
            ).bounds(contentX + 272, 108, 112, 24).build());

            int moduleGap = 6;
            int moduleWidth = Math.max(54, (contentWidth - 36 - moduleGap * 2) / 3);
            addRenderableWidget(Button.builder(
                    moduleState("screen.utilsmod.fullbright", VisualModuleConfig.isFullbrightEnabled()),
                    button -> {
                        boolean enabled = !VisualModuleConfig.isFullbrightEnabled();
                        VisualModuleConfig.setFullbrightEnabled(enabled);
                        rebuildWidgets();
                    }
            ).bounds(contentX + 18, 140, moduleWidth, 24).build());
            addRenderableWidget(Button.builder(
                    moduleState("screen.utilsmod.clear_lava", VisualModuleConfig.isClearLavaEnabled()),
                    button -> {
                        VisualModuleConfig.setClearLavaEnabled(!VisualModuleConfig.isClearLavaEnabled());
                        rebuildWidgets();
                    }
            ).bounds(contentX + 18 + moduleWidth + moduleGap, 140, moduleWidth, 24).build());
            addRenderableWidget(Button.builder(
                    moduleState("screen.utilsmod.clear_water", VisualModuleConfig.isClearWaterEnabled()),
                    button -> {
                        VisualModuleConfig.setClearWaterEnabled(!VisualModuleConfig.isClearWaterEnabled());
                        rebuildWidgets();
                    }
            ).bounds(contentX + 18 + (moduleWidth + moduleGap) * 2, 140, moduleWidth, 24).build());

            int hudButtonWidth = Math.max(72, (contentWidth - 44) / 2);
            addRenderableWidget(Button.builder(moduleState("screen.utilsmod.armor_hud", HudModuleConfig.isArmorHudEnabled()), button -> {
                HudModuleConfig.setArmorHudEnabled(!HudModuleConfig.isArmorHudEnabled());
                rebuildWidgets();
            }).bounds(contentX + 18, 170, hudButtonWidth, 23).build());
            addRenderableWidget(Button.builder(moduleState("screen.utilsmod.potion_bars", HudModuleConfig.isPotionBarsEnabled()), button -> {
                HudModuleConfig.setPotionBarsEnabled(!HudModuleConfig.isPotionBarsEnabled());
                rebuildWidgets();
            }).bounds(contentX + 26 + hudButtonWidth, 170, hudButtonWidth, 23).build());
            addRenderableWidget(Button.builder(moduleState("screen.utilsmod.fps_hud", HudModuleConfig.isFpsHudEnabled()), button -> {
                HudModuleConfig.setFpsHudEnabled(!HudModuleConfig.isFpsHudEnabled());
                rebuildWidgets();
            }).bounds(contentX + 18, 197, hudButtonWidth, 23).build());
            addRenderableWidget(Button.builder(moduleState("screen.utilsmod.cps", HudModuleConfig.isCpsEnabled()), button -> {
                HudModuleConfig.setCpsEnabled(!HudModuleConfig.isCpsEnabled());
                rebuildWidgets();
            }).bounds(contentX + 26 + hudButtonWidth, 197, hudButtonWidth, 23).build());

            searchBox = new EditBox(font, contentX + 18, 228, Math.max(80, contentWidth - 126), 24, Component.translatable("screen.utilsmod.search"));
            searchBox.setHint(Component.translatable("screen.utilsmod.search_hint"));
            searchBox.setValue(activeSearch);
            addRenderableWidget(searchBox);
            addRenderableWidget(Button.builder(Component.translatable("screen.utilsmod.apply"), button -> {
                activeSearch = searchBox.getValue().trim().toLowerCase(Locale.ROOT);
                rebuildWidgets();
            }).bounds(contentX + contentWidth - 98, 228, 80, 24).build());

            int index = 0;
            int columns = contentWidth >= 560 ? 3 : 2;
            int itemWidth = Math.max(68, (contentWidth - 36 - (columns - 1) * 8) / columns);
            for (String blockId : BlockGlowConfig.DEFAULT_BLOCKS.keySet()) {
                String displayName = blockId.substring("minecraft:".length()).replace('_', ' ');
                if (!activeSearch.isEmpty() && !displayName.contains(activeSearch) && !blockId.contains(activeSearch)) continue;
                int column = index % columns;
                int row = index / columns;
                int x = contentX + 18 + column * (itemWidth + 8);
                int y = 264 + row * 29;
                String dimension = minecraft.level == null ? "minecraft:overworld" : minecraft.level.dimension().toString();
                boolean selected = BlockGlowConfig.isBlockEnabled(dimension, blockId);
                int color = BlockGlowConfig.getBlockColor(blockId);
                String label = (selected ? "ON  " : "OFF ") + displayName;
                addRenderableWidget(Button.builder(Component.literal(label), button -> {
                    BlockGlowConfig.toggleBlock(dimension, blockId);
                    rebuildWidgets();
                }).bounds(x, y, itemWidth, 24).build());
                addRenderableWidget(Button.builder(Component.literal("●").withStyle(style -> style.withColor(color)), button -> {
                    BlockGlowConfig.cycleBlockColor(blockId);
                    rebuildWidgets();
                }).bounds(x + itemWidth - 23, y, 20, 24).build());
                index++;
            }
        }

        if (page == Page.SETTINGS) {
            addRenderableWidget(Button.builder(
                    Component.translatable(animationsEnabled ? "screen.utilsmod.animations_on" : "screen.utilsmod.animations_off"),
                    button -> {
                        animationsEnabled = !animationsEnabled;
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
        graphics.text(font, Component.literal("UTILS"), 18, 24, animated(TEXT));
        graphics.text(font, Component.literal("MOD"), 58, 24, animated(ACCENT));
        graphics.text(font, Component.translatable("screen.utilsmod.version"), 18, 43, animated(MUTED));
        graphics.text(font, Component.translatable("screen.utilsmod.workspace"), contentX + 18, 18, animated(MUTED));
        graphics.text(font, page.title(), contentX + 18, 66, animated(TEXT));

        switch (page) {
            case DASHBOARD -> drawDashboard(graphics, contentX, contentWidth);
            case MODULES -> drawModules(graphics, contentX, contentWidth);
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
                + (HudModuleConfig.isCpsEnabled() ? 1 : 0);
        String activeModules = Integer.toString(activeCount);
        if (contentWidth < 420) {
            drawCard(graphics, x + 18, 108, contentWidth - 36, Component.translatable("screen.utilsmod.active_modules"), activeModules, "8 modules");
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
        if (!animationsEnabled) return color;
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
