Dev perf tips:
- Uncomment sodium/lithium in build.gradle modRuntimeOnly to profile runClient with them.
- Lens post shader gated: only when holding lens_of_truth — saves 0.8ms.
- Z-target HUD uses DrawContext direct (no extra pass).
- Worldgen structures use jigsaw + beard_thin — no chunk rebuild lag.
