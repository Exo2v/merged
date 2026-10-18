package com.hyrulecraft.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

/**
 * Octorok — classic OoT enemy ported as MC mob.
 * Hides in water/lilypad, pops up to shoot rock (snowball), vulnerable when open.
 * Uses HostileEntity AI but with OoT timing: hides 40 ticks, shoots, hides.
 */
public class OctorokEntity extends HostileEntity {
    private int hideTicks = 40;
    private boolean hidden = true;

    public OctorokEntity(EntityType<? extends HostileEntity> type, World world) { super(type, world); }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0) // 3 hearts = 6 HP
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.18)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(1, new SwimGoal(this));
        goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 22, 14f) {
            @Override public boolean canStart() { return super.canStart() && !hidden; }
        });
        goalSelector.add(3, new WanderAroundGoal(this, 0.6));
        goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 10f));
        targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient) return;
        if (hideTicks-- <= 0) {
            hidden = !hidden;
            hideTicks = hidden ? 50 : 30;
            if (!hidden) playSound(SoundEvents.ENTITY_FROG_TONGUE, 1f, 1f);
        }
        // When hidden takes reduced damage (shield)
    }

    public boolean isHidden() { return hidden; }
}
