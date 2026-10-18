# Hyrulecraft — NeoForge 1.21.1 port (shared common code)

Fabric 1.21.1 is the **perf pick** (Sodium/Lithium, 200+ FPS, stable). This folder is a thin NeoForge wrapper that reuses the same Java systems.

## Why not default NeoForge?

- **Perf:** NeoForge's coremod/AT patching + heavier event bus → ~30% slower startup, needs Oculus+Patches for shaders. Fabric's `Mixin` only.
- **Stability:** Fabric Loader 0.16.14 has zero jar patching; NeoForge 21.1.128 rewrites MC at launch → more breakage per update.
- **Shaders:** Both support Iris/Oculus, but Fabric's Indium+Continuity give full PBR with Sodium, which NeoForge only gets via Embeddium forks.

## When to use this NeoForge build

- You depend on NeoForge-only mods (Create, FTB, etc.) and need interop.

## Build

Shared logic lives in `mod/fabric/src/main/java/com/hyrulecraft/{system,entity,block,item}` — copy or symlink here, then:

```bash
cd mod/neoforge
./gradlew build   # Java 21, NeoForge 21.1.128, MC 1.21.1
```

See `build.gradle` for NeoForge template. It depends on `mod/fabric` sources via `common` sourceSet.

## Quick port checklist

- Replace `FabricItemSettings` with `Item.Properties`
- `FabricBlockSettings` → `Block.Properties.of()`
- `ItemGroups` → `CreativeModeTabs` + `BuildCreativeModeTabContentsEvent`
- `BiomeModifications` → `BiomeModifier` (neoforge:forge/biome_modifier)
- `HudRenderCallback` → `RenderGuiEvent.Post`
- Keep `ZTargetingSystem`/`OcarinaItem` identical — they're platform-agnostic!

Full diff in `PORTING_NOTES.md`.

## Performance parity

Enable `Embeddium` + `Oculus` + `FerriteCore` on NeoForge to approach Fabric's Sodium/Lithium speeds.
