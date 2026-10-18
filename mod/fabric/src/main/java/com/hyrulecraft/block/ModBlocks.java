package com.hyrulecraft.block;

import com.hyrulecraft.Hyrulecraft;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;

public class ModBlocks {
    // BlockState flag for doors that open via Lullaby / Song
    public static final BooleanProperty LULLABY_OPEN = BooleanProperty.of("lullaby_open");

    // ── Tags that map to OoT actor flags
    public static final TagKey<Block> HOOKSHOT_TARGET = TagKey.of(Registries.BLOCK.getKey(), Hyrulecraft.id("hookshot_target"));
    public static final TagKey<Block> HOOKSHOT_PULLS_PLAYER = TagKey.of(Registries.BLOCK.getKey(), Hyrulecraft.id("hookshot_pulls_player"));
    public static final TagKey<Block> SPIN_BREAKABLE = TagKey.of(Registries.BLOCK.getKey(), Hyrulecraft.id("spin_breakable"));
    public static final TagKey<Block> LULLABY_DOOR = TagKey.of(Registries.BLOCK.getKey(), Hyrulecraft.id("lullaby_door"));
    public static final TagKey<Block> CRACKED_STONE = TagKey.of(Registries.BLOCK.getKey(), Hyrulecraft.id("cracked_stone"));

    // ── Blocks

    // Chain-like hook target — wood/metal that hookshot can grab
    public static final Block HOOK_TARGET = register("hook_target",
            new HookTargetBlock(FabricBlockSettings.copyOf(Blocks.CHAIN).sounds(BlockSoundGroup.CHAIN).nonOpaque()));

    // OoT chest — En_Box logic: needs small key (tag), sets TREASURE flag (block entity)
    public static final Block DEKU_CHEST = register("deku_chest",
            new DekuChestBlock(FabricBlockSettings.copyOf(Blocks.CHEST).strength(2.5f).sounds(BlockSoundGroup.WOOD)));

    // Cracked stone — bomb/spin only
    public static final Block CRACKED_STONE_BLOCK = register("cracked_stone",
            new CrackedStoneBlock(FabricBlockSettings.copyOf(Blocks.COBBLESTONE).strength(2f, 6f).sounds(BlockSoundGroup.STONE)));

    // Lullaby door — closed by default, opens with Zelda's Lullaby or key
    public static final Block LULLABY_DOOR_BLOCK = register("lullaby_door",
            new LullabyDoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_DOOR).sounds(BlockSoundGroup.METAL).nonOpaque()));

    // Switch block — SWCH flag; powered by redstone OR bomb nearby
    public static final Block TEMPLE_SWITCH = register("temple_switch",
            new TempleSwitchBlock(FabricBlockSettings.copyOf(Blocks.STONE_PRESSURE_PLATE).strength(1.5f)));

    // Deku web — sword/burn
    public static final Block DEKU_WEB = register("deku_web",
            new DekuWebBlock(FabricBlockSettings.copyOf(Blocks.COBWEB).noCollision().sounds(BlockSoundGroup.WOOL)));

    private static Block register(String name, Block block) {
        Registry.register(Registries.ITEM, Hyrulecraft.id(name),
                new BlockItem(block, new FabricItemSettings()));
        return Registry.register(Registries.BLOCK, Hyrulecraft.id(name), block);
    }

    public static void register() {
        Hyrulecraft.LOGGER.info("[Hyrulecraft] Blocks registered");
    }
}
