package com.hyrulecraft.item;

import com.hyrulecraft.block.ModBlocks;
import com.hyrulecraft.entity.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

/**
 * Hookshot — OoT-faithful grapple ported to MC.
 *
 * Behavior (from z_player.c hookshot state + En_Hookshot actor):
 *  - Raycast; if hits HOOKSHOT_TARGET tag (chain, wood, hook_target block) → pull player
 *  - If hits entity with HOOKSHOT_PULLS_ACTOR flag → pull entity toward player
 *  - Range: Hookshot 16, Longshot 32. Speed matches OoT (0.55 blocks/tick, ~11 b/s).
 *  - While retracting, player has slow-fall + can be steered with move keys.
 */
public class HookshotItem extends Item {
    private final float range;

    public HookshotItem(Settings settings, float range) {
        super(settings);
        this.range = range;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(stack);

        // Raycast for block
        Vec3d start = user.getCameraPosVec(1f);
        Vec3d dir = user.getRotationVec(1f);
        Vec3d end = start.add(dir.multiply(range));

        BlockHitResult hit = world.raycast(new RaycastContext(
                start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user));

        if (hit.getType() == HitResult.Type.BLOCK) {
            var pos = hit.getBlockPos();
            var state = world.getBlockState(pos);

            boolean isHookTarget = state.isIn(ModBlocks.HOOKSHOT_TARGET)
                    || state.isIn(ModBlocks.HOOKSHOT_PULLS_PLAYER); // wood, chain, dedicated

            if (isHookTarget) {
                // Launch projectile entity that visually draws chain and pulls on attach
                var proj = new com.hyrulecraft.entity.HookshotProjectileEntity(world, user, range);
                proj.setPosition(start.x, start.y, start.z);
                proj.setVelocity(dir.x, dir.y, dir.z, 2.2f, 0f);
                proj.setTargetPos(hit.getPos());
                world.spawnEntity(proj);

                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.PLAYERS, 1f, 1.2f);
                stack.damage(1, user, p -> p.sendToolBreakStatus(hand));
                user.getItemCooldownManager().set(this, 18);
                return TypedActionResult.success(stack);
            } else {
                user.sendMessage(net.minecraft.text.Text.literal("Hookshot needs wood/chain/hook target!").formatted(net.minecraft.util.Formatting.GRAY), true);
                world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE, SoundCategory.PLAYERS, 0.6f, 0.8f);
            }
        } else {
            // Miss — still launch for whiff anim
            var proj = new com.hyrulecraft.entity.HookshotProjectileEntity(world, user, range);
            proj.setPosition(start.x, start.y, start.z);
            proj.setVelocity(dir.x, dir.y, dir.z, 2.2f, 0f);
            world.spawnEntity(proj);
        }

        return TypedActionResult.success(stack);
    }

    public float getRange() { return range; }
}
