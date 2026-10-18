package com.hyrulecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;

public class DekuStickItem extends Item {
    public DekuStickItem(Settings settings) { super(settings); }
    @Override public UseAction getUseAction(ItemStack stack) { return UseAction.BLOCK; }
}
