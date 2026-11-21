package de.txmmy01.utilsmod;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UtilsModClient implements ClientModInitializer {
    public static final String MOD_ID = "utilsmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("UtilsMod {} initialized", "1.0.0");
    }
}
