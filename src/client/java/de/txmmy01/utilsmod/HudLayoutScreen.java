package de.txmmy01.utilsmod;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class HudLayoutScreen extends Screen {
    private static final List<Element> ELEMENTS = List.of(
            new Element("fps", "FPS", 6, 6, 76, 20),
            new Element("cps", "CPS", 6, 24, 104, 20),
            new Element("info", "Info modules", 6, 42, 180, 150),
            new Element("potions", "Potion bars", 6, 190, 122, 110),
            new Element("armor", "Armor HUD", -114, 6, 108, 80),
            new Element("scoreboard", "Scoreboard", -170, -100, 170, 120)
    );
    private String selected = "fps";
    private boolean dragging;
    private double dragX, dragY;
    private int startX, startY;

    public HudLayoutScreen() { super(Component.literal("HUD Layout Editor")); }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0x99000000);
        graphics.text(font, Component.literal("HUD LAYOUT EDITOR"), 12, 10, 0xFFFFFFFF);
        graphics.text(font, Component.literal("Drag an element to move it  •  Mouse wheel: resize  •  ESC: close"), 12, 24, 0xFFCCCCCC);
        for (Element element : ELEMENTS) {
            int x = baseX(element) + HudLayoutConfig.x(element.id, baseX(element));
            int y = baseY(element) + HudLayoutConfig.y(element.id, baseY(element));
            float scale = HudLayoutConfig.scale(element.id);
            int w = Math.max(30, Math.round(element.width * scale));
            int h = Math.max(16, Math.round(element.height * scale));
            int color = selected.equals(element.id) ? 0xAA2B78D0 : 0x88555555;
            graphics.fill(x, y, x + w, y + h, color);
            graphics.fill(x, y, x + w, y + 1, 0xFFFFFFFF);
            graphics.text(font, Component.literal(element.label + "  " + Math.round(scale * 100) + "%"), x + 4, y + 4, 0xFFFFFFFF);
        }
        graphics.text(font, Component.literal("Selected: " + selected + "  |  Scroll to scale"), 12, height - 20, 0xFFFFFFFF);
    }

    private int baseX(Element e) { return e.x < 0 ? width + e.x : e.x; }
    private int baseY(Element e) { return e.y < 0 ? height / 2 + e.y : e.y; }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = ELEMENTS.size() - 1; i >= 0; i--) {
                Element e = ELEMENTS.get(i);
                int x = baseX(e) + HudLayoutConfig.x(e.id, baseX(e));
                int y = baseY(e) + HudLayoutConfig.y(e.id, baseY(e));
                float scale = HudLayoutConfig.scale(e.id);
                if (mouseX >= x && mouseX <= x + e.width * scale && mouseY >= y && mouseY <= y + e.height * scale) {
                    selected = e.id;
                    dragging = true;
                    dragX = mouseX;
                    dragY = mouseY;
                    startX = x;
                    startY = y;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging && button == 0) {
            Element e = element(selected);
            HudLayoutConfig.move(selected, startX + (int)(mouseX - this.dragX) - baseX(e),
                    startY + (int)(mouseY - this.dragY) - baseY(e));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) { dragging = false; return true; }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        HudLayoutConfig.setScale(selected, HudLayoutConfig.scale(selected) + (verticalAmount > 0 ? 0.1f : -0.1f));
        return true;
    }

    private Element element(String id) { return ELEMENTS.stream().filter(e -> e.id.equals(id)).findFirst().orElse(ELEMENTS.get(0)); }
    @Override public boolean isPauseScreen() { return false; }
    @Override public void onClose() { minecraft.gui.setScreen(new UtilsModScreen()); }
    private record Element(String id, String label, int x, int y, int width, int height) {}
}
