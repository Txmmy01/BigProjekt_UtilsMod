package de.txmmy01.utilsmod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BlockGlowRenderer {
    private static final int MAX_HIGHLIGHTS = 160;
    private static final Map<Integer, List<Offset>> OFFSETS_BY_DISTANCE = new HashMap<>();
    private static volatile List<Highlight> highlights = List.of();
    private static int ticks;

    private BlockGlowRenderer() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(BlockGlowRenderer::tick);
        LevelRenderEvents.BEFORE_GIZMOS.register(context -> render());
    }

    private static void tick(Minecraft client) {
        if (!BlockGlowConfig.isEnabled() || client.level == null || client.player == null) {
            highlights = List.of();
            ticks = 0;
            return;
        }
        if (++ticks < 10) return;
        ticks = 0;
        scan(client);
    }

    private static List<Offset> offsetsFor(int distance) {
        return OFFSETS_BY_DISTANCE.computeIfAbsent(distance, radius -> {
            int radiusSquared = radius * radius;
            List<Offset> offsets = new ArrayList<>();
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        int squared = x * x + y * y + z * z;
                        if (squared <= radiusSquared) offsets.add(new Offset(x, y, z, squared));
                    }
                }
            }
            offsets.sort(Comparator.comparingInt(Offset::distanceSquared)
                    .thenComparingInt(Offset::x)
                    .thenComparingInt(Offset::y)
                    .thenComparingInt(Offset::z));
            return List.copyOf(offsets);
        });
    }

    private static void scan(Minecraft client) {
        List<Highlight> found = new ArrayList<>(MAX_HIGHLIGHTS);
        int distance = BlockGlowConfig.getRenderDistance();
        BlockPos origin = client.player.blockPosition();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        String dimension = client.level.dimension().toString();

        for (Offset offset : offsetsFor(distance)) {
            if (found.size() >= MAX_HIGHLIGHTS) break;
            cursor.set(origin.getX() + offset.x(), origin.getY() + offset.y(), origin.getZ() + offset.z());
            if (!client.level.hasChunkAt(cursor)) continue;
            String blockId = BuiltInRegistries.BLOCK.getKey(client.level.getBlockState(cursor).getBlock()).toString();
            if (!BlockGlowConfig.isBlockEnabled(dimension, blockId)) continue;
            found.add(new Highlight(cursor.immutable(), blockId, dimension, BlockGlowConfig.getBlockColor(blockId)));
        }
        highlights = List.copyOf(found);
    }

    private record Offset(int x, int y, int z, int distanceSquared) {}
    private record Highlight(BlockPos position, String blockId, String dimension, int color) {}

    private static void render() {
        Minecraft client = Minecraft.getInstance();
        if (!BlockGlowConfig.isEnabled() || client.level == null) return;
        String dimension = client.level.dimension().toString();
        float intensity = BlockGlowConfig.getIntensity();
        for (Highlight highlight : highlights) {
            if (!highlight.dimension().equals(dimension)) continue;
            if (!BlockGlowConfig.isBlockEnabled(dimension, highlight.blockId())) continue;
            int color = highlight.color();
            int alpha = Math.min(255, Math.round(((color >>> 24) & 0xFF) * intensity));
            int visibleColor = (color & 0x00FFFFFF) | (alpha << 24);
            Gizmos.cuboid(highlight.position(), GizmoStyle.stroke(visibleColor, 1.5f)).setAlwaysOnTop();
        }
    }
}