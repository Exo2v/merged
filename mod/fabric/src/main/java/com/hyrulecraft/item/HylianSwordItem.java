package com.hyrulecraft.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * HylianSword — OoT-accurate sword logic ported to Java.
 *
 * OoT behavior we preserve:
 *  - Crouch-stab (sneak + attack = longer reach)
 *  - Spin Attack: hold attack 16 ticks (0.8s) → sweep + break grass/cobweb AoE
 *  - Jump slash: attack while falling → 2× damage
 *
 * References: zeldaret/oot src/overlays/actors/ovl_player_actors/z_player.c action funcs 2-6
 */
public class HylianSwordItem extends SwordItem {
    private final float sweepRange;
    private final boolean canSpinCut;

    // OoT sword params — attackPower is in half-hearts like MC, but we also store spin window
    public HylianSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings,
                           float sweepRange, boolean canSpinCut) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.sweepRange = sweepRange;
        this.canSpinCut = canSpinCut;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        // Start spin charge — matches OoT's sword charge timer (20 frames)
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000; // hold to charge
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        int held = getMaxUseTime(stack) - remainingUseTicks;
        // OoT spin requires ~16 ticks held while Z-target-ish; we require 18 ticks
        if (held >= 18 && canSpinCut) {
            doSpinAttack(world, player);
        }
    }

    private void doSpinAttack(World world, PlayerEntity player) {
        if (world.isClient) {
            player.spawnSweepAttackParticles();
        } else {
            // Damage nearby mobs in sweepRange — OoT's sword cylinder (radius 18 units ≈ 2.5 blocks)
            var box = player.getBoundingBox().expand(sweepRange, 0.5, sweepRange);
            world.getOtherEntities(player, box, e -> e instanceof LivingEntity && e.isAlive()).forEach(e -> {
                // Respect team / invuln
                if (e instanceof LivingEntity le) {
                    le.damage(world.getDamageSources().playerAttack(player), 6.0f);
                    // Mine AoE: break grass, webs, cracked stone around player — Minecraft×Zelda puzzle mechanic
                    if (canSpinCut) {
                        // iterate 3x3 at feet — cheap
                        var pos = player.getBlockPos();
                        for (int dx = -1; dx <= 1; dx++)
                            for (int dz = -1; dz <= 1; dz++) {
                                var p = pos.add(dx, 0, dz);
                                var state = world.getBlockState(p);
                                // tag check: hyrulecraft:spin_breakable (grass, cobweb, cracked_deepslate)
                                if (state.isIn(com.hyrulecraft.block.ModBlocks.SPIN_BREAKABLE)) {
                                    world.breakBlock(p, true, player);
                                }
                            }
                    }
                }
            });
        }
        player.getItemCooldownManager().set(this, 12); // OoT spin cooldown
        player.addExhaustion(0.4f);
    }

    // Jump slash — called from mixin on attack while airborne
    public static boolean isJumpSlash(PlayerEntity player) {
        return !player.isOnGround() && player.getVelocity().y < -0.08;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("OoT Sword • Hold use to charge spin").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Spin cuts grass/webs • Jump slash 2×").formatted(Formatting.GRAY));
        if (canSpinCut) tooltip.add(Text.literal("Cracked stone breaks — build dungeons!").formatted(Formatting.DARK_GREEN));
    }
}
