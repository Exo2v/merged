package com.hyrulecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.function.Supplier;

/**
 * DekuChest — chest that requires no key but shows OoT open anim.
 * For key-locked variant see LullabyDoorBlock.
 */
public class DekuChestBlock extends ChestBlock {
    public DekuChestBlock(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityType) {
        super(settings, blockEntityType);
    }
    public DekuChestBlock(Settings settings) {
        super(settings, () -> BlockEntityType.CHEST);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            // Play OoT chest jingle + mark treasure flag
            player.sendMessage(Text.literal("♪ You got a treasure chest! Navi: Use it with Z-target next time!").formatted(Formatting.GOLD), false);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChestBlockEntity(pos, state);
    }
}
