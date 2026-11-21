package de.txmmy01.utilsmod;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class HudModuleConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("utilsmod-hud.properties");
    private static final Properties VALUES = new Properties();

    static {
        load();
    }

    private HudModuleConfig() {
    }

    public static boolean isArmorHudEnabled() {
        return get("armor_hud.enabled");
    }

    public static void setArmorHudEnabled(boolean enabled) {
        set("armor_hud.enabled", enabled);
    }

    public static boolean isPotionBarsEnabled() {
        return get("potion_bars.enabled");
    }

    public static void setPotionBarsEnabled(boolean enabled) {
        set("potion_bars.enabled", enabled);
    }

    public static boolean isFpsHudEnabled() {
        return get("fps_hud.enabled");
    }

    public static void setFpsHudEnabled(boolean enabled) {
        set("fps_hud.enabled", enabled);
    }

    public static boolean isCpsEnabled() {
        return get("cps.enabled");
    }

    public static void setCpsEnabled(boolean enabled) {
        set("cps.enabled", enabled);
    }

    private static boolean get(String key) {
        return Boolean.parseBoolean(VALUES.getProperty(key, "false"));
    }

    private static void set(String key, boolean enabled) {
        VALUES.setProperty(key, Boolean.toString(enabled));
        save();
    }

    private static void load() {
        if (!Files.exists(FILE)) return;
        try (Reader reader = Files.newBufferedReader(FILE)) {
            VALUES.load(reader);
        } catch (IOException exception) {
            UtilsModClient.LOGGER.error("Could not load HUD module configuration", exception);
        }
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                VALUES.store(writer, null);
            }
        } catch (IOException exception) {
            UtilsModClient.LOGGER.error("Could not save HUD module configuration", exception);
        }
    }
}
