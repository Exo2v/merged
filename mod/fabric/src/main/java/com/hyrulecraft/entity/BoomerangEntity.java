package com.hyrulecraft.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.world.World;

public class BoomerangEntity extends ThrownEntity {
    private int age=0; private boolean returning=false;
    public BoomerangEntity(EntityType<? extends ThrownEntity> type, World w){ super(type, w); }
    public BoomerangEntity(World w, net.minecraft.entity.LivingEntity owner){ super(ModEntities.BOOMERANG, owner, w); }
    @Override protected void initDataTracker(){}
    @Override public void tick(){
        super.tick();
        if(getOwner()==null) { discard(); return; }
        age++;
        if(age>18 && !returning) returning=true;
        if(returning){
            var to=getOwner().getPos().subtract(getPos()).normalize().multiply(0.55);
            setVelocity(to);
            if(distanceTo(getOwner())<1.2) discard();
        }
    }
    @Override protected void onCollision(net.minecraft.util.hit.HitResult hit){ if(!returning) returning=true; }
}
