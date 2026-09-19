package com.alcatrazescapee.notreepunching.util;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import com.alcatrazescapee.notreepunching.Config;
import com.alcatrazescapee.notreepunching.common.ModTags;
import com.alcatrazescapee.notreepunching.mixin.AbstractBlockAccessor;
import com.alcatrazescapee.notreepunching.mixin.AbstractBlockStateAccessor;


public final class HarvestBlockHandler
{
    public static void setup()
    {
        // Empty on purpose:
        // Do NOT forcefully set requiresCorrectToolForDrops on all blocks in BuiltInRegistries.BLOCK.
        // Doing so breaks every other mod and causes blocks that should drop by hand (torches, crops,
        // lanterns, modded decorations) to drop nothing and get destroyed.
    }

    public static boolean isUsingCorrectToolToMine(BlockState state, @Nullable BlockPos pos, Player player)
    {
        return isUsingCorrectTool(state, pos, player, ModTags.Blocks.ALWAYS_BREAKABLE, Config.INSTANCE.doBlocksMineWithoutCorrectTool, Config.INSTANCE.doInstantBreakBlocksMineWithoutCorrectTool, true);
    }

    public static boolean isUsingCorrectToolForDrops(BlockState state, @Nullable BlockPos pos, Player player)
    {
        return isUsingCorrectTool(state, pos, player, ModTags.Blocks.ALWAYS_DROPS, Config.INSTANCE.doBlocksDropWithoutCorrectTool, Config.INSTANCE.doInstantBreakBlocksDropWithoutCorrectTool, false);
    }

    private static boolean isUsingCorrectTool(BlockState state, @Nullable BlockPos pos, Player player, TagKey<Block> alwaysAllowTag, Supplier<Boolean> withoutCorrectTool, BooleanSupplier instantBreakBlocksWithoutCorrectTool, boolean checkingCanMine)
    {
        if (withoutCorrectTool.get())
        {
            return true; // Feature is disabled, always allow
        }

        final float destroySpeed = getDestroySpeed(state, pos, player);
        if (destroySpeed == 0 && instantBreakBlocksWithoutCorrectTool.getAsBoolean())
        {
            return true; // Feature is conditionally disabled for instant break blocks, always allow
        }

        if (state.is(alwaysAllowTag))
        {
            return true; // Block is set to always allow
        }

        // If a block does not naturally require a tool for drops and is not a log, it is always allowed to mine and drop
        if (!state.requiresCorrectToolForDrops() && !state.is(BlockTags.LOGS))
        {
            return true;
        }

        final ItemStack stack = player.getMainHandItem();
        if (stack.isCorrectToolForDrops(state))
        {
            return true; // Tool has already reported itself as the correct tool. This includes a tier check in vanilla.
        }

        if (checkingCanMine && stack.getDestroySpeed(state) > 1.0f)
        {
            return true; // Tool reported itself as harvesting faster than normal, in which case when checking if we can *mine* the block, we return true.
        }

        if (!state.is(ModTags.Blocks.MINEABLE))
        {
            return true; // If we have no idea what tool can mine this block, we have to return true, as otherwise it's impossible to mine
        }

        return false; // None of our checks have confirmed we can mine this block, so we can't
    }

    private static float getDestroySpeed(BlockState state, @Nullable BlockPos pos, Player player)
    {
        return pos != null ? state.getDestroySpeed(player.level(), pos) : ((AbstractBlockStateAccessor) state).getDestroySpeed();
    }
}