package com.hyrulecraft.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;

/** Wall-climbing bomb — runs forward 2.5s then explodes */
public class BombchuEntity extends TntEntity {
    private int ticksAlive=0;
    public BombchuEntity(EntityType<? extends TntEntity> type, World world){ super(type, world); setFuse(52); }
    public BombchuEntity(World w, net.minecraft.entity.LivingEntity owner){ super(w, owner.getX(), owner.getY(), owner.getZ(), owner); setFuse(52); }
    @Override public void tick(){
        super.tick();
        if(getWorld().isClient) return;
        ticksAlive++;
        move(MovementType.SELF, getVelocity());
        // stick to walls — simple: if on ground, keep velocity, if hits wall, climb
        if(isOnGround()) setVelocity(getVelocity().x, 0.06, getVelocity().z);
    }
}
