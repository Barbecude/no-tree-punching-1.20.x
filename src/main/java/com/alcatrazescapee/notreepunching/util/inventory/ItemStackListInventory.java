package com.alcatrazescapee.notreepunching.util.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

public record ItemStackListInventory(NonNullList<ItemStack> slots) implements ItemStackInventory
{
    public static ItemStackInventory create(int slots, CompoundTag tag, HolderLookup.Provider registries)
    {
        final NonNullList<ItemStack> stacks = NonNullList.withSize(slots, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, stacks, registries);
        return new ItemStackListInventory(stacks);
    }
}
