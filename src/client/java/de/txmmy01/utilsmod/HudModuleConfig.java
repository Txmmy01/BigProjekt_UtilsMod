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

    public static boolean isCoordinatesEnabled() { return get("coordinates.enabled"); }
    public static void setCoordinatesEnabled(boolean enabled) { set("coordinates.enabled", enabled); }

    public static boolean isDirectionEnabled() { return get("direction.enabled"); }
    public static void setDirectionEnabled(boolean enabled) { set("direction.enabled", enabled); }

    public static boolean isCpsEnabled() {
        return get("cps.enabled");
    }

    public static void setCpsEnabled(boolean enabled) {
        set("cps.enabled", enabled);
    }

    public static boolean isPingHudEnabled() { return get("ping_hud.enabled"); }
    public static void setPingHudEnabled(boolean enabled) { set("ping_hud.enabled", enabled); }
    public static boolean isBiomeHudEnabled() { return get("biome_hud.enabled"); }
    public static void setBiomeHudEnabled(boolean enabled) { set("biome_hud.enabled", enabled); }
    public static boolean isSpeedHudEnabled() { return get("speed_hud.enabled"); }
    public static void setSpeedHudEnabled(boolean enabled) { set("speed_hud.enabled", enabled); }
    public static boolean isWorldTimeHudEnabled() { return get("world_time_hud.enabled"); }
    public static void setWorldTimeHudEnabled(boolean enabled) { set("world_time_hud.enabled", enabled); }
    public static boolean isHungerHudEnabled() { return get("hunger_hud.enabled"); }
    public static void setHungerHudEnabled(boolean enabled) { set("hunger_hud.enabled", enabled); }

    private static boolean get(String key) {
        return Boolean.parseBoolean(VALUES.getProperty(key, "false"));
    }

    private static void set(String key, boolean enabled) {
        VALUES.setProperty(key, Boolean.toString(enabled));
        save();
    }

    private static void load() {
        ConfigStorage.load(FILE, VALUES, "HUD module");
    }

    private static void save() {
        ConfigStorage.save(FILE, VALUES, "HUD module");
    }
}
