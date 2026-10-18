# Architecture — Merging OoT (C/MIPS) and Minecraft (Java)

This doc describes how you'd *actually* merge the two deconstructions if you own both legally. It's the engineering counterpart to `mechanics.md`.

## 1. The core mismatch

| Dimension | OoT (`zeldaret/oot`) | Minecraft (Java) |
|-----------|----------------------|------------------|
| Language | C, compiled for MIPS N64 (IDO/GCC), bare-metal | Java 17+, JVM, object-oriented |
| World | Rooms + scenes, `PlayState` + `ActorContext` (max 200 actors), poly + Cylinder/Sphere collision | Infinite chunks (`ChunkSource`, `LevelChunk` 16×384×16), blockstates palette, light engine |
| Tick | 20 VI (NTSC) = ~33.3ms, actors update via `Actor_UpdateAll` | 20 TPS, `ServerLevel.tick()` → block ticks → entity ticks |
| Render | GSP display lists, segment memory, N64 RSP microcode | Modern GL via Mojang's `BufferBuilder`, model bakery |
| Modding hooks | `NON_MATCHING` + overlay relocation | Mixins / Fabric, Proguard mappings |
| Data source | ROM assets (extracted via `make setup`) | JAR + data packs, via `MinecraftDecompiler` → FernFlower/CFR |

You can't ` #include "minecraft.java" ` in OoT. You need a **host**.

### Chosen host for this repo: WebAssembly + JS (prototype) → Native C++ hybrid (real)

For the prototype we run everything in JS/Three.js to avoid needing either toolchain.

For a real merge you'd pick one:

- **Option A — OoT as host (C/C++):** Port Minecraft's Java logic to C++ (like Minetest) and run inside Ship of Harkinian's PC port scaffolding. Pros: preserves OoT actor precision, N64 behavior. Cons: massive Java→C++ port.
- **Option B — Minecraft as host (Java):** Implement OoT actors as Fabric entities, hijack camera to add Z-targeting. Pros: easier distribution (mod). Cons: can't get N64 physics exactly.
- **Option C — Dual-host (recommended):** Run both loops side-by-side, bridge via shared memory. A Rust/C++ `merged_host` owns the window, loads OoT's `liboot.so` (compiled with `COMPILER=gcc`) and Minecraft's deobfuscated JAR (via JNI or GraalVM). They communicate through a flat `MergedState` struct.

```
┌─────────────┐     JNI / FFI     ┌──────────────────┐
│  Minecraft  │◄────────────────►│  MergedHost (C++) │
│  JVM / Java │   Shared arena    │  ──────────────   │
└─────────────┘                   │  OoT .so (C)     │
                                  │  PlayState*      │
                                  │  Collision poly  │
                                  └──────────────────┘
                                          │
                                    Render (wgpu/BGFX)
```

This repo's `src/bridge/` gives you the C side.

## 2. Unified concepts

### 2.1 World

OoT thinks in **rooms**; Minecraft thinks in **chunks**. Merged:

- Define `MergedChunk = 16×16×16 voxels` aligned to OoT's world scale (1 OoT unit ≈ 0.15 blocks). Store as palette (OoT collision type + Minecraft block ID).
- Generate Minecraft-style terrain but *carve* OoT scenes via `scenes.inc` look-up: Hyrule Field `=>` plains biome with OoT actor spawns; Kokiri Forest `=>` forest biome with `En_Kusa` (grass) replaced by Minecraft grass blocks.
- OoT `BgCheck` (poly collision) now queries voxel AABB first. If voxel is empty, fall back to legacy `CollisionPoly`. This lets you mine through OoT floors.

```c
// src/bridge/world_bridge.h (simplified)
bool Merged_BgCheckLineTest(void* colCtx, Vec3f* posA, Vec3f* posB) {
    if (Voxel_Raycast(posA, posB, &hit))  // fast voxel
        return true;
    return BgCheck_CheckLineImpl(colCtx, posA, posB);
}
```

### 2.2 Actors ↔ Entities

| OoT | Minecraft | Merged |
|-----|-----------|--------|
| `Actor` (struct, 0x1A0 bytes) | `Entity` (class, UUID) | `MergedActor` — C struct with Java handle |
| `Actor_Init` / `Actor_Update` / `Actor_Draw` | `Entity.tick()` / `EntityRenderer.render()` | Single vtable: `update`, `draw`, `onBlockInteract` |
| 200 max, fixed pool | unlimited, `Level.getEntities()` | Pool for OoT-faithful actors, dynamic for Minecraft extras |
| `ATTENTION_ENABLED` + `HOSTILE` flags | `TargetingConditions` + teams | Z-target now works on Creepers |

Implementation sketch:

- Keep OoT's `ActorContext` as source of truth for "Zelda objects" (chests, doors, NPCs, enemies).
- Each `Actor` gets an optional `void* mcEntityRef` (global JNI ref). When a Minecraft Creeper spawns, create a dummy OoT `EnCreeper` actor so Z-targeting/autocamera see it.
- Conversely, every placed Minecraft block gets a lightweight `Bg_Voxel` actor (cost ~32 bytes) for collision + Lens-of-Truth visibility.

### 2.3 Inventory

OoT: `Inventory_ExchangeEquipment` + `gSaveContext.inventory` (max 24 items, button mapping `C_LEFT/DOWN/RIGHT`).
Minecraft: `Inventory` 36 slots + offhand + crafting.

Merged inventory: 41 slots (`InventoryMerged`)

- Slots 0-7: hotbar (maps to OoT C-buttons + D-pad)
- Slot 8: offhand / shield (Hylian Shield)
- Slots 9-35: main
- Slots 36-39: armor → tunics/boots
- Slot 40: Ocarina (dedicated)

Crafting table becomes a 3×3 that can produce OoT items if you have the right materials (e.g., Stick+Iron = Hookshot chain).

### 2.4 Flags

OoT's global flags (`gSaveContext.weekEventReg`, `gSaveContext.permanentSceneFlags`, chest flags) now also track Minecraft achievements:

- `SWCH` (switch) `←→` redstone power: redstone dust powering a block sets `SWCH` for that room's coord hash.
- `TREASURE` (chest opened) `←→` `minecraft.opened_chest`.
- Temporarily mined blocks set `room.tempSwch` so they respawn on room reload unless you have Silk Touch + Master Sword (lore hack).

### 2.5 Time

OoT: Day/night via `gSaveContext.dayTime` + `EnvironmentContext`. Minecraft: `LevelDayTime` 0-24000.

Merge: Drive both clocks from single `mergedDayTime`. 1 OoT hour = 6 Minecraft minutes (both become 60× faster than real — keeps OoT's 2.5min day).

Sun Song / Song of Time directly sets `level.setDayTime()`.

## 3. Rendering

Prototype uses Three.js. Real merge would use one renderer (e.g., `libultraship` + `vigem` style):

- Voxels: greedy meshed, texture atlas (Minecraft's `atlas/terrain`). Use instancing.
- OoT DLs: Convert GSP display lists to modern GL via `gfx_pc` (from Ship of Harkinian). Draw after voxel opaque pass, before translucent.
- Camera: OoT's `Camera` struct lerps target; Minecraft's first-person bob is replaced when `Camera_Mode == CAM_MODE_ZTARGET`. Switching is scroll-wheel: FOV 70→45 and camera pulls to 3rd person.

## 4. Tick order (deterministic)

Every frame:

```
Input → MergedHost_PreUpdate (handle Z-target, item use)
→ Minecraft ServerLevel.tick (block ticks, redstone)
→ OoT Actor_UpdateAll (reads updated voxel collisions)
→ Collision resolution (voxel vs OoT poly)
→ Camera_Update (OoT camera wins if locked on)
→ Render
```

This ensures you can place a block under a rolling Goron and it instantly becomes a step.

## 5. Toolchain steps (real)

1. Build OoT for host: `make COMPILER=gcc VERSION=gc-eu-mq-dbg -j` → `build/*/oot*.elf`, extract `liboot.a`.
2. Decompile Minecraft: use `MinecraftDecompiler` CLI:

   ```bash
   java -jar MinecraftDecompiler.jar --input 1.20.1.jar --mappings mojang --decompiler VineFlower --out mc_src/
   ```
3. Run `tools/mapping/analyze.py` to map `z_actor.c:ACTOR_FLAG_*` ↔ `net.minecraft.world.entity.*` and `bgcheck.h` ↔ `LevelChunkSection`.
4. Codegen `src/bridge/merged_registry.json` → auto-generate JNI stubs.
5. Build host:

   ```
   cmake -DOOT_ROOT=/path/to/oot -DMC_SRC=/path/to/mc_src
   make merged_host
   ```

Read `docs/decomp-guide.md` for step-by-step with WSL.

## 6. Why not just mod one game?

You could ship a Fabric mod that *feels* like OoT (Z-targeting mod already exists). You could ship an OoT randomizer that adds block-placement. But the merged engine lets you keep **both physics faithfully**: Minecraft redstone timing *and* OoT's precise actor movement, which fans demand for speedrun fidelity.

If you only want one side, use:

- OoT host if you care about OoT glitches (wrong warp, bomb hover — needs N64 poly precision)
- Minecraft host if you care about farm optimization (needs block tick determinism)

---

Next: see `docs/mechanics.md` for player-facing loops.
