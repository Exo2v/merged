# Hyrulecraft — Fabric Mod (Minecraft as Host)

> The **playable** route: Fabric loads Minecraft normally, and we inject OoT's actor/camera/inventory systems as Java.

This is the mod folder you asked for. It compiles for **Minecraft 1.20.1 + Fabric Loader 0.15.7 + Fabric API 0.92**.

## Quick start

```bash
cd mod/fabric
# (needs JDK 17 — see https://adoptium.net)
./gradlew build          # outputs build/libs/hyrulecraft-0.1.0-alpha.jar
./gradlew runClient      # dev client with mod
```

Copy the jar + `fabric-api` to your real `mods/` and launch via Prism / vanilla launcher with Fabric.

> **No ROM/JAR needed** — this is clean-room reimplementation referencing the decomp docs, not copying.

## Controls (in-game)

| Key | OoT | Does |
|-----|-----|------|
| **Z** | Z-target | Lock nearest attention-mob (Hostile + `hyrulecraft:attention_enabled`). Strafe becomes circle. Press again to unlock. |
| **R** | Hookshot | Fire held hookshot/longshot. Hits `hookshot_target` tag (chain, oak log, hook_target block) → pulls you. Hits mob → pulls it. |
| **F** | Sword sweep | Hold use on Kokiri/Master sword 18 ticks → spin (breaks `spin_breakable` = webs/grass/cracked). Jump + attack = 2×. |
| **O** | Ocarina | Hold `O` + `WASD` (+ `Z` for A) to play notes to buffer; release to match songs. |
| **N** | Navi | Hint: cracked stone, Skulltula webs, etc. |
| **B** (item) | Bomb | Throw timed bomb (40-tick fuse). Blast only breaks tagged blocks + sets `temple_switch`. |

Songs: `Zelda's Lullaby (L U R …)`, `Sun's Song (R D U)`, `Song of Time (R A D)`, `Song of Storms (A D U)`, `Epona's Song (U L R)`, `Saria's Song (D R L)`.

## Items → OoT mapping

| Hyrulecraft | Vanilla base | OoT source |
|-------------|--------------|------------|
| Kokiri Sword | `wooden_sword` | `ITEM_SWORD_KOKIRI` |
| Master Sword | `iron_sword` (fireproof) | `ITEM_SWORD_MASTER`, glows near sculk |
| Hookshot/Longshot | crossbow base | `ACTOR_FLAG_HOOKSHOT_*`, 16/32 range |
| Hylian Shield | `shield` | `ITEM_SHIELD_HYLIAN` |
| Bomb/Bombchu | `tnt` | `ITEM_BOMB` fuse 38→40t |
| Fairy Bow/Slingshot | `bow` | `ITEM_BOW` |
| Ocarina | `goat_horn` | `ITEM_OCARINA_TIME` |
| Deku Stick/Nut | `stick` / flash | `ITEM_STICK/NUT` |
| Tunics/Boots | armor | `ITEM_TUNIC_GORON/ZORA`, `IRON_BOOTS/HOVER_BOOTS` |

Recipes in `src/main/resources/data/hyrulecraft/recipes/` — edit datapacks without recompiling.

## Blocks → Actor mapping

| Hyrulecraft block | OoT actor | MC tag |
|-------------------|-----------|--------|
| `hook_target` | `Bg_Gjyo_Bridge` hook point | `hookshot_target` |
| `deku_chest` | `En_Box` chest | — |
| `cracked_stone` | `Bg_Bombwall` | `cracked_stone`, bomb/spin only |
| `lullaby_door` | `Bg_Gate_Shutter` (locked) | needs `LULLABY_OPEN=true` via song |
| `temple_switch` | `SWCH` flag | redstone power ↔ switch |
| `deku_web` | Skulltula web | sword/fire breaks |
| `spin_breakable` tag | `MASS_HEAVY` | spin/bomb breaks |
| `attention_enabled` entity tag | `ACTOR_FLAG_ATTENTION_ENABLED` | Z-targetable |

## Entities (port of OoT overlays)

- **Octorok** — hides then shoots (see `z_en_octorok.c`)
- **Gold Skulltula** — wall-cling, drops token
- **Bomb / Bombchu / Hookshot Projectile / Boomerang** — throwable helpers

Add variants by extending `HostileEntity` and tagging `attention_enabled`.

## Systems

- **`ZTargetingSystem`** — find best cone target, lock, orbit camera via `CameraMixin`/`GameRendererMixin`.
- **`SwitchFlagSystem`** — bomb/switch → open nearby `lullaby_door`, update redstone.
- **`TimeRewindSystem`** — Song of Time restores last N broken blocks (undo stack per player).
- **`OcarinaItem`** — note buffer → song → effect.

## Folder map

```
mod/fabric/
  build.gradle / gradle.properties  — loom 1.6, MC 1.20.1, Yarn 10, JDK 17
  src/main/java/com/hyrulecraft/
    Hyrulecraft.java                — main entry
    item/                           — swords, hookshot, ocarina, bombs...
    block/                          — chest/door/switch/web/target
    entity/                         — octorok, skulltula, projectiles
    system/                         — Z-target, switch flags, time rewind
    mixin/                          — LivingEntity (jump slash), Camera
    client/HyrulecraftClient.java   — keybinds + HUD
  src/main/resources/
    fabric.mod.json                 — mod manifest
    assets/hyrulecraft/lang/en_us.json
    data/hyrulecraft/{recipes,tags}
```

## Next steps for you

1. `gradle build` → test `runClient` → Z-target a zombie.
2. Drop a `hook_target` block over a ravine, `R` to cross — that's the Zelda×MC bridge puzzle.
3. Build `cracked_stone` temple rooms + `temple_switch` behind them → bomb to open door.
4. Place `lullaby_door`, test `ocarina` lullaby to open.
5. Expand: add `Epona` horse reskin, `Lens of Truth` overlay, structure generation for Kokiri Forest.

PRs welcome — see `docs/architecture.md` for the full dual-host plan.
