package com.hyrulecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TunicItem extends ArmorItem {
    public enum Type { KOKIRI, GORON, ZORA }
    private final Type type;
    public TunicItem(ArmorMaterial material, Type armorType, Settings settings, Type t) {
        super(material, armorType, settings);
        this.type = t;
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof PlayerEntity p)) return;
        if (p.getInventory().getArmorStack(2).isOf(this)) { // chest slot
            switch (type) {
                case GORON -> { p.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 22, 0, true, false)); if (p.isInLava()) p.setFireTicks(0); }
                case ZORA -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 22, 0, true, false));
                case KOKIRI -> {} // no bonus, but lighter
            }
        }
    }
}
