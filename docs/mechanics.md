# Mechanics — Five Merged Loops

> The design bible for *Hyrulecraft*. Written as if the game shipped.

## Elevator pitch

It's 1998. Link leaves Kokiri Forest and discovers the world is made of blocks. The Great Deku Tree is sick because its interior has become a Minecraft cave system. The only way to save Hyrule is to learn two verbs: **Z-Target** and **Mine**.

Minecraft gives you **emergence** (break anything, build anything, redstone). OoT gives you **adventure** (hand-crafted dungeons, lock-on combat, ocarina magic). The merge makes each more meaningful.

---

## Loop 1 — Explore a voxel Hyrule Field

**Minecraft rule:** world is infinite, generated.
**OoT rule:** world is bounded, memorable (Hyrule Field → Kakariko → Death Mountain).

**Merge:** Hyrule Field is a 2048×2048 block “island” (−1024 to +1024) surrounded by endless Minecraft biomes. Inside the island, terrain is *hand-edited voxels* that match OoT's heightmap; outside, it's vanilla Minecraft generation. You can walk from Castle Town into a normal Minecraft plains forever.

- **Chunk = Room:** Each 16×16 chunk loads its OoT `Scene` actors on demand. If you break a chunk's dungeon entrance, you literally open the dungeon.
- **Fast travel:** Ocarina songs now *write* blocks: Prelude of Light places a warp pedestal (3×3 obsidian) you can mine and move.
- **Horses:** Epona is a Minecraft horse with OoT controls (Z-target dismount, carrot on stick = carrot stick).
- **Example moment:** You’re lost in Lost Woods. Instead of following Saria’s Song, you pillar up 20 blocks to scout. Navi screams “Hey! That’s cheating!” — then admits it’s useful.

## Loop 2 — Mine dungeons, build solutions

Classic OoT puzzle: push block onto switch. Classic Minecraft puzzle: build bridge across lava.

In merged dungeons, both are true:

- **Rooms are voxels** but rules remain. You can't just break the boss door — it's an unbreakable `Bg_Gate_Shutter` actor with `MASS_IMMOVABLE`. Lens of Truth reveals which blocks are breakable (cracked texture).
- **Dungeon themes merge biomes:**
  - *Deku Tree* → Lush Cave + moss + vines; you mine roots choking the heart.
  - *Dodongo's Cavern* → Basalt + lava; bombs are throwable TnT (OoT Bomb model, Minecraft blast radius).
  - *Water Temple* → waterlogged state everywhere; iron boots = heavy armor that lets you walk underwater but you sink through soul sand.
  - *Shadow Temple* → sculk + Lens of Truth = spyglass that reveals invisible poly collision.
- **Build solves:** A switch too high? Pillar up. Chasm? Place blocks. But you have limited blocks per room (dungeon inventory) — inspired by Minecraft’s adventure mode. Finding the Dungeon’s Crafting Table unlocks that room’s build budget.

> **Signature puzzle:** *“Gold Skulltula Hive”* — A wall of webs. You can burn with Deku Stick (OoT) or shear with shears (MC). Behind it, a void with Hookshot targets floating as Minecraft chains. You must place a block mid-air (with precise OoT jump) to reach the token.

## Loop 3 — Combat: Z-target meets creepers

- **Z-target** (`Z`) locks onto nearest attention-enabled entity within 15 blocks — now including Minecraft mobs. Reticle color: yellow = friendly, red = hostile.
- While Z-targeting, strafe = circle, roll = Minecraft sprint+Ctrl (with i-frames), backflip = block-place-parry.
- **Sword:** Wood→Kokiri, Stone→Goron Knife, Iron→Master, Diamond→Biggoron. Charging spin attack breaks all adjacent dirt/cobble (aoe mining).
- **Shield:** Hold `RMB` without target = Minecraft shield; with target = OoT shield parry window (6f).
- **Creeper + Bomb:** You can pick up a lit Creeper with the Goron Bracelet and throw it (OoT pickup actor + Minecraft fuse). Explosions destroy voxels and also trigger OoT `SWCH`.
- **Boss example — King Dodongo (voxel):** Arena is a 30×30 pit. King Dodongo inhales and exhales fire (Minecraft breath = warden sonar). Throw a bomb (crafted TnT + OoT Bomb flower) into his mouth when he opens. His stomp cracks the floor — new ores exposed each phase.

## Loop 4 — Craft & equip progression

Unified crafting:

| Result | Recipe | Unlock |
|--------|--------|--------|
| Kokiri Sword | 1 Stick + 1 Flint + 1 Vine | Start |
| Fairy Bow | 3 String + 3 Stick + 1 Rupee (emerald) | Deku Seeds |
| Hookshot | 1 Chain + 1 Iron Ingot + 1 Hook + 1 Redstone | Dodongo |
| Bomb Bag (holds Gunpowder) | 1 Leather + 3 Gunpowder | Goron Ruby |
| Ocarina | 1 Clay + 5 Note Blocks | Ocarina of Time (story) |
| Master Sword | 1 Nether Star + Hookshot + Kokiri Sword on Pedestal | Time travel |

- **Rupees = Emeralds** (retex). Hearts are craftable from `Red Mushroom + Gold Ingot` but drop from grass.
- **Tunic = Armor set:** Kokiri Tunic = leather, Goron Tunic = iron + fire_res, Zora Tunic = chain + water breathing. Wearing full set enables OoT tunics' abilities.
- **Songs are schematics:** Learning a song stores a structure you can paste with Ocarina (costs magic/lapis). Song of Time = 3×3×3 cube rewinds (restores last 5 broken blocks).

## Loop 5 — Music & time

- Ocarina is held with both hands (third-person anim), 5 notes mapped to `C-up/down/left/right + A`. Playing correct sequence previews song name.
- **Song effects are voxel-aware:**
  - Zelda's Lullaby — opens marked doors (sets flag) + grows nearby crops instantly.
  - Epona's Song — summons nearest horse, even through walls.
  - Sun's Song — sets time to noon/midnight (directly calls `level.setDayTime()`).
  - Song of Storms — thunderstorm + washes away sand.
  - Scarecrow's Song — player-recordable, places scarecrow actor that scares phantoms.
- **Day/night matters:** Minecraft phantoms + OoT Stalchildren share spawn table at night on Hyrule Field. Day 0-3 is child Link (small hitbox, can mine faster, throw shorter), day 4+ is adult (hookshot 3× range, heavy blocks).

---

## Onboarding (first 30 mins)

1. **Kokiri Forest (tutorial):** You spawn as child Link with empty hands. Navi teaches Z-target on a dummy. Mido blocks path — you learn to mine 3 logs, craft wooden sword, chop Mido's fence.
2. **Deku Tree (dungeon 1):** Inside is a lush cave. You get Slingshot (bow with seeds). Boss Gohma is a spider + eye; shooting eye stuns, then you mine its legs.
3. **Hyrule Field (open):** On exit, you receive the infamous prompt: *“Would you like to punch this tree? Yes / Navi says No”*. Punching starts your Minecraft journey; you get a pickaxe from Business Scrub (emerald trade).
4. **Kakariko & crafting:** You learn rupee/emerald trade; first dungeon switch puzzle requiring redstone.

## Balancing principles

- **Never break Zelda:** Story doors, boss keys, and ocarina triggers remain unbreakable; else speedrunners softlock.
- **Never neuter Minecraft:** Outside dungeons, all blocks breakable. Inside dungeons, 30% of blocks breakable — enough to be creative, not enough to skip.
- **One economy:** Rupees = emeralds = XP for enchanting your Master Sword (Sharpness = Giant's Knife).
- **Keep quirks:** OoT glitches (ISG, bomb hover) work *because* voxels give new surfaces. Minecraft dupes work inside dungeons too — embrace.

---

See `spec/items.yaml` for all 42 merged items + recipes.
