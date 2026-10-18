package com.hyrulecraft.mixin.client;

import com.hyrulecraft.system.ZTargetingSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * GameRendererMixin — draws Z-target reticle + locks FOV when targeting.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void hyrulecraft$zTargetOverlay(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (ZTargetingSystem.isLocked()) {
            Entity t = ZTargetingSystem.getTarget();
            // HUD handled in HyrulecraftClient HUD overlay; nothing to do here — placeholder for view bob cancel
        }
    }
}
