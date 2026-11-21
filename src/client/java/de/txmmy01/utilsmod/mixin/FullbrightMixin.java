package de.txmmy01.utilsmod.mixin;

import de.txmmy01.utilsmod.VisualModuleConfig;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapRenderStateExtractor.class)
public abstract class FullbrightMixin {
    private static final Vector3f WHITE = new Vector3f(1.0f, 1.0f, 1.0f);
    @Inject(method = "extract", at = @At("RETURN"))
    private void utilsmod$applyFullbright(LightmapRenderState state, float partialTicks, CallbackInfo callback) {
        if (!VisualModuleConfig.isFullbrightEnabled()) return;

        state.skyFactor = 1.0f;
        state.blockFactor = 1.0f;
        state.blockLightTint = WHITE;
        state.skyLightColor = WHITE;
        state.ambientColor = WHITE;
        state.darknessEffectScale = 0.0f;
        state.bossOverlayWorldDarkening = 0.0f;
        state.brightness = 1.0f;
    }
}
