#pragma once
/**
 * world_bridge.h — voxel-aware collision for OoT
 * Part of HYRULECRAFT merged_host.
 *
 * This file compiles against zeldaret/oot headers (actor.h, bgcheck.h)
 * and provides shims that query Minecraft's voxel world before falling back to
 * legacy CollisionPoly.
 *
 * Usage: in your forked oot/src/code/z_bgcheck.c, replace BgCheck_CheckLine calls
 * with Merged_BgCheck_CheckLine.
 */

#include <stdbool.h>
#include <stdint.h>

// Forward decls from OoT — include real headers in your build
struct PlayState;
struct CollisionPoly;
typedef struct { float x, y, z; } Vec3f;
typedef struct { int x, y, z; } Vec3i;

// Voxel world interface (implemented by Minecraft JVM side via JNI, or native voxel in prototype)
typedef struct VoxelWorld VoxelWorld;

// Block flags that map to OoT poly types
#define VOXEL_FLAG_SOLID       (1 << 0)
#define VOXEL_FLAG_WATER       (1 << 1)
#define VOXEL_FLAG_LAVA        (1 << 2)
#define VOXEL_FLAG_HOOKSHOT    (1 << 3) // chain
#define VOXEL_FLAG_LENS_HIDDEN (1 << 4)
#define VOXEL_FLAG_UNBREAKABLE (1 << 5)

// Provided by host
extern VoxelWorld* gMergedVoxelWorld;

// Query single voxel
bool Voxel_IsSolid(VoxelWorld* w, int x, int y, int z);
uint32_t Voxel_GetFlags(VoxelWorld* w, int x, int y, int z);
bool Voxel_Raycast(VoxelWorld* w, Vec3f* from, Vec3f* to, Vec3f* hitPos, Vec3i* hitBlock);
bool Voxel_GetAABB(VoxelWorld* w, int x, int y, int z, Vec3f* min, Vec3f* max);

// OoT-compat helpers
static inline bool Merged_BgCheck_CheckLineImpl(struct PlayState* play, Vec3f* posA, Vec3f* posB, Vec3f* hitPos) {
    // 1) Fast voxel raycast
    if (gMergedVoxelWorld) {
        Vec3i hitBlock;
        if (Voxel_Raycast(gMergedVoxelWorld, posA, posB, hitPos, &hitBlock)) {
            // Respect Lens of Truth — hidden blocks don't collide unless lens active
            uint32_t flags = Voxel_GetFlags(gMergedVoxelWorld, hitBlock.x, hitBlock.y, hitBlock.z);
            extern bool gLensActive;
            if ((flags & VOXEL_FLAG_LENS_HIDDEN) && !gLensActive) {
                // pretend air
            } else {
                return true;
            }
        }
    }
    // 2) Fallback to original OoT poly check
    extern bool BgCheck_CheckLineImpl(void* colCtx, Vec3f* a, Vec3f* b, Vec3f* out);
    // colCtx lives at play->colCtx
    return BgCheck_CheckLineImpl(&play, posA, posB, hitPos);
}

// Called when a Minecraft block is placed/broken — update OoT culling volume
void Merged_OnVoxelChanged(int x, int y, int z, uint32_t oldFlags, uint32_t newFlags);

// Redstone ↔ Switch flags
void Merged_RedstonePowerChanged(int x, int y, int z, int power); // 0-15 → sets SWCH flag for room
int  Merged_GetSwitchFlagForPos(int x, int y, int z); // returns OoT SWCH id or -1

// Lens helpers
bool Merged_IsBlockVisibleWithLens(int x, int y, int z);
