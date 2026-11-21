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

public final class HudModuleRenderer {
    private static final int PANEL = 0xD9090909;
    private static final int PANEL_BORDER = 0xFF777777;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int MUTED = 0xFFB8B8B8;
    private static final int GREEN = 0xFFFFFFFF;
    private static final int YELLOW = 0xFFD0D0D0;
    private static final int RED = 0xFF888888;

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
            if (!effect.showIcon() || (!effect.isInfiniteDuration() && effect.getDuration() <= 0)) continue;
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

        int infoY = HudModuleConfig.isCpsEnabled() ? 42 : HudModuleConfig.isFpsHudEnabled() ? 24 : 6;
        if (HudModuleConfig.isCoordinatesEnabled()) {
            var position = client.player.blockPosition();
            String coordinates = "XYZ  " + position.getX() + "  " + position.getY() + "  " + position.getZ();
            drawInfoLine(graphics, client, coordinates, infoY, width);
            infoY += 19;
        }
        if (HudModuleConfig.isDirectionEnabled()) {
            String facing = "FACING  " + client.player.getDirection().getName().toUpperCase(java.util.Locale.ROOT);
            drawInfoLine(graphics, client, facing, infoY, width);
            infoY += 19;
        }
        if (HudModuleConfig.isPingHudEnabled() && client.getConnection() != null) {
            var info = client.getConnection().getPlayerInfo(client.player.getUUID());
            if (info != null) {
                drawInfoLine(graphics, client, "PING  " + info.getLatency() + " ms", infoY, width);
                infoY += 19;
            }
        }
        if (HudModuleConfig.isBiomeHudEnabled()) {
            String biome = client.level.getBiome(client.player.blockPosition()).unwrapKey().map(Object::toString).orElse("unknown");
            drawInfoLine(graphics, client, "BIOME  " + biome, infoY, width);
            infoY += 19;
        }
        if (HudModuleConfig.isSpeedHudEnabled()) {
            double speed = client.player.getDeltaMovement().horizontalDistance() * 20.0;
            drawInfoLine(graphics, client, String.format(java.util.Locale.ROOT, "SPEED  %.2f m/s", speed), infoY, width);
            infoY += 19;
        }
        if (HudModuleConfig.isWorldTimeHudEnabled()) {
            long dayTime = Math.floorMod(client.level.getDefaultClockTime(), 24000L);
            int hours = (int) ((dayTime / 1000 + 6) % 24);
            int minutes = (int) (dayTime % 1000 * 60 / 1000);
            drawInfoLine(graphics, client, String.format(java.util.Locale.ROOT, "TIME  %02d:%02d", hours, minutes), infoY, width);
            infoY += 19;
        }
        if (HudModuleConfig.isHungerHudEnabled()) {
            drawInfoLine(graphics, client, "HUNGER  " + client.player.getFoodData().getFoodLevel() + "/20", infoY, width);
            infoY += 19;
        }
        if (HudModuleConfig.isPotionBarsEnabled()) {
            drawPotionBars(graphics, client, width, graphics.guiHeight(), infoY);
        }

        if (HudModuleConfig.isArmorHudEnabled()) {
            drawArmorHud(graphics, client, width, graphics.guiHeight());
        }
    }

    private static void drawInfoLine(GuiGraphicsExtractor graphics, Minecraft client, String text, int y, int screenWidth) {
        int panelWidth = Math.min(screenWidth - 12, Math.max(72, client.font.width(text) + 12));
        if (panelWidth < 52) return;
        String visible = text;
        while (!visible.isEmpty() && client.font.width(visible) > panelWidth - 10) {
            visible = visible.substring(0, visible.length() - 1);
        }
        if (!visible.equals(text) && visible.length() > 1) visible = visible.substring(0, visible.length() - 1) + "…";
        drawPanel(graphics, 6, y, panelWidth, 16);
        graphics.text(client.font, Component.literal(visible), 11, y + 4, TEXT);
    }

    private static void drawPotionBars(GuiGraphicsExtractor graphics, Minecraft client, int screenWidth, int screenHeight, int infoOffset) {
        List<MobEffectInstance> effects = new ArrayList<>(client.player.getActiveEffects());
        effects.removeIf(effect -> !effect.showIcon() || (!effect.isInfiniteDuration() && effect.getDuration() <= 0));
        effects.sort((first, second) -> Integer.compare(second.getDuration(), first.getDuration()));

        int x = 6;
        int y = Math.max(infoOffset + 19, HudModuleConfig.isCpsEnabled() ? 44 : HudModuleConfig.isFpsHudEnabled() ? 25 : 6);
        int panelWidth = Math.max(1, Math.min(116, screenWidth - 12));
        int rowHeight = 20;
        int visibleRows = Math.max(0, (screenHeight - y - 6) / rowHeight);
        int count = Math.min(Math.min(effects.size(), 5), visibleRows);
        if (count == 0 || panelWidth < 52) return;

        for (int index = 0; index < count; index++) {
            MobEffectInstance effect = effects.get(index);
            int rowY = y + index * rowHeight;
            drawPanel(graphics, x, rowY, panelWidth, 17);

            String name = effect.getEffect().value().getDisplayName().getString();
            int duration = effect.getDuration();
            String time = effect.isInfiniteDuration() ? "∞" : formatDuration(duration);
            int nameMaxWidth = Math.max(8, panelWidth - client.font.width(time) - 18);
            while (!name.isEmpty() && client.font.width(name) > nameMaxWidth) {
                name = name.substring(0, name.length() - 1);
            }
            if (!name.equals(effect.getEffect().value().getDisplayName().getString())) name += "…";
            int barColor = effect.getEffect().value().getColor();

            graphics.text(client.font, Component.literal(name), x + 5, rowY + 3, TEXT);
            graphics.text(client.font, Component.literal(time), x + panelWidth - client.font.width(time) - 5, rowY + 3, MUTED);

            int barX = x + 5;
            int barY = rowY + 13;
            int barWidth = panelWidth - 10;
            graphics.fill(barX, barY, barX + barWidth, barY + 2, 0xFF303844);
            int maximum = potionMaxDurations.getOrDefault(potionKey(effect), Math.max(1, duration));
            float progress = effect.isInfiniteDuration() ? 1.0f : Math.clamp(duration / (float) Math.max(1, maximum), 0.0f, 1.0f);
            graphics.fill(barX, barY, barX + Math.max(1, Math.round(barWidth * progress)), barY + 2, barColor);
        }
    }

    private static void drawArmorHud(GuiGraphicsExtractor graphics, Minecraft client, int screenWidth, int screenHeight) {
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        if (screenHeight < 82) return;
        int panelWidth = Math.min(108, Math.max(1, screenWidth - 12));
        int panelHeight = 76;
        int x = Math.max(6, screenWidth - panelWidth - 6);
        int y = 6;
        if (panelWidth < 50 || panelHeight < 20) return;
        drawPanel(graphics, x, y, panelWidth, panelHeight);

        for (int index = 0; index < slots.length; index++) {
            ItemStack stack = client.player.getItemBySlot(slots[index]);
            int rowY = y + 4 + index * 18;
            String value = stack.isEmpty() || stack.getMaxDamage() <= 0
                    ? "--"
                    : Integer.toString(Math.max(0, 100 - Math.round(stack.getDamageValue() * 100.0f / stack.getMaxDamage()))) + "%";
            int color = stack.isEmpty() || stack.getMaxDamage() <= 0
                    ? MUTED
                    : stack.getDamageValue() * 100.0f / stack.getMaxDamage() >= 80.0f
                    ? RED
                    : stack.getDamageValue() * 100.0f / stack.getMaxDamage() >= 55.0f ? YELLOW : GREEN;

            if (!stack.isEmpty()) graphics.item(stack, x + 4, rowY - 4);
            else graphics.text(client.font, Component.literal("·"), x + 9, rowY, MUTED);
            int barX = x + 24;
            int barWidth = Math.max(4, panelWidth - 66);
            graphics.fill(barX, rowY + 2, barX + barWidth, rowY + 8, 0xFF303844);
            if (!stack.isEmpty() && stack.getMaxDamage() > 0) {
                float remaining = Math.max(0.0f, 1.0f - stack.getDamageValue() / (float) stack.getMaxDamage());
                graphics.fill(barX, rowY + 2, barX + Math.max(1, Math.round(barWidth * remaining)), rowY + 8, color);
            }
            graphics.text(client.font, Component.literal(value), x + panelWidth - client.font.width(value) - 4, rowY, color);
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
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }
}
