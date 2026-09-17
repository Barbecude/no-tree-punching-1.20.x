package com.alcatrazescapee.notreepunching.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import com.alcatrazescapee.notreepunching.common.ModTags;
import com.alcatrazescapee.notreepunching.common.container.LargeVesselContainer;
import com.alcatrazescapee.notreepunching.util.Helpers;
import com.alcatrazescapee.notreepunching.util.inventory.ItemStackInventory;

import static com.alcatrazescapee.notreepunching.NoTreePunching.MOD_ID;

public class LargeVesselBlockEntity extends ModBlockEntity implements MenuProvider, ItemStackInventory
{
    public static final int SLOT_COLUMNS = 5;
    public static final int SLOT_ROWS = 3;
    public static final int SLOTS = SLOT_COLUMNS * SLOT_ROWS;

    public static final Component NAME = Component.translatable(MOD_ID + ".block_entity.large_vessel");

    private final NonNullList<ItemStack> slots;
    private @Nullable Component name;

    public LargeVesselBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.LARGE_VESSEL.get(), pos, state);

        this.slots = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        this.name = null;
    }

    public void setCustomName(@Nullable Component name)
    {
        this.name = name;
    }

    @Override
    public Component getDisplayName()
    {
        return name == null ? NAME : name;
    }

    @Nullable
    @Override
    public LargeVesselContainer createMenu(int windowId, Inventory playerInventory, Player player)
    {
        return new LargeVesselContainer(this, playerInventory, windowId);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        slots.clear();
        ContainerHelper.loadAllItems(tag, slots, registries);
        if (tag.contains("CustomName"))
        {
            name = Component.Serializer.fromJson(tag.getString("CustomName"), registries);
        }
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        ContainerHelper.saveAllItems(tag, slots, registries);
        if (name != null)
        {
            tag.putString("CustomName", Component.Serializer.toJson(name, registries));
        }
        super.saveAdditional(tag, registries);
    }

    @Override
    public NonNullList<ItemStack> slots()
    {
        return slots;
    }

    @Override
    public boolean canContain(ItemStack stack)
    {
        return !Helpers.isItem(stack.getItem(), ModTags.Items.LARGE_VESSEL_BLACKLIST);
    }

    public boolean isEmpty()
    {
        for (int i = 0; i < size(); i++)
        {
            if (!get(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }
}