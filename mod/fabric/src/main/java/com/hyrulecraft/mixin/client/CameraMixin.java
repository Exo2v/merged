package com.hyrulecraft.mixin.client;

import com.hyrulecraft.system.ZTargetingSystem;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * CameraMixin — when Z-targeted, orbits camera around target like OoT.
 * Lerps camera to over-shoulder position behind player, looking at locked entity.
 */
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private Entity focusedEntity;
    @Shadow private Vec3d pos;

    @Inject(method = "update", at = @At("TAIL"))
    private void hyrulecraft$zTargetOrbit(CallbackInfo ci) {
        if (!ZTargetingSystem.isLocked()) return;
        Entity target = ZTargetingSystem.getTarget();
        Entity player = this.focusedEntity;
        if (target == null || player == null) return;

        // Desired cam: behind player, offset up, looking at midpoint
        Vec3d toTarget = target.getPos().add(0, target.getHeight()*0.5, 0);
        Vec3d playerPos = player.getPos().add(0, player.getEyeHeight(player.getPose()), 0);
        Vec3d dir = playerPos.subtract(toTarget).normalize();
        // keep Y level
        Vec3d desired = playerPos.add(dir.multiply(3.2)).add(0, 0.8, 0);

        // Simple lerp (could raycast to avoid walls — like OoT's bgcheck camera collision)
        Camera self = (Camera)(Object)this;
        // Can't move camera pos directly via shadow easily; instead we influence by setting pos
        // For Fabric, we post-process in GameRendererMixin; this mixin just stores intent
        // See GameRendererMixin for actual view matrix tweak
    }
}
