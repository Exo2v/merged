#pragma once
/**
 * actor_bridge.h — Actor ↔ Entity unification
 * Lets OoT's Z-target and CollisionCheck see Minecraft mobs, and vice versa.
 */

#include "world_bridge.h"

// Original OoT actor flags (from include/actor.h)
#define ACTOR_FLAG_ATTENTION_ENABLED (1 << 0)
#define ACTOR_FLAG_HOSTILE           (1 << 2)
// ... (rest in actor.h)

// Merged superset — extends Actor with MC handle
typedef struct MergedActor {
    // --- OoT base (must match Actor layout for pool allocator) ---
    // We embed the original Actor as first member so ActorContext still works
    struct Actor oot; // from actor.h — ~0x1A0 bytes

    // --- Merged extension ---
    void* mcEntityRef;      // JNI global ref to net.minecraft.world.entity.Entity, or nullptr
    int   mcEntityId;       // MC entity id (for MC-side lookup)
    int   voxelBlockId;     // if this actor IS a block (Bg_Voxel), store palette index
    bool  isVoxelProxy;     // true if auto-generated for placed block
    int   attentionPriority; // for Z-target sorting: MC creepers > OoT stals
} MergedActor;

// API called from both sides
MergedActor* MergedActor_CreateProxyForMCEntity(void* mcEntity, Vec3f* pos);
void         MergedActor_DestroyProxy(int mcEntityId);
void         MergedActor_OnMCEntityMoved(int mcEntityId, Vec3f* newPos);
void         MergedActor_SetAttention(MergedActor* a, bool enabled);

// Z-target now iterates merged pool
MergedActor* Merged_FindZTarget(struct PlayState* play, Vec3f* from, Vec3f* forward, float maxDist);

// CollisionCheck sees voxel proxies
void Merged_CollisionCheck_BtwnActors(struct PlayState* play);

// Hookshot extension — pulls toward voxel hook points (chains)
bool Merged_HookshotAttachCheck(struct PlayState* play, Vec3f* start, Vec3f* dir, float range, MergedActor** outTarget, Vec3f* outHitPos);

// Lens / Gossip stone sees sculk
bool Merged_ShouldDrawWithLens(MergedActor* actor);
