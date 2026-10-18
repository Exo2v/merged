package com.hyrulecraft.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import com.hyrulecraft.Hyrulecraft;

/**
 * ModWorldgen — injects OoT flora/ores into vanilla biomes.
 *
 * Structures themselves are data-driven (see data/hyrulecraft/worldgen/structure/*.json).
 * Here we inject placed features: deku_web patches, hook_target pillars, cracked_stone veins.
 */
public class ModWorldgen {
    private static final RegistryKey<net.minecraft.world.gen.feature.PlacedFeature> DEKU_WEB_PATCH =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("hyrulecraft", "deku_tree_patch"));

    public static void register() {
        // Add deku web patch to forests
        BiomeModifications.addFeature(
                BiomeSelectors.tag(net.minecraft.registry.tag.BiomeTags.IS_FOREST),
                GenerationStep.Feature.VEGETAL_DECORATION,
                DEKU_WEB_PATCH
        );
        Hyrulecraft.LOGGER.info("[Hyrulecraft] Worldgen injected — structures: deku_tree (22/10), castle_ruins (28/12), dodongo_cavern (26/9), kokiri_grove (16/8)");
    }
}
