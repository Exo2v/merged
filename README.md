# HYRULECRAFT — Minecraft × Ocarina of Time

> **A merged game:** Minecraft's voxel sandbox inside Zelda: Ocarina of Time's adventure engine.

This repo is a **clean-room merging scaffold** for the two deconstructions you linked:

- **Minecraft:** [MaxPixelStudios/minecraftdecompiler](https://github.com/maxpixelstudios/minecraftdecompiler) — Java, Proguard/SRG/Tiny mappings → deobfuscated blocks, entities, worldgen, redstone
- **Zelda OoT:** [zeldaret/oot](https://github.com/zeldaret/oot) — C, MIPS N64 decompilation → actors, collisions, scenes, camera, audio

You can't ship Nintendo/Mojang assets, so this project does **not** embed either ROM or JAR. Instead it provides:

1. **Design & architecture** for how the two deconstructions merge at system level
2. **Bridge code** showing C ↔ Java interop patterns (OoT's actor system driving Minecraft's voxel world)
3. **Playable web prototype** (`/engine`) that *feels* like the merge — no ROM required

---

### ▶ Play the prototype (web)

```bash
python3 -m http.server 8000 --bind 0.0.0.0
# open http://localhost:8000/engine/
```

Or after `git push`, use the preview URL.

**Controls:** `WASD` move, `Mouse` look (click to lock), `Space` jump, `Shift` sprint, `LMB` break, `RMB` place, `Q/E` hotbar, `F` sword, `R` hookshot, `Z` Z-target, `B` bombs, `E` inventory, `1` pick / Minecraft mode.

### ▶ Playable Fabric mod (Minecraft as host — your pick)

You chose **Fabric as host**. The full mod is in [`mod/fabric/`](mod/fabric/):

```bash
cd mod/fabric
./gradlew build        # JDK 17 → build/libs/hyrulecraft-0.1.0-alpha.jar
./gradlew runClient    # dev client
```

Then drop the jar (+ Fabric API) in `mods/` and launch 1.20.1 with Fabric Loader 0.15.7.

**Mod controls:** `Z` Z-target lock, `R` hookshot, `O` ocarina (hold + arrows), `N` Navi hint, `F` hold for sword spin. See [`mod/fabric/README.md`](mod/fabric/README.md).

### What is merged?

| Minecraft | OoT | Merged |
|-----------|-----|--------|
| Voxel chunks 16³, blocks as IDs + metadata | Actor system (200 actors max), `Actor_Update/Draw` | **Voxel Actor:** every placed block is also a lightweight `BgActor` with collision poly |
| Inventory: 36 slots + crafting 3×3 | Inventory: 24 items + equipment + quest | Unified 8-hotbar + 4 gear slots + C-buttons |
| Redstone: block ticks, power 0-15 | Switch flags: `SWCH`, temp/perm flags | Redstone powers dungeon switches |
| Hunger / XP | Hearts / Magic | Hearts + hunger hybrid |
| First-person | Z-target third-person | Seamless toggle (scroll = zoom) |
| Generated biomes | Handcrafted scenes/rooms | **Voxel Hyrule Field** + handplaced dungeons |

### Repo layout (now with Fabric)

```
/
├── mod/fabric/        # ★ Fabric mod — Minecraft as host (your pick)
│   ├── src/main/java/com/hyrulecraft/{item,block,entity,system,client}
│   └── build.gradle — loom 1.6, MC 1.20.1, Yarn 10
├── engine/              # playable Three.js prototype (no build step)
│   ├── index.html
│   └── js/
├── docs/
│   ├── architecture.md  # technical merge: how C and Java actually talk
│   ├── mechanics.md     # gameplay merge: 5 core loops
│   └── decomp-guide.md  # legal/workflow to do real merge locally
├── src/bridge/          # example C headers + JNI stubs for real decomp merge
├── tools/mapping/       # scripts to compare actor↔entity mappings
└── spec/                # merged game spec (items, blocks, flags)
```

### Quick start for real decomp owners

See [docs/decomp-guide.md](docs/decomp-guide.md). TL;DR:

1. You must own both games legally; clone the two decomps locally (not in this repo)
2. Run `tools/mapping/analyze.py` to generate `spec/merged_registry.json`
3. Use `src/bridge/` as template for your forked OoT `src/overlays/actors` and Minecraft `net.minecraft.world.level`

### Vision teaser

> *Link awakens in a Hyrule made of blocks. The Deku Tree is dying because its roots are being eaten by a Creeper hive. You mine the first tree, craft a wooden sword, learn Z-targeting from Navi, and use the Hookshot to cross a Minecraft ravine to reach Hyrule Castle. Inside the Water Temple you place and break blocks to redirect water — and Creeper explosions become a puzzle mechanic.*

Read full vision in [docs/mechanics.md](docs/mechanics.md).

---
**License:** Code in this repo (engine + docs + bridge templates) is MIT. The decompiled game code itself remains under its original licenses (OoT: CC0-ish decomp project, Minecraft: proprietary — deobfuscated via mapping licenses LGPL/GPL). Do not commit ROMs, JARs, or extracted assets.
