package com.hyrulecraft;

import com.hyrulecraft.block.ModBlocks;
import com.hyrulecraft.entity.ModEntities;
import com.hyrulecraft.item.ModItems;
import com.hyrulecraft.system.SwitchFlagSystem;
import com.hyrulecraft.worldgen.ModWorldgen;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hyrulecraft — Fabric host entrypoint (1.21.1, Java 21, Fabric Loader 0.16.14).
 *
 * Fabric wins on perf (Sodium/Lithium compatible, no coremod patching).
 * NeoForge port shares common code via mod/neoforge/ — see docs/fabric-vs-neoforge-1.21.1.md.
 */
public class Hyrulecraft implements ModInitializer {
    public static final String MOD_ID = "hyrulecraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("Hyrulecraft");

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("[Hyrulecraft] Booting — Fabric 1.21.1 as host. Loading OoT→MC bridge...");

        ModItems.register();
        ModBlocks.register();
        ModEntities.register();
        SwitchFlagSystem.register(); // redstone → SWCH
        ModWorldgen.register();      // structures: deku_tree, castle_ruins, dodongo_cavern, kokiri_grove

        // Hook up item use callbacks (hookshot, bombs, ocarina songs)
        com.hyrulecraft.item.OcarinaItem.registerSongs();

        LOGGER.info("[Hyrulecraft] Ready — Fabric 1.21.1 + Iris shaders. Press Z to Z-target, R for hookshot, O for ocarina. Navi says Hey Listen!");
    }
}
