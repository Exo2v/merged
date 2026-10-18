# Worldgen — Hyrule in Minecraft 1.21.1

This mod adds **4 Jigsaw structures** (Fabric 1.21.1 data-driven) + injected features.

## Structures (locate with /locate structure)

| ID | Biome | Spacing/Separation | What is it? |
|----|-------|-------------------|-------------|
| `hyrulecraft:deku_tree` | `#hyrulecraft:has_deku_tree` → forest/jungle | 22/10 | Hollow log stump with roots (cracked_stone puzzle), chest behind. `pool: deku_tree/start` |
| `hyrulecraft:castle_ruins` | `#minecraft:is_plains` | 28/12 | Crumbling castle gate + redstone switch → lullaby door. |
| `hyrulecraft:dodongo_cavern` | `#minecraft:is_desert` | 26/9 | Underground basalt temple entrance (bury adapt). Bomb to open. |
| `hyrulecraft:kokiri_grove` | `#minecraft:is_forest` | 16/8 | 2-3 huts + lost woods gate (vegetal step). |

Edit rarity in `data/hyrulecraft/worldgen/structure_set/*.json` — increase `spacing` to rarer.

## NBTs — supply your own builds

Pools reference NBTs like `hyrulecraft:deku_tree/stump`. To use:

1. Build in creative with vanilla blocks + mod blocks (`hook_target`, `cracked_stone`, etc.).
2. Structure Block → Save (include entities ON) → saves to `world/generated/hyrulecraft/structures/`.
3. Move `.nbt` to `data/hyrulecraft/structures/hyrulecraft/<pool>/<name>.nbt` in your resource pack or this mod.
4. Reload (`/reload`) → new chunks generate it.

Starter NBTs: use `/place template hyrulecraft:deku_tree/stump` after you supply one.

## Features

- `deku_tree_patch` — random_patch of `deku_web` in forests (4/ chunk). Injected via `BiomeModifications` in `ModWorldgen.java`.
- Processor lists add variation: moss on Deku, cracked bricks on castle, basalt in Dodongo.

## Balancing for survival

- All structures use `beard_thin/box/bury` so terrain blends — no floating castles.
- Chests use `deku_chest` with `TREASURE` loot table (edit `data/hyrulecraft/loot_table/chests/*.json`).
- Switches (`temple_switch`) generate next to lullaby doors → teach redstone=SWCH.

## Commands

```
/locate structure hyrulecraft:deku_tree
/place structure hyrulecraft:deku_tree ~ ~ ~
/place template hyrulecraft:kokiri_grove/hut_1
/waypoint add DekuTree located
```

See `mod/fabric/src/main/resources/data/hyrulecraft/worldgen/` for full JSON.
