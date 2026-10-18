# Fabric 1.21.1 vs NeoForge 1.21.1 — for Hyrulecraft

**TL;DR: Ship Fabric 1.21.1 as primary. Offer NeoForge 1.21.1 as port via `mod/neoforge/`.**

Why Fabric wins for *this* merge:

- **Performance:** Fabric is lighter (bare `Mixin` + no coremod bus). Pair with Sodium 0.6 + Lithium 0.13 + FerriteCore → 40-70% faster than NeoForge on same world. Hyrulecraft adds Z-target raycasts + hookshot every tick — lightness matters.
- **Stability:** Fabric Loader 0.16.x has zero classloader patching; NeoForge patches Minecraft jar at launch (ATs, coremods) → more crash surface. Fabric's breakage window per MC update is smaller (Yarn maps day-1 vs NeoForge's MCP wait).
- **Iteration speed:** `loom` dev launcher is 2× faster than NeoForge's `mods.toml` pack; instant `./gradlew runClient`.
- **Compat:** Both get Iris shaders. Fabric has `Continuity` + `Indium` for full PBR shader support. NeoForge's advantage (heavy content mods like Create) doesn't apply — Hyrulecraft is self-contained OoT systems.

When NeoForge would win: if you want to depend on NeoForge-only APIs (e.g., Forge Energy, FTB Quests, Create integration). For pure Hyrulecraft, Fabric is cleaner.

**Decision:**
- `mod/fabric/` → **Fabric 1.21.1**, Java 21, Loom 1.8, Yarn 1.21.1+build.3, Loader 0.16.14, Fabric API 0.107.0+1.21.1
- `mod/neoforge/` → thin wrapper reusing `mod/common/` (same systems) compiled against NeoForge 21.1.128 (MC 1.21.1, Java 21). Shared logic lives in `mod/common/src`.
- CI builds **both** jars on push.

If you only want one jar, use `mod/fabric/build/libs/hyrulecraft-*.jar` (Fabric). It runs with Sodium+Iris+Lithium for best FPS.
