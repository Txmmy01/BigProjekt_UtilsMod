package de.txmmy01.utilsmod;

import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;
import java.util.Properties;

public final class HudLayoutConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("utilsmod-hud-layout.properties");
    private static final Properties VALUES = new Properties();
    static { ConfigStorage.load(FILE, VALUES, "HUD layout"); }
    private HudLayoutConfig() {}
    public static int x(String id, int fallback) { return integer(id + ".x", 0); }
    public static int y(String id, int fallback) { return integer(id + ".y", 0); }
    public static float scale(String id) {
        try { return Math.clamp(Float.parseFloat(VALUES.getProperty(id + ".scale", "1.0")), 0.5f, 2.5f); }
        catch (NumberFormatException ignored) { return 1.0f; }
    }
    public static void move(String id, int x, int y) {
        VALUES.setProperty(id + ".x", Integer.toString(x));
        VALUES.setProperty(id + ".y", Integer.toString(y));
        save();
    }
    public static void setScale(String id, float scale) {
        VALUES.setProperty(id + ".scale", Float.toString(Math.clamp(scale, 0.5f, 2.5f)));
        save();
    }
    private static int integer(String key, int fallback) {
        try { return Integer.parseInt(VALUES.getProperty(key, Integer.toString(fallback))); }
        catch (NumberFormatException ignored) { return fallback; }
    }
    private static void save() { ConfigStorage.save(FILE, VALUES, "HUD layout"); }
}
