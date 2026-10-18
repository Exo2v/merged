package com.hyrulecraft.system;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * SwitchFlagSystem — bridges OoT's SWCH / TREASURE flags with Minecraft redstone.
 *
 * In OoT, each room has 8 temp + 4 perm switch flags set by stepping on switches,
 * hitting eye switches, bombing walls, etc. Here we store them as BlockPos-meta
 * in World persistent state (or just use blockstates where possible).
 *
 * Simplified for Fabric host:
 *  - Temple switch block → sets flag true → opens linked doors
 *  - Bomb explosion → sets nearby cracked/switch flags
 *  - Redstone power at door pos → treats as flag (lets MC contraptions open OoT doors)
 */
public class SwitchFlagSystem {
    public static void register() {
        // Listen for redstone updates if needed
    }

    public static void setSwitch(World world, BlockPos pos, boolean on) {
        // In full merge, write to WorldSave flag storage (see OoT WorldSave)
        // For Fabric demo, we just try to power linked doors in radius
        if (!on) return;
        // Search for lullaby doors nearby and open them
        for (BlockPos p : BlockPos.iterate(pos.add(-8, -3, -8), pos.add(8, 3, 8))) {
            var state = world.getBlockState(p);
            if (state.contains(com.hyrulecraft.block.ModBlocks.LULLABY_OPEN)) {
                world.setBlockState(p, state.with(com.hyrulecraft.block.ModBlocks.LULLABY_OPEN, true));
            }
            // Also generic: if door is closed and has POWERED, power it via redstone hack
            if (state.isOf(Blocks.IRON_DOOR) || state.isOf(Blocks.OAK_DOOR)) {
                // nudge to update
                world.updateNeighbors(pos, state.getBlock());
            }
        }
    }

    public static void onCrackedBroken(World world, BlockPos pos) {
        setSwitch(world, pos, true);
    }

    public static void onBombExplode(World world, BlockPos center, int radius) {
        setSwitch(world, center, true);
        // Break webs within radius — mirrors Bg_Spot06_Objects web break
        for (BlockPos p : BlockPos.iterate(center.add(-radius, -radius, -radius), center.add(radius, radius, radius))) {
            var s = world.getBlockState(p);
            if (s.isOf(Blocks.COBWEB) || s.isIn(com.hyrulecraft.block.ModBlocks.SPIN_BREAKABLE)) {
                if (p.getSquaredDistance(center) < radius*radius) world.breakBlock(p, true);
            }
        }
    }
}
