#!/usr/bin/env python3
"""
codegen.py — Generate C headers from merged_registry.json

python tools/mapping/codegen.py --registry spec/merged_registry.json --out src/bridge/generated
"""
import argparse, json, pathlib

TEMPLATE_ACTOR = """#pragma once
// Auto-generated — do not edit manually. Source: merged_registry.json
#define MERGED_ACTOR_COUNT {count}
typedef enum {{
{entries}
}} MergedActorId;
"""

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--registry", required=True)
    ap.add_argument("--out", required=True)
    args = ap.parse_args()

    data = json.loads(pathlib.Path(args.registry).read_text())
    proposals = [p for p in data.get("proposals", []) if p.get("confidence",0) > 0.5]

    outdir = pathlib.Path(args.out)
    outdir.mkdir(parents=True, exist_ok=True)

    # merged_actor_ids.h
    entries = []
    for i,p in enumerate(proposals):
        sym = f"    MERGED_{p['oot'].upper()} = {i},"
        entries.append(sym + f" // -> {p['mc']} : {p['note']}")
    (outdir/"merged_actor_ids.h").write_text(TEMPLATE_ACTOR.format(count=len(proposals), entries="\n".join(entries)))

    # merged_block_ids.h — simple
    (outdir/"merged_block_ids.h").write_text("#pragma once\n// TODO: map MC block numeric ids\n#define MERGED_BLOCK_AIR 0\n")

    # jni_stubs.cpp skeleton
    jni = """#include "world_bridge.h"
#include "actor_bridge.h"
#include <jni.h>
// Auto-generated JNI stubs — implement to forward to JVM
extern "C" bool Java_com_hyrulecraft_Bridge_onVoxelChanged(JNIEnv* env, jobject, jint x, jint y, jint z) {
    // TODO: call Merged_OnVoxelChanged
    return true;
}
"""
    (outdir/"jni_stubs.cpp").write_text(jni)
    print(f"Generated {len(proposals)} merged ids in {outdir}")

if __name__ == "__main__":
    main()
