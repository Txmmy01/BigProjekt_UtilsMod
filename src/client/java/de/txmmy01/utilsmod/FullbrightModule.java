package de.txmmy01.utilsmod;

import net.minecraft.client.Minecraft;

public final class FullbrightModule {
    private static boolean applied;

    private FullbrightModule() {
    }

    public static void update(Minecraft client) {
        if (client == null) return;
        if (VisualModuleConfig.isFullbrightEnabled()) {
            applied = true;
            if (client.options.gamma().get() < 1.0) {
                client.options.gamma().set(1.0);
            }
        } else if (applied) {
            client.options.gamma().set(VisualModuleConfig.getOriginalGamma());
            applied = false;
        }
    }
}
