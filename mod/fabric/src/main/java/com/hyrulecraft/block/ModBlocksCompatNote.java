// Compatibility note: FabricBlockSettings.copyOf() still works in 1.21.1.
// If you see deprecation, replace with Block.Settings.copy(Blocks.CHAIN) + .sounds(BlockSoundGroup.CHAIN)
// Yarn 1.21.1+build.3 maps FabricBlockSettings → Block.Settings for 1.21.1.
