# Worldgen — Hyrulecraft structures

These are data-driven Jigsaw structures. They spawn via `structure_set` random_spread.

- **deku_tree** — forest/jungle, spacing 22, salt 420420. Pool `hyrulecraft:deku_tree/start` → NBT `hyrulecraft:deku_tree/stump`.
- **castle_ruins** — plains, spacing 28, beard_box (sinks into terrain).
- **dodongo_cavern** — desert/mesa underground, terrain bury.
- **kokiri_grove** — forest vegetal, huts + lost woods gate.

NBTs: place your `.nbt` in `data/hyrulecraft/structures/hyrulecraft/{deku_tree,castle_ruins,...}/`. Use Structure Block (or WorldEdit) to save. The JSON here will then load them.

For quick test without NBTs, the pools fallback to `minecraft:empty` — you can still force-place via:

  /place structure hyrulecraft:deku_tree

Or use `/locate structure hyrulecraft:deku_tree`.

Processor lists add moss/crack/ basalt variation (see `processor_list/`).

To tweak rarity: edit `structure_set/*.json` spacing/separation.

See docs/worldgen.md for full guide.
