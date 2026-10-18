package com.hyrulecraft.item;

import com.hyrulecraft.entity.BoomerangEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BoomerangItem extends Item {
    public BoomerangItem(Settings s){ super(s); }
    @Override public TypedActionResult<ItemStack> use(World w, PlayerEntity p, Hand h){
        if(!w.isClient){
            BoomerangEntity b=new BoomerangEntity(w, p);
            b.setPosition(p.getEyePos());
            b.setVelocity(p, p.getPitch(), p.getYaw(), 0f, 1.1f, 0f);
            w.spawnEntity(b);
        }
        return TypedActionResult.success(p.getStackInHand(h), w.isClient);
    }
}
