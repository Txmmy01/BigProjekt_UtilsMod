package de.txmmy01.utilsmod;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UtilsModClient implements ClientModInitializer {
    public static final String MOD_ID = "utilsmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static KeyMapping openGuiKey;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "general")
        );
        openGuiKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.utilsmod.open_gui",
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                category
        ));
        ClientTickEvents.END_CLIENT_TICK.register(UtilsModClient::handleKeyInput);
        BlockGlowRenderer.register();
        LOGGER.info("UtilsMod {} initialized", "1.0.0");
    }

    private static void handleKeyInput(Minecraft client) {
        while (openGuiKey.consumeClick()) {
            if (client.gui.screen() == null) {
                client.gui.setScreen(new UtilsModScreen());
            } else if (client.gui.screen() instanceof UtilsModScreen) {
                client.gui.setScreen(null);
            }
        }
    }
}
