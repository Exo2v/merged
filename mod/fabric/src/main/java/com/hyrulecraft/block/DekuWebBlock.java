package com.hyrulecraft.block;

import net.minecraft.block.CobwebBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DekuWebBlock extends CobwebBlock {
    public DekuWebBlock(Settings settings) { super(settings); }
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof PlayerEntity p && p.getMainHandStack().getItem() instanceof com.hyrulecraft.item.HylianSwordItem) {
            // sword cuts — don't slow
            return;
        }
        super.onEntityCollision(state, world, pos, entity);
    }
}
