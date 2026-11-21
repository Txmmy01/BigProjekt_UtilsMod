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

    public static double getOriginalGamma() {
        try {
            return Math.clamp(Double.parseDouble(VALUES.getProperty("fullbright.originalGamma", "0.5")), 0.0, 1.0);
        } catch (NumberFormatException exception) {
            return 0.5;
        }
    }

    public static void setFullbrightEnabled(boolean enabled, double currentGamma) {
        if (enabled && !isFullbrightEnabled()) {
            VALUES.setProperty("fullbright.originalGamma", Double.toString(Math.clamp(currentGamma, 0.0, 1.0)));
        }
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
        if (!Files.exists(FILE)) return;
        try (Reader reader = Files.newBufferedReader(FILE)) {
            VALUES.load(reader);
        } catch (IOException exception) {
            UtilsModClient.LOGGER.error("Could not load visual module configuration", exception);
        }
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                VALUES.store(writer, null);
            }
        } catch (IOException exception) {
            UtilsModClient.LOGGER.error("Could not save visual module configuration", exception);
        }
    }
}
