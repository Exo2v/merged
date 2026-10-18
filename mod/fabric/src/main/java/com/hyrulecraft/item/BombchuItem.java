package com.hyrulecraft.item;

import com.hyrulecraft.entity.BombchuEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BombchuItem extends Item {
    public BombchuItem(Settings s){ super(s); }
    @Override public TypedActionResult<ItemStack> use(World w, PlayerEntity p, Hand h){
        if(!w.isClient){
            BombchuEntity e=new BombchuEntity(w, p);
            e.setPosition(p.getX(), p.getEyeY()-0.2, p.getZ());
            e.setVelocity(p, p.getPitch(), p.getYaw(), 0f, 0.8f, 0f);
            w.spawnEntity(e);
            p.getStackInHand(h).decrement(1);
        }
        return TypedActionResult.success(p.getStackInHand(h), w.isClient);
    }
}
