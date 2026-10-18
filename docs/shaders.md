# Shaders — Hyrulecraft (Fabric 1.21.1)

Hyrulecraft ships **two layers** of shading:

## 1) Built-in core/post shaders (no Iris needed)
Bundled in `mod/fabric/src/main/resources/assets/hyrulecraft/shaders/`:

| Shader | File | When it runs | Effect |
|--------|------|--------------|--------|
| Lens of Truth | `core/lens_of_truth.fsh` + `post/lens_of_truth.json` | Holding `hyrulecraft:lens_of_truth` (check in `HyrulecraftClient` post chain) | Grayscale + blue tint + Sobel edge outline on hidden blocks (`REACT_TO_LENS` flag). Vignette + shimmer. |
| Hookshot Beam | `core/hookshot_beam.*` | When hookshot projectile retracts | Scrolling emissive chain (UV scroll `GameTime*1.2`). |
| Z-target Reticle | `core/ztarget_reticle.*` | When `ZTargetingSystem.isLocked()` | Pulsing gold diamond (pulse `sin(GameTime*4)`). |

**Enabling:** `HyrulecraftClient` registers a post-effect pipeline on `GameRenderer` when lens is active. See `mixin/client/PostEffectMixin.java` (stub in this repo — wire to `MinecraftClient.gameRenderer.loadEffect(ResourceLocation("hyrulecraft:shaders/post/lens_of_truth.json"))`).

**Perf:** core shaders are draw-call local (<0.3ms). Post lens is 1 full-screen pass (~0.8ms at 1080p) — cheap vs Iris.

## 2) Optional Iris/Oculus shader pack
`resourcepack/hyrulecraft_shaders/` — requires `Iris 1.8.1` (Fabric) or `Oculus` (NeoForge) + `Sodium`.

- `gbuffers_terrain.fsh` — warm OoT dusk tint + cracked_stone emissive bloom. Edit tint `vec3(0.12,0.08,0.02)`.
- Compatible with `Continuity` + `Indium` for PBR metal on chains/master sword.

**Install:**
1. Put `mod/fabric/build/libs/hyrulecraft-*.jar` + `fabric-api` + `iris-fabric-1.8.1.jar` + `sodium-fabric-0.6.0.jar` in `mods/`.
2. Copy `resourcepack/hyrulecraft_shaders` to `resourcepacks/` and enable.
3. Video settings → Shader Packs → select `hyrulecraft_shaders`.

Disable for low-end: mod still works — you just lose enhanced fog/glow. Lens core shader remains.

## Tuning for performance

- **Potato:** disable Iris, keep built-ins. Add `Lithium 0.13` + `FerriteCore` (already suggested). Fabric 1.21.1 + Sodium hits 200+ FPS on vanilla.
- **Balanced:** Iris pack ON, shadowQuality 0.5x, disable post lens unless holding item (`lens_of_truth` only when holding — we already gate).
- **Cinematic:** Iris + Continuum 2.1 fork + our tint → full Hyrule Field haze.

## Adding your own
1. Copy `core/ztarget_reticle.fsh` → new `.fsh`, edit.
2. Reference in `GameRendererMixin` or `HudRenderCallback`.
3. For Iris: add to `shaders/` with same naming (`gbuffers_*`, `composite`, `final`).

See `mod/fabric/src/main/resources/assets/hyrulecraft/shaders/README.txt`.
