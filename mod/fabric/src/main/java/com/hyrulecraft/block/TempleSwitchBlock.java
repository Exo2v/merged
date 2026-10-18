package com.hyrulecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TempleSwitchBlock extends Block {
    public static final BooleanProperty PRESSED = BooleanProperty.of("pressed");
    public TempleSwitchBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(PRESSED, false));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> b){ b.add(PRESSED); }

    @Override
    public void onSteppedOn(World world, BlockState state, BlockPos pos, Entity entity) {
        if (!world.isClient && entity instanceof PlayerEntity && !state.get(PRESSED)) {
            world.setBlockState(pos, state.with(PRESSED, true));
            com.hyrulecraft.system.SwitchFlagSystem.setSwitch(world, pos, true);
            world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, net.minecraft.sound.SoundCategory.BLOCKS, 0.6f, 0.9f);
        }
        super.onSteppedOn(world, state, pos, entity);
    }
}
