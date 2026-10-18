# CI — auto-build jars on push

Push to `main` or any `arena/**` branch → GitHub builds Fabric 1.21.1 jar via JDK 21 + loom 1.8.

Artifact: `hyrulecraft-fabric-1.21.1` (in Actions tab). Tag `v*` → prerelease.

Workflow file: `.github/workflows/build.yml` (Fabric primary; NeoForge job auto-enables if `mod/neoforge/build.gradle` exists).

No local Gradle needed — just push.
