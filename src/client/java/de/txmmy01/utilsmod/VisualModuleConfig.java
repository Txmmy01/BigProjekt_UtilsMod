package de.txmmy01.utilsmod;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class VisualModuleConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("utilsmod-visual.properties");
    private static final Properties VALUES = new Properties();

    static {
        load();
    }

    private VisualModuleConfig() {
    }

    public static boolean isFullbrightEnabled() {
        return Boolean.parseBoolean(VALUES.getProperty("fullbright.enabled", "false"));
    }

    public static void setFullbrightEnabled(boolean enabled) {
        VALUES.setProperty("fullbright.enabled", Boolean.toString(enabled));
        save();
    }

    public static boolean isClearLavaEnabled() {
        return Boolean.parseBoolean(VALUES.getProperty("clear_lava.enabled", "false"));
    }

    public static void setClearLavaEnabled(boolean enabled) {
        VALUES.setProperty("clear_lava.enabled", Boolean.toString(enabled));
        save();
    }

    public static boolean isClearWaterEnabled() {
        return Boolean.parseBoolean(VALUES.getProperty("clear_water.enabled", "false"));
    }

    public static void setClearWaterEnabled(boolean enabled) {
        VALUES.setProperty("clear_water.enabled", Boolean.toString(enabled));
        save();
    }

    private static void load() {
        ConfigStorage.load(FILE, VALUES, "visual module");
    }

    private static void save() {
        ConfigStorage.save(FILE, VALUES, "visual module");
    }
}
