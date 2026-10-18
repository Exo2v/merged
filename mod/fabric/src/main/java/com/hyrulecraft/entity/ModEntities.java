package com.hyrulecraft.entity;

import com.hyrulecraft.Hyrulecraft;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEntities {
    public static final EntityType<HookshotProjectileEntity> HOOKSHOT_PROJECTILE =
            Registry.register(Registries.ENTITY_TYPE, Hyrulecraft.id("hookshot_projectile"),
                    FabricEntityTypeBuilder.<HookshotProjectileEntity>create(SpawnGroup.MISC, HookshotProjectileEntity::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .trackRangeChunks(8).trackedUpdateRate(1).build());

    public static final EntityType<BombEntity> BOMB =
            Registry.register(Registries.ENTITY_TYPE, Hyrulecraft.id("bomb"),
                    FabricEntityTypeBuilder.<BombEntity>create(SpawnGroup.MISC, BombEntity::new)
                            .dimensions(EntityDimensions.fixed(0.6f, 0.6f)).trackRangeChunks(8).build());

    public static final EntityType<BombchuEntity> BOMBCHU =
            Registry.register(Registries.ENTITY_TYPE, Hyrulecraft.id("bombchu"),
                    FabricEntityTypeBuilder.<BombchuEntity>create(SpawnGroup.MISC, BombchuEntity::new)
                            .dimensions(EntityDimensions.fixed(0.4f, 0.4f)).trackRangeChunks(8).build());

    public static final EntityType<OctorokEntity> OCTOROK =
            Registry.register(Registries.ENTITY_TYPE, Hyrulecraft.id("octorok"),
                    FabricEntityTypeBuilder.<OctorokEntity>create(SpawnGroup.MONSTER, OctorokEntity::new)
                            .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).trackRangeChunks(8).build());

    public static final EntityType<SkulltulaEntity> SKULLTULA =
            Registry.register(Registries.ENTITY_TYPE, Hyrulecraft.id("skulltula"),
                    FabricEntityTypeBuilder.<SkulltulaEntity>create(SpawnGroup.MONSTER, SkulltulaEntity::new)
                            .dimensions(EntityDimensions.fixed(0.6f, 0.6f)).trackRangeChunks(8).build());

    public static final EntityType<BoomerangEntity> BOOMERANG =
            Registry.register(Registries.ENTITY_TYPE, Hyrulecraft.id("boomerang"),
                    FabricEntityTypeBuilder.<BoomerangEntity>create(SpawnGroup.MISC, BoomerangEntity::new)
                            .dimensions(EntityDimensions.fixed(0.5f, 0.2f)).build());

    public static void register() {
        // Attribute registration (done in ModEntities::registerAttributes called from Hyrulecraft)
        Hyrulecraft.LOGGER.info("[Hyrulecraft] Entities registered");
    }
}
