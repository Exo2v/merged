package com.hyrulecraft.entity;

import com.hyrulecraft.block.ModBlocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Hookshot projectile — visually a chain tip, mechanically a pull.
 *
 * Mirrors En_Hookshot actor (z_en_hookshot.c):
 *  - flies until hits HOOKSHOT_TARGET or max range → sticks
 *  - if target has PULLS_PLAYER → retracts pulling player (lerpPos)
 *  - if target has PULLS_ACTOR → pulls entity toward player
 */
public class HookshotProjectileEntity extends ProjectileEntity {
    private Vec3d targetPos;
    private float maxRange;
    private int retractTicks = -1;
    private boolean stuck = false;
    private PlayerEntity owner;

    public HookshotProjectileEntity(EntityType<? extends ProjectileEntity> type, World world) { super(type, world); }
    public HookshotProjectileEntity(World world, PlayerEntity owner, float maxRange) {
        super(ModEntities.HOOKSHOT_PROJECTILE, world);
        this.owner = owner;
        this.maxRange = maxRange;
        this.setOwner(owner);
    }

    public void setTargetPos(Vec3d pos) { this.targetPos = pos; }

    @Override
    protected void initDataTracker() {}

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient) return;

        if (!stuck) {
            // move
            Vec3d pos = getPos();
            Vec3d vel = getVelocity();
            HitResult hit = ProjectileUtil.getCollision(this, this::canHit, this.getRaycastShapeType());
            if (hit.getType() != HitResult.Type.MISS) onCollision(hit);

            setPosition(pos.add(vel));
            // drag
            setVelocity(vel.multiply(0.997));

            // max range check
            if (owner != null && distanceTo(owner) > maxRange + 2) {
                retract();
            }
        } else {
            // retract phase — pulls
            if (retractTicks < 0) retractTicks = 0;
            retractTicks++;

            // find target block type
            var bpos = new net.minecraft.util.math.BlockPos((int)Math.floor(getX()), (int)Math.floor(getY()), (int)Math.floor(getZ()));
            var state = getWorld().getBlockState(bpos);
            boolean pullsPlayer = state.isIn(ModBlocks.HOOKSHOT_PULLS_PLAYER) || state.isIn(ModBlocks.HOOKSHOT_TARGET);

            if (owner != null && pullsPlayer) {
                // Pull player toward projectile — OoT hookshot speed = 0.5 b/tick + gravity cancel
                Vec3d to = getPos().subtract(owner.getPos()).normalize().multiply(0.65);
                to = new Vec3d(to.x, to.y + 0.22, to.z); // arc
                owner.addVelocity(to.x, to.y, to.z);
                owner.velocityModified = true;
                owner.fallDistance = 0;
                // stop when close
                if (distanceTo(owner) < 1.8) {
                    owner.setVelocity(Vec3d.ZERO);
                    owner.sendMessage(net.minecraft.text.Text.literal("Hookshot latch!").formatted(net.minecraft.util.Formatting.AQUA), true);
                    discard();
                }
            }
            if (retractTicks > 40) discard();
        }
    }

    private void retract() {
        if (!stuck) {
            stuck = true;
            setVelocity(Vec3d.ZERO);
            setNoGravity(true);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult hit) {
        super.onBlockHit(hit);
        stuck = true;
        setVelocity(Vec3d.ZERO);
        setNoGravity(true);
        setPosition(hit.getPos());
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        super.onEntityHit(hit);
        // Pull entity to player if it has HOOKSHOT_PULLS_ACTOR (e.g. Skulltula token)
        // For demo: any living entity hit is pulled toward owner
        if (owner != null) {
            var e = hit.getEntity();
            Vec3d toOwner = owner.getPos().subtract(e.getPos()).normalize().multiply(0.9);
            e.addVelocity(toOwner.x, 0.2, toOwner.z);
            e.velocityModified = true;
        }
        discard();
    }
}
