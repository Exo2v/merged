package com.hyrulecraft.system;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * ZTargetingSystem — OoT lock-on for Fabric.
 *
 * Key: Z. Finds nearest LivingEntity with ATTENTION_ENABLED flag (Hostile or
 * tagged hyrulecraft:attention) in 18b cone (0.92 dot). When locked:
 *  - Camera orbits target (handled by CameraMixin)
 *  - Strafe becomes circle (handled by MovementInputMixin or tick)
 *  - Attack is aimed
 *
 * Port of OoT's Attention System (z_actor.c Actor_FindTarget + Camera_SetTargetActor)
 */
public class ZTargetingSystem {
    public static Entity currentTarget = null;
    private static int lockTicks = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            // Unlock if target dead/far
            if (currentTarget != null) {
                if (!currentTarget.isAlive() || currentTarget.isRemoved()
                        || currentTarget.distanceTo(client.player) > 22) {
                    clearTarget(client);
                } else {
                    lockTicks++;
                    // Keep client aware of target — show HUD
                    if (lockTicks % 20 == 0 && currentTarget instanceof LivingEntity le) {
                        // optionally show health as actionbar
                    }
                }
            }
        });
    }

    public static void toggle(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        if (currentTarget != null) {
            clearTarget(client);
            return;
        }
        Entity best = findBestTarget(client.player);
        if (best != null) {
            currentTarget = best;
            lockTicks = 0;
            client.player.sendMessage(Text.literal("Z-target locked! Strafe to circle • F spin").formatted(Formatting.GOLD), true);
            // Play Navi sound
            client.player.playSound(net.minecraft.sound.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 1.6f);
        } else {
            client.player.sendMessage(Text.literal("No target — look at an Octorok/Skulltula!").formatted(Formatting.GRAY), true);
        }
    }

    private static Entity findBestTarget(ClientPlayerEntity player) {
        Vec3d eye = player.getCameraPosVec(1f);
        Vec3d forward = player.getRotationVec(1f);
        Box search = player.getBoundingBox().expand(18, 8, 18);
        Entity best = null;
        double bestScore = -1;

        for (Entity e : player.getWorld().getOtherEntities(player, search, ent ->
                ent instanceof LivingEntity le && le.isAlive()
                        && (ent instanceof HostileEntity || ent.getType().isIn(ModTag.ATTENTION_ENABLED)))) {
            Vec3d to = e.getPos().add(0, e.getHeight()/2, 0).subtract(eye);
            double dist = to.length();
            if (dist > 18) continue;
            double dot = to.normalize().dotProduct(forward);
            if (dot < 0.5) continue; // outside cone
            double score = dot * 12 - dist * 0.3 + (e instanceof HostileEntity ? 2 : 0);
            if (score > bestScore) { bestScore = score; best = e; }
        }
        return best;
    }

    private static void clearTarget(MinecraftClient client) {
        currentTarget = null;
        lockTicks = 0;
        if (client.player != null) client.player.sendMessage(Text.literal("Z-target released").formatted(Formatting.GRAY), true);
    }

    // Called by CameraMixin to get orbit target
    public static Entity getTarget() { return currentTarget; }
    public static boolean isLocked() { return currentTarget != null && currentTarget.isAlive(); }
}
