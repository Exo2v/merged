package com.hyrulecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HookTargetBlock extends Block {
    public HookTargetBlock(Settings settings) { super(settings); }
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) player.sendMessage(Text.literal("Hook target broken! Place a new one to cross.").formatted(Formatting.GRAY), true);
        super.onBreak(world, pos, state, player);
    }
}
