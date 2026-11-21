package de.txmmy01.utilsmod;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class UtilsModScreen extends Screen {
    private static final int BACKGROUND = 0xF20D1015;
    private static final int PANEL = 0xFF171C24;
    private static final int PANEL_LIGHT = 0xFF202733;
    private static final int ACCENT = 0xFFFF8A32;
    private static final int TEXT = 0xFFF4F6FA;
    private static final int MUTED = 0xFF9BA6B5;
    private static boolean animationsEnabled = true;
    private Page page = Page.DASHBOARD;

    public UtilsModScreen() {
        super(Component.translatable("screen.utilsmod.title"));
    }

    @Override
    protected void init() {
        int sidebarWidth = Math.max(112, Math.min(148, width / 5));
        int buttonX = 18;
        int buttonWidth = sidebarWidth - 30;
        int buttonHeight = 25;
        int startY = 82;

        addRenderableWidget(Button.builder(Component.translatable("screen.utilsmod.dashboard"), button -> setPage(Page.DASHBOARD)).bounds(buttonX, startY, buttonWidth, buttonHeight).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.utilsmod.modules"), button -> setPage(Page.MODULES)).bounds(buttonX, startY + 34, buttonWidth, buttonHeight).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.utilsmod.settings"), button -> setPage(Page.SETTINGS)).bounds(buttonX, startY + 68, buttonWidth, buttonHeight).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.utilsmod.close"), button -> onClose()).bounds(width - 104, height - 38, 86, 22).build());

        if (page == Page.SETTINGS) {
            addRenderableWidget(Button.builder(
                    Component.translatable(animationsEnabled ? "screen.utilsmod.animations_on" : "screen.utilsmod.animations_off"),
                    button -> {
                        animationsEnabled = !animationsEnabled;
                        rebuildWidgets();
                    }
            ).bounds(sidebarWidth + 28, 156, 190, 28).build());
        }
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
        graphics.fill(sidebarWidth - 2, 0, sidebarWidth, height, ACCENT);
        graphics.fill(contentX, 48, contentX + contentWidth, panelBottom, PANEL);
        graphics.fill(contentX, 48, contentX + contentWidth, 51, ACCENT);
        graphics.text(font, Component.literal("UTILS"), 18, 24, TEXT);
        graphics.text(font, Component.literal("MOD"), 58, 24, ACCENT);
        graphics.text(font, Component.translatable("screen.utilsmod.version"), 18, 43, MUTED);
        graphics.text(font, Component.translatable("screen.utilsmod.workspace"), contentX + 18, 18, MUTED);
        graphics.text(font, page.title(), contentX + 18, 66, TEXT);

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
        if (contentWidth < 420) {
            drawCard(graphics, x + 18, 108, contentWidth - 36, Component.translatable("screen.utilsmod.active_modules"), "0", " ");
            drawCard(graphics, x + 18, 190, contentWidth - 36, Component.translatable("screen.utilsmod.game_version"), "26.2", "Fabric");
            drawCard(graphics, x + 18, 272, contentWidth - 36, Component.translatable("screen.utilsmod.release"), "1.0.0", " ");
            return;
        }

        int cardY = 112;
        int gap = 10;
        int cardWidth = (contentWidth - 36 - gap * 2) / 3;
        drawCard(graphics, x + 18, cardY, cardWidth, Component.translatable("screen.utilsmod.active_modules"), "0", " ");
        drawCard(graphics, x + 18 + cardWidth + gap, cardY, cardWidth, Component.translatable("screen.utilsmod.game_version"), "26.2", "Fabric");
        drawCard(graphics, x + 18 + (cardWidth + gap) * 2, cardY, cardWidth, Component.translatable("screen.utilsmod.release"), "1.0.0", " ");
        graphics.text(font, Component.translatable("screen.utilsmod.welcome"), x + 20, 216, TEXT);
        graphics.text(font, Component.translatable("screen.utilsmod.welcome_description"), x + 20, 236, MUTED);
        graphics.text(font, Component.translatable("screen.utilsmod.next_step"), x + 20, 274, ACCENT);
        graphics.text(font, Component.translatable("screen.utilsmod.next_step_description"), x + 20, 294, MUTED);
    }

    private void drawModules(GuiGraphicsExtractor graphics, int x, int contentWidth) {
        graphics.fill(x + 18, 108, x + contentWidth - 18, 180, PANEL_LIGHT);
        graphics.fill(x + 18, 108, x + 21, 180, ACCENT);
        graphics.text(font, Component.translatable("screen.utilsmod.module_library"), x + 32, 124, TEXT);
        graphics.text(font, Component.translatable("screen.utilsmod.module_library_description"), x + 32, 146, MUTED);
    }

    private void drawSettings(GuiGraphicsExtractor graphics, int x, int contentWidth) {
        graphics.fill(x + 18, 108, x + contentWidth - 18, 210, PANEL_LIGHT);
        graphics.text(font, Component.translatable("screen.utilsmod.interface_settings"), x + 32, 124, TEXT);
        graphics.text(font, Component.translatable("screen.utilsmod.animations_description"), x + 32, 140, MUTED);
    }

    private void drawCard(GuiGraphicsExtractor graphics, int x, int y, int cardWidth, Component label, String value, String detail) {
        graphics.fill(x, y, x + cardWidth, y + 76, PANEL_LIGHT);
        graphics.fill(x, y, x + cardWidth, y + 2, ACCENT);
        graphics.text(font, label, x + 10, y + 12, MUTED);
        graphics.text(font, Component.literal(value), x + 10, y + 32, TEXT);
        if (!detail.isBlank()) graphics.text(font, Component.literal(detail), x + 10, y + 52, MUTED);
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
