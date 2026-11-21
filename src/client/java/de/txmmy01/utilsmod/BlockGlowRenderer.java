package de.txmmy01.utilsmod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gizmos.Gizmos;

import java.util.ArrayList;
import java.util.List;

public final class BlockGlowRenderer {
    private static final int MAX_HIGHLIGHTS = 160;
    private static volatile List<Highlight> highlights = List.of();
    private static int ticks;

    private BlockGlowRenderer() {
    }

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

    private static void scan(Minecraft client) {
        List<Highlight> found = new ArrayList<>();
        int distance = BlockGlowConfig.getRenderDistance();
        int distanceSquared = distance * distance;
        BlockPos origin = client.player.blockPosition();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        String dimension = client.level.dimension().toString();

        for (int x = -distance; x <= distance && found.size() < MAX_HIGHLIGHTS; x++) {
            for (int y = -distance; y <= distance && found.size() < MAX_HIGHLIGHTS; y++) {
                for (int z = -distance; z <= distance && found.size() < MAX_HIGHLIGHTS; z++) {
                    if (x * x + y * y + z * z > distanceSquared) continue;
                    cursor.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (!client.level.hasChunkAt(cursor)) continue;
                    String blockId = BuiltInRegistries.BLOCK.getKey(client.level.getBlockState(cursor).getBlock()).toString();
                    if (BlockGlowConfig.isBlockEnabled(dimension, blockId)) {
                        found.add(new Highlight(cursor.immutable(), blockId, dimension));
                    }
                }
            }
        }
        highlights = List.copyOf(found);
    }

    private record Highlight(BlockPos position, String blockId, String dimension) {
    }

    private static void render() {
        Minecraft client = Minecraft.getInstance();
        if (!BlockGlowConfig.isEnabled() || client.level == null) return;
        String dimension = client.level.dimension().toString();
        float intensity = BlockGlowConfig.getIntensity();
        for (Highlight highlight : highlights) {
            if (!highlight.dimension().equals(dimension)) continue;
            String blockId = highlight.blockId();
            if (!BlockGlowConfig.isBlockEnabled(dimension, blockId)) continue;
            int color = BlockGlowConfig.getBlockColor(blockId);
            int alpha = Math.min(255, Math.round(((color >>> 24) & 0xFF) * intensity));
            int visibleColor = (color & 0x00FFFFFF) | (alpha << 24);
            Gizmos.cuboid(highlight.position(), GizmoStyle.stroke(visibleColor, 1.5f)).setAlwaysOnTop();
        }
    }
}
