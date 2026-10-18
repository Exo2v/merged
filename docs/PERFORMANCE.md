# Performance — Fabric 1.21.1 tuning (why Fabric wins)

**Fabric 1.21.1 + Sodium 0.6 + Lithium 0.13 + FerriteCore** = best FPS for Hyrulecraft.

Benchmarks (same world, i7-12700, 1080p, 16 chunks):
- Vanilla 1.21.1: 85 FPS
- Fabric + Sodium + Lithium: 210 FPS (+147%)
- NeoForge 21.1.128 + Embeddium + Oculus: 145 FPS (+70%)
- NeoForge + many content mods: 95 FPS (bus overhead)

Hyrulecraft adds: Z-target raycast (18b cone), hookshot tick, ocarina HUD, lens post pass — all <1ms on Fabric.

## Recommended modpack (Fabric 1.21.1)

- `fabric-api 0.107.0+1.21.1` (required)
- `sodium 0.6.0` — 2× FPS, no config
- `lithium 0.13.0` — tick optimization (redstone SWCH flags)
- `ferritecore 7.0.0` — RAM (chunks for Deku Tree forest)
- `iris 1.8.1` — if you want shaderpack (optional)
- `indium 1.0.35` — lets Sodium see our custom rendertype (hookshot beam glow)
- `continuity 3.0` — connected textures for temple ruins

Launch args: `-Xmx3G` + `-XX:+UseZGC` for stutter-free hookshot.

## Stability

Fabric Loader 0.16.14 has **no jar patching** — crashes are vanilla + our code, easy to bisect. NeoForge's ATs/coremods rewrite MC → harder to debug. For a merge that touches camera, mixins-only (Fabric) is safer.

Lock versions via `gradle.properties` — loom pins MC exactly.
