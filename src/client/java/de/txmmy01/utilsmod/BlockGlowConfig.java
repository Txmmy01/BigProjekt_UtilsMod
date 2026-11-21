package de.txmmy01.utilsmod;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public final class BlockGlowConfig {
    public static final Map<String, Integer> DEFAULT_BLOCKS = Map.ofEntries(
            Map.entry("minecraft:diamond_ore", 0xFF35D8FF),
            Map.entry("minecraft:deepslate_diamond_ore", 0xFF35D8FF),
            Map.entry("minecraft:emerald_ore", 0xFF55E68A),
            Map.entry("minecraft:deepslate_emerald_ore", 0xFF55E68A),
            Map.entry("minecraft:gold_ore", 0xFFFFC44D),
            Map.entry("minecraft:deepslate_gold_ore", 0xFFFFC44D),
            Map.entry("minecraft:iron_ore", 0xFFCBD5E1),
            Map.entry("minecraft:deepslate_iron_ore", 0xFFCBD5E1),
            Map.entry("minecraft:spawner", 0xFFB18CFF),
            Map.entry("minecraft:chest", 0xFFFFA45C),
            Map.entry("minecraft:trapped_chest", 0xFFFF725E),
            Map.entry("minecraft:ender_chest", 0xFFB56CFF)
    );

    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("utilsmod.properties");
    private static final Properties VALUES = new Properties();
    private static volatile boolean enabled;
    private static volatile int renderDistance = 16;
    private static volatile float intensity = 1.0f;

    static {
        load();
    }

    private BlockGlowConfig() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        save();
    }

    public static int getRenderDistance() {
        return renderDistance;
    }

    public static void cycleRenderDistance() {
        renderDistance = switch (renderDistance) {
            case 8 -> 16;
            case 16 -> 24;
            case 24 -> 32;
            default -> 8;
        };
        save();
    }

    public static float getIntensity() {
        return intensity;
    }

    public static void cycleIntensity() {
        intensity = intensity >= 1.5f ? 0.5f : intensity + 0.5f;
        save();
    }

    public static boolean isBlockEnabled(String dimension, String blockId) {
        if (!DEFAULT_BLOCKS.containsKey(blockId)) return false;
        return Boolean.parseBoolean(VALUES.getProperty(key(dimension, blockId, "enabled"), "true"));
    }

    public static void toggleBlock(String dimension, String blockId) {
        setBlockEnabled(dimension, blockId, !isBlockEnabled(dimension, blockId));
    }

    public static void setBlockEnabled(String dimension, String blockId, boolean value) {
        VALUES.setProperty(key(dimension, blockId, "enabled"), Boolean.toString(value));
        save();
    }

    public static int getBlockColor(String blockId) {
        return parseColor(VALUES.getProperty("color." + blockId), DEFAULT_BLOCKS.getOrDefault(blockId, 0xFFFF8A32));
    }

    public static void cycleBlockColor(String blockId) {
        int[] palette = {0xFFFF8A32, 0xFF35D8FF, 0xFF55E68A, 0xFFFFC44D, 0xFFB18CFF, 0xFFFF647C};
        int current = getBlockColor(blockId);
        int next = palette[0];
        for (int index = 0; index < palette.length; index++) {
            if (palette[index] == current) {
                next = palette[(index + 1) % palette.length];
                break;
            }
        }
        VALUES.setProperty("color." + blockId, String.format("#%08X", next));
        save();
    }

    public static String dimensionKey(String dimension) {
        return dimension.replace(':', '.');
    }

    private static String key(String dimension, String blockId, String setting) {
        return "dimension." + dimensionKey(dimension) + "." + blockId.replace(':', '.') + "." + setting;
    }

    private static int parseColor(String value, int fallback) {
        if (value == null) return fallback;
        try {
            return (int) Long.parseLong(value.replace("#", ""), 16);
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private static void load() {
        if (!Files.exists(FILE)) return;
        try (Reader reader = Files.newBufferedReader(FILE)) {
            VALUES.load(reader);
            enabled = Boolean.parseBoolean(VALUES.getProperty("blockglow.enabled", "false"));
            renderDistance = Math.clamp(Integer.parseInt(VALUES.getProperty("blockglow.distance", "16")), 8, 32);
            intensity = Math.clamp(Float.parseFloat(VALUES.getProperty("blockglow.intensity", "1.0")), 0.5f, 2.0f);
        } catch (IOException | NumberFormatException exception) {
            UtilsModClient.LOGGER.error("Could not load UtilsMod configuration", exception);
        }
    }

    private static void save() {
        VALUES.setProperty("blockglow.enabled", Boolean.toString(enabled));
        VALUES.setProperty("blockglow.distance", Integer.toString(renderDistance));
        VALUES.setProperty("blockglow.intensity", Float.toString(intensity));
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                VALUES.store(writer, null);
            }
        } catch (IOException exception) {
            UtilsModClient.LOGGER.error("Could not save UtilsMod configuration", exception);
        }
    }
}
