package com.alcatrazescapee.notreepunching.util.inventory;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class ItemStackAttachedInventory implements ItemStackInventory
{
    public static Factory create(int slots, Predicate<ItemStack> predicate)
    {
        return stack -> new ItemStackAttachedInventory(stack, slots, predicate);
    }

    private final ItemStack stack;
    private final Predicate<ItemStack> predicate;
    private final NonNullList<ItemStack> stacks;

    public ItemStackAttachedInventory(ItemStack stack, int slots, Predicate<ItemStack> predicate)
    {
        this.stack = stack;
        this.predicate = predicate;
        this.stacks = NonNullList.withSize(slots, ItemStack.EMPTY);

        // Load items from DataComponents.CONTAINER (1.21 API)
        final ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents != null)
        {
            final List<ItemStack> items = contents.stream().toList();
            for (int i = 0; i < Math.min(items.size(), stacks.size()); i++)
            {
                stacks.set(i, items.get(i).copy());
            }
        }
    }

    @Override
    public NonNullList<ItemStack> slots()
    {
        return stacks;
    }

    @Override
    public void modified()
    {
        // Save items to DataComponents.CONTAINER (1.21 API)
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(stacks));
    }

    @Override
    public boolean canContain(ItemStack stack)
    {
        return predicate.test(stack);
    }

    @FunctionalInterface
    public interface Factory
    {
        ItemStackAttachedInventory create(ItemStack stack);
    }
}
