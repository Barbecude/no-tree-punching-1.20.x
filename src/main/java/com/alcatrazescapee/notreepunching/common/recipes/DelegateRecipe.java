package com.alcatrazescapee.notreepunching.common.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public interface DelegateRecipe<C extends RecipeInput> extends Recipe<C>
{
    Recipe<C> delegate();

    @Override
    default NonNullList<ItemStack> getRemainingItems(C container)
    {
        return delegate().getRemainingItems(container);
    }

    @Override
    default boolean isIncomplete()
    {
        return delegate().isIncomplete();
    }

    @Override
    default boolean matches(C inv, Level level)
    {
        return delegate().matches(inv, level);
    }

    @Override
    default ItemStack assemble(C inv, HolderLookup.Provider registries)
    {
        return delegate().assemble(inv, registries);
    }

    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return delegate().canCraftInDimensions(width, height);
    }

    @Override
    default ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return delegate().getResultItem(registries);
    }

    @Override
    default NonNullList<Ingredient> getIngredients()
    {
        return delegate().getIngredients();
    }

    @Override
    default boolean isSpecial()
    {
        return delegate().isSpecial();
    }

    @Override
    default String getGroup()
    {
        return delegate().getGroup();
    }

    @Override
    default ItemStack getToastSymbol()
    {
        return delegate().getToastSymbol();
    }
}
