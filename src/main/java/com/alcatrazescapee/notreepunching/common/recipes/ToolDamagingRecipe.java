package com.alcatrazescapee.notreepunching.common.recipes;

import java.util.Optional;
import java.util.function.BiFunction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import com.alcatrazescapee.notreepunching.platform.XPlatform;
import com.alcatrazescapee.notreepunching.util.Helpers;

public abstract class ToolDamagingRecipe implements DelegateRecipe<CraftingInput>, CraftingRecipe
{
    public final CraftingRecipe recipe;
    public @Nullable final Ingredient tool;

    protected ToolDamagingRecipe(CraftingRecipe recipe, @Nullable Ingredient tool)
    {
        this.recipe = recipe;
        this.tool = tool;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput container)
    {
        final NonNullList<ItemStack> items = NonNullList.withSize(container.size(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++)
        {
            final ItemStack stack = container.getItem(i);
            final ItemStack remainder = XPlatform.INSTANCE.getCraftingRemainder(stack);
            if (!remainder.isEmpty())
            {
                items.set(i, remainder);
            }
            else if (stack.isDamageableItem() && (tool == null || tool.test(stack)))
            {
                items.set(i, Helpers.hurtAndBreak(stack, 1).copy());
            }
        }
        return items;
    }

    @Override
    public CraftingRecipe delegate()
    {
        return recipe;
    }

    @Override
    public CraftingBookCategory category()
    {
        return recipe.category();
    }

    public static class Shaped extends ToolDamagingRecipe
    {
        public Shaped(CraftingRecipe recipe, @Nullable Ingredient tool)
        {
            super(recipe, tool);
        }

        @Override
        public RecipeSerializer<?> getSerializer()
        {
            return ModRecipes.SHAPED_TOOL_DAMAGING.get();
        }
    }

    public static class Shapeless extends ToolDamagingRecipe
    {
        public Shapeless(CraftingRecipe recipe, @Nullable Ingredient tool)
        {
            super(recipe, tool);
        }

        @Override
        public RecipeSerializer<?> getSerializer()
        {
            return ModRecipes.SHAPELESS_TOOL_DAMAGING.get();
        }
    }

    public static class Serializer<T extends ToolDamagingRecipe> implements RecipeSerializer<T>
    {
        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        public Serializer(BiFunction<CraftingRecipe, Ingredient, T> factory)
        {
            this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Recipe.CODEC.fieldOf("recipe").forGetter(r -> r.recipe),
                Ingredient.CODEC.optionalFieldOf("tool").forGetter(r -> Optional.ofNullable(r.tool))
            ).apply(instance, (rec, tl) -> factory.apply((CraftingRecipe) rec, tl.orElse(null))));

            this.streamCodec = StreamCodec.composite(
                Recipe.STREAM_CODEC,
                r -> r.recipe,
                ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC),
                r -> Optional.ofNullable(r.tool),
                (rec, tl) -> factory.apply((CraftingRecipe) rec, tl.orElse(null))
            );
        }

        @Override
        public MapCodec<T> codec()
        {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec()
        {
            return streamCodec;
        }
    }
}
