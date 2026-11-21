package de.txmmy01.utilsmod.mixin;

import de.txmmy01.utilsmod.HudLayoutConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class ScoreboardLayoutMixin {
    @Inject(method = "extractScoreboardSidebar", at = @At("HEAD"))
    private void utilsmod$moveScoreboard(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker, CallbackInfo ci) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(HudLayoutConfig.x("scoreboard", 0), HudLayoutConfig.y("scoreboard", 0));
        float scale = HudLayoutConfig.scale("scoreboard");
        graphics.pose().scale(scale, scale);
    }

    @Inject(method = "extractScoreboardSidebar", at = @At("RETURN"))
    private void utilsmod$restoreScoreboard(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker, CallbackInfo ci) {
        graphics.pose().popMatrix();
    }
}
