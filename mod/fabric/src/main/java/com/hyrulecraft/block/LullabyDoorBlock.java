package com.hyrulecraft.block;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LullabyDoorBlock extends DoorBlock {
    public static final BooleanProperty LULLABY_OPEN = com.hyrulecraft.block.ModBlocks.LULLABY_OPEN;

    public LullabyDoorBlock(Settings settings) {
        super(settings, BlockSetType.IRON);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, net.minecraft.util.math.Direction.NORTH)
                .with(OPEN, false).with(HINGE, DoorHinge.LEFT)
                .with(POWERED, false).with(HALF, DoubleBlockHalf.LOWER)
                .with(LULLABY_OPEN, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LULLABY_OPEN);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!state.get(LULLABY_OPEN)) {
            player.sendMessage(net.minecraft.text.Text.literal("♪ This door needs Zelda's Lullaby! Play it on the Ocarina.").formatted(net.minecraft.util.Formatting.LIGHT_PURPLE), true);
            return ActionResult.FAIL;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    // Called by OcarinaItem when lullaby played
    public static void openNearby(World world, BlockPos center) {
        for (BlockPos p : BlockPos.iterate(center.add(-5, -2, -5), center.add(5, 2, 5))) {
            BlockState s = world.getBlockState(p);
            if (s.contains(LULLABY_OPEN) && !s.get(LULLABY_OPEN)) {
                world.setBlockState(p, s.with(LULLABY_OPEN, true).with(OPEN, true));
            }
        }
    }
}
