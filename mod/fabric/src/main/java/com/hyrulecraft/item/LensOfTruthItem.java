package com.hyrulecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class LensOfTruthItem extends Item {
    public LensOfTruthItem(Settings settings) { super(settings); }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) user.sendMessage(Text.literal("Lens active — hidden blocks would shimmer (check cracked_stone / web within 12b)").formatted(Formatting.AQUA), true);
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
    }
}
