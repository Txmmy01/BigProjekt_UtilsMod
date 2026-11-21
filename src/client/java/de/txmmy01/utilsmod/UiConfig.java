package de.txmmy01.utilsmod;

import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;
import java.util.Properties;

final class UiConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("utilsmod-ui.properties");
    private static final Properties VALUES = new Properties();
    static { ConfigStorage.load(FILE, VALUES, "UI"); }
    private UiConfig() {}
    static boolean isAnimationsEnabled() {
        return Boolean.parseBoolean(VALUES.getProperty("animations.enabled", "true"));
    }
    static void setAnimationsEnabled(boolean enabled) {
        VALUES.setProperty("animations.enabled", Boolean.toString(enabled));
        ConfigStorage.save(FILE, VALUES, "UI");
    }
}
