package de.txmmy01.utilsmod.mixin;

import de.txmmy01.utilsmod.HudModuleRenderer;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Inject(method = "onButton", at = @At("HEAD"))
    private void utilsmod$trackMouseClick(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo callback) {
        HudModuleRenderer.onMouseClick(buttonInfo.button(), action);
    }
}