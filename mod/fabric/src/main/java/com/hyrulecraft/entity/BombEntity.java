package com.hyrulecraft.entity;

import com.hyrulecraft.block.ModBlocks;
import com.hyrulecraft.system.SwitchFlagSystem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

/**
 * Bomb — like TntEntity but with OoT timing (38 ticks fuse ≈ 1.9s) and
 * selective breaking: only SPIN_BREAKABLE / CRACKED_STONE / webs, plus sets SWCH.
 */
public class BombEntity extends TntEntity {
    private static final int FUSE = 40; // matches OoT bomb fuse

    public BombEntity(EntityType<? extends TntEntity> type, World world) {
        super(type, world);
        setFuse(FUSE);
    }
    public BombEntity(World world, LivingEntity igniter) {
        super(world, igniter.getX(), igniter.getY(), igniter.getZ(), igniter);
        setFuse(FUSE);
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient) return;
        // wobble + flash
        if (getFuse() % 6 == 0) {
            getWorld().playSound(null, getBlockPos(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 0.5f, 1.2f);
        }
    }

    @Override
    protected void explode() {
        // Custom explosion: smaller radius, only breaks tagged blocks
        World world = getWorld();
        BlockPos center = getBlockPos();

        // Visual + damage
        world.createExplosion(this, getX(), getY(), getZ(), 3.0f, World.ExplosionSourceType.TNT);

        //Selective break around radius 3.5
        int broken = 0;
        for (BlockPos p : BlockPos.iterate(center.add(-3, -2, -3), center.add(3, 2, 3))) {
            var state = world.getBlockState(p);
            if (state.isIn(ModBlocks.SPIN_BREAKABLE) || state.isIn(ModBlocks.CRACKED_STONE) || state.isOf(net.minecraft.block.Blocks.COBWEB)) {
                if (p.getSquaredDistance(center) < 12) {
                    world.breakBlock(p, true, this);
                    broken++;
                }
            }
        }
        // Set switch flags within 5b
        SwitchFlagSystem.onBombExplode(world, center, 5);

        world.playSound(null, center, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1f, 1f);
        discard();
    }
}
