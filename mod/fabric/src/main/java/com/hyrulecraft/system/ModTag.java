package com.hyrulecraft.system;

import com.hyrulecraft.Hyrulecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;

public class ModTag {
    public static final TagKey<EntityType<?>> ATTENTION_ENABLED = TagKey.of(Registries.ENTITY_TYPE.getKey(), Hyrulecraft.id("attention_enabled"));
    public static final TagKey<EntityType<?>> HOSTILE_OOT = TagKey.of(Registries.ENTITY_TYPE.getKey(), Hyrulecraft.id("hostile_oot"));
}
