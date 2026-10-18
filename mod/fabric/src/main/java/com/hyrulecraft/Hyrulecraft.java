package com.hyrulecraft;

import com.hyrulecraft.block.ModBlocks;
import com.hyrulecraft.entity.ModEntities;
import com.hyrulecraft.item.ModItems;
import com.hyrulecraft.system.SwitchFlagSystem;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hyrulecraft — Fabric host entrypoint.
 *
 * This is the "Minecraft as host" merge: Fabric loads normally, and we
 * inject OoT's actor/camera/inventory logic as Java systems.
 *
 * OoT fidelity: where the C decomp has fixed-point math / exact collision,
 * we port it 1:1 (see system/OoTMath.java) so bomb-hover / hookshot
 * distances match N64 (where practical).
 */
public class Hyrulecraft implements ModInitializer {
    public static final String MOD_ID = "hyrulecraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("Hyrulecraft");

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("[Hyrulecraft] Booting — Fabric as host. Loading OoT→MC bridge...");

        ModItems.register();
        ModBlocks.register();
        ModEntities.register();
        SwitchFlagSystem.register(); // redstone → SWCH

        // Hook up item use callbacks (hookshot, bombs, ocarina songs)
        com.hyrulecraft.item.OcarinaItem.registerSongs();

        LOGGER.info("[Hyrulecraft] Ready. Press Z to Z-target, R for hookshot, O for ocarina. Navi says Hey Listen!");
    }
}
