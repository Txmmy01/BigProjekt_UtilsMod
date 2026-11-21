package de.txmmy01.utilsmod.mixin;

import de.txmmy01.utilsmod.VisualModuleConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("RETURN"))
    private void utilsmod$clearLiquidFog(Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<FogData> callback) {
        FogData fog = callback.getReturnValue();
        if (fog == null) return;

        FogType type = camera.getFluidInCamera();
        boolean clearWater = type == FogType.WATER && VisualModuleConfig.isClearWaterEnabled();
        boolean clearLava = type == FogType.LAVA && VisualModuleConfig.isClearLavaEnabled();
        if (!clearWater && !clearLava) return;

        fog.environmentalStart = 0.0f;
        fog.environmentalEnd = Float.MAX_VALUE;
        fog.color.set(fog.color.x(), fog.color.y(), fog.color.z(), 0.0f);
        fog.skyEnd = Float.MAX_VALUE;
        fog.cloudEnd = Float.MAX_VALUE;
    }
}
