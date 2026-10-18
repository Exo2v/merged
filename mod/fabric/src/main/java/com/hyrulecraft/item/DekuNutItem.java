package com.hyrulecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/** Flash stun — blinds nearby mobs for 2s */
public class DekuNutItem extends Item {
    public DekuNutItem(Settings settings) { super(settings); }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            world.getEntitiesByClass(net.minecraft.entity.LivingEntity.class, user.getBoundingBox().expand(5), e-> e!=user)
                 .forEach(e-> e.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.BLINDNESS, 40, 0)));
            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1f, 1.5f);
        }
        user.getStackInHand(hand).decrement(1);
        user.getItemCooldownManager().set(this, 20);
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
    }
}
