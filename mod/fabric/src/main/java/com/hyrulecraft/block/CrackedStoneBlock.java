package com.hyrulecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CrackedStoneBlock extends Block {
    public CrackedStoneBlock(Settings settings) { super(settings); }
    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        // Only bombs, hook spin, or pick with silk? For now allow pick but slower
        ItemStack tool = player.getMainHandStack();
        if (tool.isOf(com.hyrulecraft.item.ModItems.BOMB.getClass().cast(null))) return 0;
        return super.calcBlockBreakingDelta(state, player, world, pos) * 0.5f;
    }
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            // Set SWCH flag on break — used for dungeon puzzle tracking
            com.hyrulecraft.system.SwitchFlagSystem.onCrackedBroken(world, pos);
            if (world.random.nextFloat() < 0.15f) {
                player.sendMessage(Text.literal("Crack! Switch flag set — door may have opened.").formatted(Formatting.YELLOW), true);
            }
        }
        super.onBreak(world, pos, state, player);
    }
}
