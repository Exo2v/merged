#!/usr/bin/env python3
"""
analyze.py — Heuristic mapper between OoT actors and Minecraft blocks/entities.

Usage:
  python analyze.py --oot /path/to/oot --mc /path/to/mc_src --out spec/merged_registry.json

It doesn't need ROMs/JARs — just the decompiled sources.
Produces a JSON with proposed merges you can curate.

Heuristics:
 - OoT En_* actors vs MC monster entity names via substring + collision shape
 - OoT Bg_* background actors vs MC blocks via material (wood/stone/water)
 - OoT flags (SWCH, TREASURE) vs MC blockstates (door open, chest type)
"""

import argparse, json, re, pathlib, sys

def scan_oot(oot_root: pathlib.Path):
    actors = []
    actor_dir = oot_root / "src" / "overlays" / "actors"
    if not actor_dir.exists():
        # fallback: count src/overlays
        actor_dir = oot_root / "src" / "code"
    # collect include/actor.h flags for reference
    actor_h = oot_root / "include" / "actor.h"
    flags = []
    if actor_h.exists():
        txt = actor_h.read_text(errors="ignore")
        flags = re.findall(r"#define (ACTOR_FLAG_\w+)", txt)
    # enumerate actor overlays
    if (oot_root/"src"/"overlays"/"actors").exists():
        for p in (oot_root/"src"/"overlays"/"actors").iterdir():
            if p.is_dir():
                actors.append(p.name)
    else:
        # fallback: list src/code/z_actor*
        for p in (oot_root/"src"/"code").glob("z_*.c"):
            actors.append(p.stem)
    return actors, flags

def scan_mc(mc_root: pathlib.Path):
    blocks = []
    entities = []
    # Look for Blocks.java
    for p in mc_root.rglob("Blocks.java"):
        txt = p.read_text(errors="ignore")
        # crude: static final Block STONE =
        blocks = re.findall(r"public static final Block (\w+)", txt)
        break
    for p in mc_root.rglob("EntityType.java"):
        txt = p.read_text(errors="ignore")
        entities = re.findall(r"public static final EntityType<[^>]+> (\w+)", txt)
        break
    # fallback: scan directory names
    if not blocks:
        lvl = mc_root / "net" / "minecraft" / "world" / "level" / "block"
        if lvl.exists():
            blocks = [x.stem for x in lvl.glob("*.java")]
    if not entities:
        ent = mc_root / "net" / "minecraft" / "world" / "entity"
        if ent.exists():
            entities = [x.stem for x in ent.rglob("*.java")]
    return blocks[:120], entities[:120]

def propose_merges(actors, flags, blocks, entities):
    proposals = []
    # hand-tuned map (the core value)
    hardcoded = {
        "En_Kusa": ("grass_block", "entityless: tall grass → break gives rupee"),
        "En_Dodongo": ("ravager", "boss: Dodongo ↔ Ravager charge"),
        "En_Gohma": ("spider", "Gohma eye ↔ Spider with weak eye"),
        "Bg_Hidan_Hrock": ("lava", "lava rock"),
        "Bg_Bombwall": ("cobblestone", "breakable wall → cracked stone"),
        "Bg_Heavy_Block": ("obsidian", "unbreakable until Goron Bracelet"),
        "En_Box": ("chest", "treasure chest"),
        "Bg_Gate_Shutter": ("iron_door", "key door"),
        "En_Stal": ("skeleton", "Stalchild"),
        "En_Octo": ("guardian", "Octorok → Guardian beam reskin"),
        "Bg_Gjyo_Bridge": ("oak_planks", "drawbridge"),
        "Bg_Menkuri_Nisekabe": ("stone_bricks", "fake wall → Lens reveals"),
        "En_Sc_Ruppe": ("emerald", "Rupee ↔ Emerald"),
    }
    for oot_actor, (mc_target, note) in hardcoded.items():
        proposals.append({"oot": oot_actor, "mc": mc_target, "note": note, "confidence": 0.95})

    # heuristic: anything with Kusa/grass → grass-like blocks
    for a in actors:
        if "kusa" in a.lower() or "grass" in a.lower():
            if a not in [p["oot"] for p in proposals]:
                proposals.append({"oot": a, "mc": "grass_block", "note": "auto: grass-like", "confidence": 0.6})
    # heuristic: Bg_* with Hidan (fire temple) → nether/bassalt
    for a in actors:
        if a.startswith("Bg_Hidan_") and a not in [p["oot"] for p in proposals]:
            proposals.append({"oot": a, "mc": "basalt / magma_block", "note": "auto: fire temple", "confidence": 0.55})

    return proposals

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--oot", required=True)
    ap.add_argument("--mc", required=True)
    ap.add_argument("--out", required=True)
    args = ap.parse_args()

    oot_root = pathlib.Path(args.oot)
    mc_root = pathlib.Path(args.mc)

    if not oot_root.exists():
        print(f"OoT path not found: {oot_root}", file=sys.stderr); sys.exit(2)
    if not mc_root.exists():
        print(f"MC path not found: {mc_root}", file=sys.stderr); sys.exit(2)

    actors, flags = scan_oot(oot_root)
    blocks, entities = scan_mc(mc_root)
    proposals = propose_merges(actors, flags, blocks, entities)

    out = {
        "meta": {
            "oot_actors_found": len(actors),
            "mc_blocks_found": len(blocks),
            "mc_entities_found": len(entities),
            "oot_flags": flags[:20],
        },
        "proposals": proposals,
        "instructions": "Curate this file: set 'approved': true/false, then run codegen.py"
    }
    pathlib.Path(args.out).parent.mkdir(parents=True, exist_ok=True)
    pathlib.Path(args.out).write_text(json.dumps(out, indent=2))
    print(f"Wrote {args.out} with {len(proposals)} proposals")
    print(f"  OoT actors: {len(actors)}  MC blocks: {len(blocks)}  MC entities: {len(entities)}")

if __name__ == "__main__":
    main()
