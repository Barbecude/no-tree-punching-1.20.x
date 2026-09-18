package com.alcatrazescapee.notreepunching.common.items;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import com.alcatrazescapee.notreepunching.common.blockentity.LargeVesselBlockEntity;
import com.alcatrazescapee.notreepunching.util.Helpers;
import com.alcatrazescapee.notreepunching.util.inventory.ItemStackListInventory;

public class LargeVesselBlockItem extends BlockItem
{
    public LargeVesselBlockItem(Block blockIn, Properties builder)
    {
        super(blockIn, builder);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        // In 1.21, block entity data is stored in DataComponents.BLOCK_ENTITY_DATA or the ItemContainerContents component
        // The large vessel stores items through the BlockEntity NBT. We use BLOCK_ENTITY_DATA to find them.
        final var blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData != null)
        {
            final var tag = blockEntityData.copyTag();
            final var registries = context.registries();
            if (tag != null && registries != null)
            {
                final var listInventory = ItemStackListInventory.create(LargeVesselBlockEntity.SLOTS, tag, registries);
                Helpers.addInventoryTooltip(listInventory, tooltip);
            }
        }
    }
}