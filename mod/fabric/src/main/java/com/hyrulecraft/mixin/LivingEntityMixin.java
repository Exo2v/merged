package com.hyrulecraft.mixin;

import com.hyrulecraft.item.HylianSwordItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * LivingEntityMixin — handles jump-slash damage doubling and sword sweep hooks.
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float hyrulecraft$boostJumpSlash(float amount, DamageSource source) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (source.getAttacker() instanceof PlayerEntity player) {
            if (HylianSwordItem.isJumpSlash(player)) {
                return amount * 2.0f;
            }
        }
        return amount;
    }
}
