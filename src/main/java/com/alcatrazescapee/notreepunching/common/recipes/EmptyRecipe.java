package com.alcatrazescapee.notreepunching.common.recipes;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * A no-op recipe implementation, used to remove recipes platform independently.
 */
public record EmptyRecipe() implements CraftingRecipe
{
    public static final EmptyRecipe INSTANCE = new EmptyRecipe();
    public static final MapCodec<EmptyRecipe> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, EmptyRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public boolean matches(CraftingInput container, Level level)
    {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registries)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public CraftingBookCategory category()
    {
        return CraftingBookCategory.MISC;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipes.EMPTY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return ModRecipes.EMPTY_TYPE.get();
    }

    public enum Serializer implements RecipeSerializer<EmptyRecipe>
    {
        INSTANCE;

        @Override
        public MapCodec<EmptyRecipe> codec()
        {
            return EmptyRecipe.CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EmptyRecipe> streamCodec()
        {
            return EmptyRecipe.STREAM_CODEC;
        }
    }
}
