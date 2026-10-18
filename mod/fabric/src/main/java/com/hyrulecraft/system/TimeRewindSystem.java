package com.hyrulecraft.system;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * TimeRewindSystem — Song of Time rewinds last N mined blocks.
 * Stores per-player deques of broken block snapshots (pos + state). Max 64.
 */
public class TimeRewindSystem {
    private static final Map<UUID, Deque<Snapshot>> undoStacks = new HashMap<>();
    private static final int MAX = 64;

    public record Snapshot(BlockPos pos, BlockState state) {}

    public static void push(UUID player, BlockPos pos, BlockState state) {
        undoStacks.computeIfAbsent(player, k -> new ArrayDeque<>()).push(new Snapshot(pos.toImmutable(), state));
        var dq = undoStacks.get(player);
        while (dq.size() > MAX) dq.removeLast();
    }

    public static int rewind(World world, net.minecraft.entity.player.PlayerEntity player, int n) {
        var dq = undoStacks.get(player.getUuid());
        if (dq == null || dq.isEmpty()) return 0;
        int restored = 0;
        for (int i = 0; i < n && !dq.isEmpty(); i++) {
            var s = dq.pop();
            if (world.getBlockState(s.pos).isAir()) {
                world.setBlockState(s.pos, s.state);
                restored++;
            }
        }
        return restored;
    }

    // Hook: call from mixin on block break
    public static void onBlockBroken(World world, BlockPos pos, BlockState state, net.minecraft.entity.player.PlayerEntity breaker) {
        if (breaker == null) return;
        // Only player-initiated breaks go to undo (not explosions/tick)
        push(breaker.getUuid(), pos, state);
    }
}
