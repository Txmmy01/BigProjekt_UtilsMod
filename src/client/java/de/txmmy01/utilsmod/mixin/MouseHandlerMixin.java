package de.txmmy01.utilsmod.mixin;

import de.txmmy01.utilsmod.HudModuleRenderer;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Inject(method = "onPress", at = @At("HEAD"))
    private void utilsmod$trackMouseClick(long window, int button, int action, int modifiers, CallbackInfo callback) {
        HudModuleRenderer.onMouseClick(button, action);
    }
}