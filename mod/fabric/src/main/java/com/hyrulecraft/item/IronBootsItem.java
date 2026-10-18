package com.hyrulecraft.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;

public class IronBootsItem extends ArmorItem {
    public IronBootsItem(ArmorMaterial m, Type t, Settings s) { super(m, t, s); }
    // Sinking behavior handled via movement mixin — slows swim, lets you walk on bottom
}
