package de.txmmy01.utilsmod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class HudModuleRenderer {
    private static final int PANEL = 0xB810141B;
    private static final int PANEL_BORDER = 0xFF343D49;
    private static final int TEXT = 0xFFF4F6FA;
    private static final int MUTED = 0xFF9BA6B5;
    private static final int GREEN = 0xFF55D68A;
    private static final int YELLOW = 0xFFFFC44D;
    private static final int RED = 0xFFFF647C;

    private static boolean leftWasDown;
    private static boolean rightWasDown;
    private static int leftClicks;
    private static int rightClicks;
    private static int leftCps;
    private static int rightCps;
    private static long cpsWindowStart = System.nanoTime();

    private HudModuleRenderer() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(HudModuleRenderer::tick);
        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(UtilsModClient.MOD_ID, "utility_hud"),
                HudModuleRenderer::render
        );
    }

    private static final java.util.Map<String, Integer> potionMaxDurations = new java.util.HashMap<>();

    private static void tick(Minecraft client) {
        if (client.player == null || client.level == null || !HudModuleConfig.isCpsEnabled()) {
            leftClicks = 0;
            rightClicks = 0;
            leftCps = 0;
            rightCps = 0;
            cpsWindowStart = System.nanoTime();
        } else {
            long now = System.nanoTime();
            if (now - cpsWindowStart >= 1_000_000_000L) {
                leftCps = leftClicks;
                rightCps = rightClicks;
                leftClicks = 0;
                rightClicks = 0;
                cpsWindowStart = now;
            }
        }

        if (client.player == null || !HudModuleConfig.isPotionBarsEnabled()) {
            potionMaxDurations.clear();
            return;
        }
        java.util.Set<String> activeEffects = new java.util.HashSet<>();
        for (MobEffectInstance effect : client.player.getActiveEffects()) {
            if (!effect.showIcon() || effect.isInfiniteDuration()) continue;
            String key = potionKey(effect);
            activeEffects.add(key);
            potionMaxDurations.merge(key, effect.getDuration(), Math::max);
        }
        potionMaxDurations.keySet().retainAll(activeEffects);
    }

    private static String potionKey(MobEffectInstance effect) {
        return effect.getEffect().value().getDisplayName().getString() + "#" + effect.getAmplifier();
    }

    public static void onMouseClick(int button, int action) {
        if (action != GLFW.GLFW_PRESS || !HudModuleConfig.isCpsEnabled()) return;
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) leftClicks++;
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) rightClicks++;
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || client.gui.screen() != null) return;

        int width = graphics.guiWidth();

        if (HudModuleConfig.isFpsHudEnabled()) {
            drawPanel(graphics, 6, 6, 72, 15);
            graphics.text(client.font, Component.literal("FPS  " + client.getFps()), 11, 10, TEXT);
        }

        if (HudModuleConfig.isCpsEnabled()) {
            drawPanel(graphics, 6, 24, 100, 15);
            graphics.text(client.font, Component.literal("L " + leftCps + "  R " + rightCps), 11, 28, TEXT);
        }

        if (HudModuleConfig.isPotionBarsEnabled()) {
            drawPotionBars(graphics, client);
        }

        if (HudModuleConfig.isArmorHudEnabled()) {
            drawArmorHud(graphics, client, width);
        }
    }

    private static void drawPotionBars(GuiGraphicsExtractor graphics, Minecraft client) {
        List<MobEffectInstance> effects = new ArrayList<>(client.player.getActiveEffects());
        effects.removeIf(effect -> !effect.showIcon());
        effects.sort((first, second) -> Integer.compare(second.getDuration(), first.getDuration()));

        int count = Math.min(effects.size(), 5);
        if (count == 0) return;

        int x = 6;
        int y = HudModuleConfig.isCpsEnabled() ? 44 : HudModuleConfig.isFpsHudEnabled() ? 25 : 6;
        int panelWidth = 116;
        int rowHeight = 20;

        for (int index = 0; index < count; index++) {
            MobEffectInstance effect = effects.get(index);
            int rowY = y + index * rowHeight;
            drawPanel(graphics, x, rowY, panelWidth, 17);

            String name = effect.getEffect().value().getDisplayName().getString();
            if (name.length() > 12) name = name.substring(0, 11) + "…";
            int duration = effect.getDuration();
            String time = effect.isInfiniteDuration() ? "∞" : formatDuration(duration);
            int barColor = effect.getEffect().value().isBeneficial() ? GREEN : RED;

            graphics.text(client.font, Component.literal(name), x + 5, rowY + 3, TEXT);
            graphics.text(client.font, Component.literal(time), x + panelWidth - client.font.width(time) - 5, rowY + 3, MUTED);

            int barX = x + 5;
            int barY = rowY + 13;
            int barWidth = panelWidth - 10;
            graphics.fill(barX, barY, barX + barWidth, barY + 2, 0xFF303844);
            float progress = effect.isInfiniteDuration() ? 1.0f : Math.min(1.0f, duration / 3600.0f);
            graphics.fill(barX, barY, barX + Math.max(1, Math.round(barWidth * progress)), barY + 2, barColor);
        }
    }

    private static void drawArmorHud(GuiGraphicsExtractor graphics, Minecraft client, int screenWidth) {
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        String[] labels = {"H", "C", "L", "B"};
        int x = screenWidth - 91;
        int y = 6;
        drawPanel(graphics, x, y, 85, 54);

        for (int index = 0; index < slots.length; index++) {
            ItemStack stack = client.player.getItemBySlot(slots[index]);
            int rowY = y + 5 + index * 12;
            String value = stack.isEmpty() || stack.getMaxDamage() <= 0
                    ? "--"
                    : Integer.toString(Math.max(0, 100 - Math.round(stack.getDamageValue() * 100.0f / stack.getMaxDamage()))) + "%";
            int color = stack.isEmpty() || stack.getMaxDamage() <= 0
                    ? MUTED
                    : stack.getDamageValue() * 100.0f / stack.getMaxDamage() >= 80.0f
                    ? RED
                    : stack.getDamageValue() * 100.0f / stack.getMaxDamage() >= 55.0f ? YELLOW : GREEN;

            graphics.text(client.font, Component.literal(labels[index]), x + 5, rowY, MUTED);
            int barX = x + 18;
            int barWidth = 39;
            graphics.fill(barX, rowY + 2, barX + barWidth, rowY + 8, 0xFF303844);
            if (!stack.isEmpty() && stack.getMaxDamage() > 0) {
                float remaining = Math.max(0.0f, 1.0f - stack.getDamageValue() / (float) stack.getMaxDamage());
                graphics.fill(barX, rowY + 2, barX + Math.max(1, Math.round(barWidth * remaining)), rowY + 8, color);
            }
            graphics.text(client.font, Component.literal(value), x + 61, rowY, color);
        }
    }

    private static void drawPanel(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, PANEL);
        graphics.fill(x, y, x + width, y + 1, PANEL_BORDER);
        graphics.fill(x, y + height - 1, x + width, y + height, PANEL_BORDER);
    }

    private static String formatDuration(int ticks) {
        int totalSeconds = Math.max(0, ticks) / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.ROOT, "%d:%02d", minutes, seconds);
    }
}
