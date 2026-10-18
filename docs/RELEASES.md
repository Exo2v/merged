# Releases — 1-click jar

**Current 1-click (dev, sandbox-limited):**
- Raw jar (71KB, inspect): https://raw.githubusercontent.com/Exo2v/merged/arena/2fe62dfd-merged/mod/fabric/build/libs/hyrulecraft-0.2.0-1.21.1-dev.jar
- Full zip (jar+shaderpack+docs): https://raw.githubusercontent.com/Exo2v/merged/arena/2fe62dfd-merged/hyrulecraft-1.21.1-release.zip

**After this push → real Release (remapped, runnable):**
- Wait for `release` workflow on tag `v0.2.0-1.21.1` → https://github.com/Exo2v/merged/releases
- There you'll see **1-click Assets:** `hyrulecraft-0.2.0-1.21.1.jar` (remapped, Java 21, Fabric 1.21.1 0.19.5+loom 1.18) + zip
- Install: Fabric Loader 0.19.5 + Fabric API 0.116.17+1.21.1 + Sodium/Iris optional → drop jar in `mods/` → launch 1.21.1

Why two steps? Sandbox allow-list blocks `uploads.github.com` so `gh release upload` fails here. The `release.yml` workflow runs on GitHub's runner (open network) and can upload to Releases.

To cut a new 1-click release locally:
```bash
git tag v0.2.0-1.21.1
git push origin v0.2.0-1.21.1
# → Actions → release → Releases page → download jar
```
