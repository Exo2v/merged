# Porting Fabric 1.21.1 → NeoForge 1.21.1

All core systems (Z-target, Ocarina, SwitchFlags, TimeRewind) are pure Java — copy verbatim.

Fabric → NeoForge mapping:

- `FabricItemSettings` → `Item.Properties`
- `FabricBlockSettings.copyOf(Blocks.CHAIN)` → `Block.Properties.ofFullCopy(Blocks.CHAIN)`
- `Registries.register` → `DeferredRegister` / `Registry.register` in `commonSetup` event
- `BiomeModifications.addFeature` → `AddFeaturesBiomeModifier` JSON in `data/neoforge/biome_modifier/`
- `KeyBindingHelper` → `RegisterKeyMappingsEvent`
- `ClientTickEvents.END_CLIENT_TICK` → `TickEvent.ClientTickEvent` + `RegisterClientTickEvent`
- `HudRenderCallback` → `RenderGuiEvent.Post`
- `UseBlockCallback`, `AttackBlockCallback` same; wrap via `NeoForge.EVENT_BUS`

This folder is scaffold — fill in DeferredRegisters by copying mod/fabric's ModItems/ModBlocks and swapping the above 6 lines.
