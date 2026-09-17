package com.alcatrazescapee.notreepunching.common;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import com.alcatrazescapee.notreepunching.platform.XPlatform;

public final class ModTiers
{
    public static final Tier FLINT = XPlatform.INSTANCE.toolTier(60, 2.5f, 0.5f, BlockTags.INCORRECT_FOR_STONE_TOOL, 0, () -> Ingredient.of(Items.FLINT));
}