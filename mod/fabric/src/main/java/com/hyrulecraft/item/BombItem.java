package com.hyrulecraft.item;

import com.hyrulecraft.entity.BombEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Bomb — OoT bomb as throwable TNT.
 * 2s fuse, blast radius 4 (configurable), breaks SPIN_BREAKABLE + cracked stone,
 * and sets hyrulecraft:swch flag (redstone power) on nearby switch blocks.
 */
public class BombItem extends Item {
    public BombItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            BombEntity bomb = new BombEntity(world, user);
            bomb.setPosition(user.getX(), user.getEyeY() - 0.1, user.getZ());
            bomb.setVelocity(user, user.getPitch(), user.getYaw(), 0f, 0.9f, 0f);
            world.spawnEntity(bomb);
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 1f, 1f);
        if (!user.getAbilities().creativeMode) stack.decrement(1);
        user.getItemCooldownManager().set(this, 12);
        return TypedActionResult.success(stack, world.isClient);
    }
}
