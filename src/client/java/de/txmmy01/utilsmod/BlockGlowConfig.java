package de.txmmy01.utilsmod;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
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
        String currentKey = key(dimension, blockId, "enabled");
        String legacyKey = "dimension." + dimension.replace(':', '.') + "." + blockId.replace(':', '.') + ".enabled";
        return Boolean.parseBoolean(VALUES.getProperty(currentKey, VALUES.getProperty(legacyKey, "true")));
    }

    public static void toggleBlock(String dimension, String blockId) {
        setBlockEnabled(dimension, blockId, !isBlockEnabled(dimension, blockId));
    }

    public static void setBlockEnabled(String dimension, String blockId, boolean value) {
        if (!DEFAULT_BLOCKS.containsKey(blockId)) return;
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
        VALUES.setProperty("color." + blockId, String.format(java.util.Locale.ROOT, "#%08X", next));
        save();
    }

    public static String dimensionKey(String dimension) {
        return java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString(dimension.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private static String key(String dimension, String blockId, String setting) {
        return "dimension64." + dimensionKey(dimension) + "." + dimensionKey(blockId) + "." + setting;
    }

    private static int parseColor(String value, int fallback) {
        return ConfigValues.parseColor(value, fallback);
    }

    private static void load() {
        ConfigStorage.load(FILE, VALUES, "UtilsMod");
        try {
            enabled = Boolean.parseBoolean(VALUES.getProperty("blockglow.enabled", "false"));
            renderDistance = ConfigValues.parseDistance(VALUES.getProperty("blockglow.distance"), 16);
            intensity = ConfigValues.parseIntensity(VALUES.getProperty("blockglow.intensity"), 1.0f);
        } catch (RuntimeException exception) {
            UtilsModClient.LOGGER.error("Could not parse UtilsMod configuration values", exception);
        }
    }

    private static void save() {
        VALUES.setProperty("blockglow.enabled", Boolean.toString(enabled));
        VALUES.setProperty("blockglow.distance", Integer.toString(renderDistance));
        VALUES.setProperty("blockglow.intensity", Float.toString(intensity));
        ConfigStorage.save(FILE, VALUES, "UtilsMod");
    }
}
