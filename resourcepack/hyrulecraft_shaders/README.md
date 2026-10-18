# Hyrulecraft — Iris shader pack (optional)

This `resourcepack/hyrulecraft_shaders` is a **minimal Iris/Oculus pack** that pairs with the Fabric mod.

- **What it does:** warm OoT fog tint, cracked-stone micro-glow, depth fog like Hyrule Field haze. Mod's Lens of Truth post shader (`assets/hyrulecraft/shaders/core/lens_of_truth.fsh`) runs even without this pack — this just enhances.
- **Install:** drop folder as resource pack in `resourcepacks/` + enable. Requires `Iris 1.8.1` (Fabric) or `Oculus` (NeoForge).
- **Perf:** shader cost ~0.6ms on GTX 1060; disable for potato PCs (mod works without).
- **Tweak:** edit `shaders/gbuffers_terrain.fsh` tint.

For full PBR (labPBR on hook chain, master sword), enable `Continuity` + `Indium` alongside Iris.
