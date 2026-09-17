package com.alcatrazescapee.notreepunching.common.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.alcatrazescapee.notreepunching.Config;
import com.alcatrazescapee.notreepunching.common.ModTags;
import com.alcatrazescapee.notreepunching.platform.RegistryHolder;
import com.alcatrazescapee.notreepunching.platform.RegistryInterface;
import com.alcatrazescapee.notreepunching.platform.XPlatform;
import com.alcatrazescapee.notreepunching.util.Helpers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class ModRecipes
{
    public static final RegistryInterface<RecipeSerializer<?>> RECIPE_SERIALIZERS = XPlatform.INSTANCE.registryInterface(BuiltInRegistries.RECIPE_SERIALIZER);
    public static final RegistryInterface<RecipeType<?>> RECIPE_TYPES = XPlatform.INSTANCE.registryInterface(BuiltInRegistries.RECIPE_TYPE);

    public static final RegistryHolder<RecipeSerializer<?>> SHAPED_TOOL_DAMAGING = RECIPE_SERIALIZERS.register("tool_damaging_shaped", () -> XPlatform.INSTANCE.recipeSerializer(new ToolDamagingRecipe.Serializer<>(XPlatform.INSTANCE::shapedToolDamagingRecipe)));
    public static final RegistryHolder<RecipeSerializer<?>> SHAPELESS_TOOL_DAMAGING = RECIPE_SERIALIZERS.register("tool_damaging_shapeless", () -> XPlatform.INSTANCE.recipeSerializer(new ToolDamagingRecipe.Serializer<>(XPlatform.INSTANCE::shapelessToolDamagingRecipe)));

    public static final RegistryHolder<RecipeSerializer<?>> EMPTY_SERIALIZER = RECIPE_SERIALIZERS.register("empty", () -> XPlatform.INSTANCE.recipeSerializer(EmptyRecipe.Serializer.INSTANCE));
    public static final RegistryHolder<RecipeType<?>> EMPTY_TYPE = RECIPE_TYPES.register("empty", () -> new RecipeType<>() {});

    public static void injectRecipes(ReloadableServerResources resources)
    {
        if (!Config.INSTANCE.enableDynamicRecipeReplacement.getAsBoolean()) return;

        final Set<Item> logItems = new HashSet<>();
        final Set<Item> plankItems = new HashSet<>();

        BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.LOGS).forEach(holder -> logItems.add(holder.value()));
        BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.PLANKS).forEach(holder -> plankItems.add(holder.value()));

        final RecipeManager recipeManager = resources.getRecipeManager();
        final HolderLookup.Provider registries = resources.fullRegistries().get();

        final List<RecipeHolder<?>> newRecipes = new ArrayList<>();

        for (RecipeHolder<?> holder : recipeManager.getRecipes())
        {
            final Recipe<?> recipe = holder.value();
            if (recipe.getType() != RecipeType.CRAFTING)
            {
                newRecipes.add(holder);
                continue;
            }
            if (recipe.getSerializer() != RecipeSerializer.SHAPED_RECIPE && recipe.getSerializer() != RecipeSerializer.SHAPELESS_RECIPE)
            {
                newRecipes.add(holder);
                continue; // Only pure shaped + shapeless recipes
            }
            if (recipe.getIngredients().size() != 1)
            {
                newRecipes.add(holder);
                continue; // With a single ingredient (the log(s) in question)
            }

            final Ingredient log = recipe.getIngredients().get(0);
            final ItemStack[] values = log.getItems();

            if (Arrays.stream(values).anyMatch(item -> !logItems.contains(item.getItem())))
            {
                newRecipes.add(holder);
                continue; // Where all items in the ingredient belong to the log tag
            }

            final ItemStack result = recipe.getResultItem(registries);

            if (result.isEmpty() || !plankItems.contains(result.getItem()))
            {
                newRecipes.add(holder);
                continue; // Where the output is one of the plank items
            }

            final Item plank = result.getItem();
            final ResourceLocation plankName = BuiltInRegistries.ITEM.getKey(plank);

            // One recipe must use the same ID as the original recipe, so we can override/replace it
            // This avoids any dangling references to the original recipe ID
            // The other one we just add in our namespace
            newRecipes.add(new RecipeHolder<>(holder.id(), sawLogToPlankRecipe(ModTags.Items.SAWS, log, plank, 4)));
            newRecipes.add(new RecipeHolder<>(Helpers.identifier("generated/%s_%s".formatted(plankName.getNamespace(), plankName.getPath())), sawLogToPlankRecipe(ModTags.Items.WEAK_SAWS, log, plank, 2)));
        }

        recipeManager.replaceRecipes(newRecipes);
    }

    private static CraftingRecipe sawLogToPlankRecipe(TagKey<Item> saw, Ingredient log, Item plank, int count)
    {
        final ShapedRecipePattern pattern = ShapedRecipePattern.of(Map.of(
            'S', Ingredient.of(saw),
            'L', log
        ), "S", "L");
        final ShapedRecipe recipe = new ShapedRecipe("", CraftingBookCategory.BUILDING, pattern, new ItemStack(plank, count));
        return XPlatform.INSTANCE.shapedToolDamagingRecipe(recipe, Ingredient.of(saw));
    }
}
