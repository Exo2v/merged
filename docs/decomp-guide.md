# Real Merge — Local Workflow (Legal)

> **You must own both games.** This repo contains no Nintendo or Mojang assets. Follow this guide on your own machine with legally dumped ROMs/JARs.

## 0. Prerequisites

- Linux or WSL2 Ubuntu (OoT decomp requires it)
- Python 3.11+, JDK 17+, Maven 3.9+
- `mips-linux-gnu-gcc` or `mips64-ultra-elf` toolchain
- Both deconstructions cloned *outside* this repo (sibling folders recommended):

```bash
mkdir ~/merged-workspace && cd ~/merged-workspace
git clone https://github.com/zeldaret/oot.git
git clone https://github.com/maxpixelstudios/minecraftdecompiler.git
git clone https://github.com/Exo2v/merged.git merged-host  # this repo
```

- Legally dumped OoT ROM → `oot/baseroms/gc-eu-mq-dbg/baserom.z64` (or your version)
- Vanilla Minecraft JAR (e.g., 1.20.1) downloaded via launcher

## 1. Build OoT as a relocatable library

OoT normally builds a ROM. For merging we build position-independent objects:

```bash
cd oot
make setup VERSION=gc-eu-mq-dbg -j$(nproc)
# Build GCC version (not IDO) for host linking:
make COMPILER=gcc VERSION=gc-eu-mq-dbg -j$(nproc)

# Verify
ls build/gc-eu-mq-dbg/src/code/z_actor.o
# Archive into liboot.a
find build/gc-eu-mq-dbg -name "*.o" | xargs ar rcs /tmp/liboot.a
```

Keep `src/` and `include/` — our bridge includes `actor.h`, `play_state.h`, `bgcheck.h`.

## 2. Decompile Minecraft

Use MinecraftDecompiler's CLI (VineFlower recommended):

```bash
cd minecraftdecompiler
mvn package -DskipTests
java -jar modules/cli/target/minecraft-decompiler-*.jar \
  --input ~/.minecraft/versions/1.20.1/1.20.1.jar \
  --mappings mojang \
  --decompiler VineFlower \
  --out /tmp/mc_src

ls /tmp/mc_src/net/minecraft/world/level/block
ls /tmp/mc_src/net/minecraft/world/entity/monster
```

Optional: also extract mappings as CSV for analysis:

```bash
java -jar modules/cli/target/minecraft-decompiler-*.jar \
  --input 1.20.1.jar --mappings mojang --mode map --out /tmp/mc_mappings.csv
```

## 3. Analyze overlap

This repo ships `tools/mapping/analyze.py` which reads:

- OoT `src/code/*.c` + `include/*.h` → actor list, flags, collision types
- Minecraft decompiled `net/minecraft/**/*` → block/entity registry (via parsing `Blocks.java`, `EntityType.java`)

```bash
cd merged-host
pip install pyyaml
python tools/mapping/analyze.py \
  --oot ~/merged-workspace/oot \
  --mc /tmp/mc_src \
  --out spec/merged_registry.json

cat spec/merged_registry.json | head -n 80
# outputs: unified items, actor↔entity map, block↔collision map
```

The script proposes:

- `Bg_Hidan_*` lava actors → Minecraft `lava` + `magma_block`
- `En_Kusa` (grass) → `grass_block` + drop table (rupees)
- `En_Dodongo` → `entity.dodongo` hybrid (custom)
- etc. — see `spec/block_map.yaml`.

Manually curate `spec/merged_registry.json` — accept/reject suggestions.

## 4. Generate bridge

Our bridge templates live in `src/bridge/`:

- `world_bridge.h` — voxel-aware `BgCheck`
- `actor_bridge.h` — `Actor ↔ Entity` handle
- `inventory_bridge.h` — unified inventory

Feed the registry into the codegen:

```bash
python tools/mapping/codegen.py --registry spec/merged_registry.json --out src/bridge/generated/
ls src/bridge/generated/
# merged_actor_ids.h
# merged_block_ids.h
# jni_stubs.cpp
```

## 5. Build merged_host (native)

`merged_host` is a minimal native host using GLFW + wgpu (or SDL) + JNI:

```bash
cmake -B build \
  -DOOT_ROOT=~/merged-workspace/oot \
  -DMC_SRC=/tmp/mc_src \
  -DMERGED_REGISTRY=spec/merged_registry.json

cmake --build build -j$(nproc)
./build/merged_host
```

`merged_host` will:

1. Init JVM via JNI (`JNI_CreateJavaVM`) loading Minecraft's deobfuscated classes.
2. Load `liboot.a` actors (register each overlay's init/update).
3. Open a window, start merged tick loop (see `docs/architecture.md`).

Without assets it will show a test voxel field + one Kokiri dummy — no ROM needed. With your ROM/JAR pointed via `MERGED_ASSETS=/path/to/extracted`, it loads real textures.

## 6. Legal checklist before pushing

- [ ] No `baserom*.z64` or `assets/` from ROM in Git
- [ ] No Minecraft JAR or `mc_src/` in Git (add to `.gitignore`)
- [ ] No extracted N64 textures/sounds
- [ ] Only your bridge + registry JSON + engine prototype

This repo's `.gitignore` already ignores those.

## 7. Contributing your merge

Fork `Exo2v/merged`, push to `arena/*` branch, open PR with:

- What mechanic you merged (e.g., “Hookshot as grapple + block raycast”)
- Video/GIF of prototype
- Mapping change in `spec/`

See `spec/` for schema.

---

### FAQ

**Do I need the N64 SDK?** No, `zeldaret/oot` ships its own libultra.

**Can I use Ship of Harkinian instead of raw oot?** Yes — SoH is a PC port with renderer. It’s a better host than raw `liboot.a` for graphics. Swap `liboot.a` for `libultraship`.

**What Minecraft version?** 1.20.1 is most stable for decompilation. 1.12.2 also works (more deobfuscation docs). Protocol version matters less than mapping completeness.

**Headless analysis?** Yes, `tools/mapping/analyze.py` runs without ROM/JAR — it just parses source lists.

