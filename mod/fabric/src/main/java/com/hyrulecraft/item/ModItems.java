package com.hyrulecraft.item;

import com.hyrulecraft.Hyrulecraft;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public class ModItems {
    // ── Swords — port of OoT's SwordType + MC tool tier
    // Wood → Kokiri (7 dmg with sweep), Stone→Goron Knife, Iron→Master, Diamond→Biggoron
    public static final Item KOKIRI_SWORD = register("kokiri_sword",
            new HylianSwordItem(ToolMaterials.WOOD, 3, -2.0f,
                    new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1),
                    8.0f, true)); // damage, speed, sweep range, spin-cuts grass

    public static final Item MASTER_SWORD = register("master_sword",
            new HylianSwordItem(ToolMaterials.IRON, 3, -2.1f,
                    new FabricItemSettings().fireproof().rarity(Rarity.EPIC).maxCount(1),
                    9.5f, true));

    public static final Item BIGGORON_SWORD = register("biggoron_sword",
            new HylianSwordItem(ToolMaterials.DIAMOND, 6, -2.8f,
                    new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1),
                    12f, false));

    // ── Tools that map to OoT equipment
    public static final Item HOOKSHOT = register("hookshot",
            new HookshotItem(new FabricItemSettings().maxCount(1).maxDamage(64), 16f));
    public static final Item LONGSHOT = register("longshot",
            new HookshotItem(new FabricItemSettings().maxCount(1).maxDamage(64), 32f));

    public static final Item HYLIAN_SHIELD = register("hylian_shield",
            new ShieldItem(new FabricItemSettings().maxCount(1).maxDamage(336)));

    public static final Item BOMB = register("bomb",
            new BombItem(new FabricItemSettings().maxCount(16)));
    public static final Item BOMBCHU = register("bombchu",
            new BombchuItem(new FabricItemSettings().maxCount(16)));

    public static final Item BOOMERANG = register("boomerang",
            new BoomerangItem(new FabricItemSettings().maxCount(1).maxDamage(128)));

    public static final Item FAIRY_BOW = register("fairy_bow",
            new FairyBowItem(new FabricItemSettings().maxCount(1).maxDamage(384)));

    public static final Item SLINGSHOT = register("slingshot",
            new SlingshotItem(new FabricItemSettings().maxCount(1)));

    public static final Item DEKU_STICK = register("deku_stick",
            new DekuStickItem(new FabricItemSettings().maxCount(16).maxDamage(16)));

    public static final Item DEKU_NUT = register("deku_nut",
            new DekuNutItem(new FabricItemSettings().maxCount(16)));

    public static final Item OCARINA = register("ocarina",
            new OcarinaItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

    public static final Item LENS_OF_TRUTH = register("lens_of_truth",
            new LensOfTruthItem(new FabricItemSettings().maxCount(1)));

    // ── Tunics = armor with OoT tunic abilities
    public static final Item GORON_TUNIC = register("goron_tunic",
            new TunicItem(ArmorMaterials.IRON, ArmorItem.Type.CHESTPLATE,
                    new FabricItemSettings().fireproof(), TunicItem.Type.GORON));
    public static final Item ZORA_TUNIC = register("zora_tunic",
            new TunicItem(ArmorMaterials.CHAIN, ArmorItem.Type.CHESTPLATE,
                    new FabricItemSettings(), TunicItem.Type.ZORA));
    public static final Item KOKIRI_TUNIC = register("kokiri_tunic",
            new TunicItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE,
                    new FabricItemSettings(), TunicItem.Type.KOKIRI));

    public static final Item IRON_BOOTS = register("iron_boots",
            new IronBootsItem(ArmorMaterials.IRON, ArmorItem.Type.BOOTS, new FabricItemSettings()));
    public static final Item HOVER_BOOTS = register("hover_boots",
            new HoverBootsItem(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Hyrulecraft.id(name), item);
    }

    public static void register() {
        // Add to a creative tab (Combat + Tools)
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(KOKIRI_SWORD);
            entries.add(MASTER_SWORD);
            entries.add(HYLIAN_SHIELD);
            entries.add(FAIRY_BOW);
            entries.add(SLINGSHOT);
            entries.add(BOMB);
            entries.add(BOOMERANG);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(HOOKSHOT);
            entries.add(LONGSHOT);
            entries.add(OCARINA);
            entries.add(LENS_OF_TRUTH);
            entries.add(GORON_TUNIC);
            entries.add(ZORA_TUNIC);
            entries.add(IRON_BOOTS);
            entries.add(HOVER_BOOTS);
        });
        // Note: actual group fill is in HyrulecraftClient.registerCreativeTab()
        Hyrulecraft.LOGGER.info("[Hyrulecraft] Registered {} items", 16);
    }
}
