package com.alcatrazescapee.notreepunching.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.alcatrazescapee.notreepunching.util.Helpers;

public class LooseRockBlock extends Block
{
    public static final VoxelShape SHAPE = box(6, 0, 6, 10, 1, 10);

    public LooseRockBlock()
    {
        super(Properties.of().sound(SoundType.STONE).strength(0.15f).noCollission().noOcclusion());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        if (!state.canSurvive(level, pos) && !level.isClientSide)
        {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final ItemStack dropStack = getCloneItemStack(level, pos, state);
        Helpers.giveItemToPlayer(level, player, dropStack);
        level.removeBlock(pos, false);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockState stateUnder = level.getBlockState(pos.below());
        return stateUnder.isFaceSturdy(level, pos.below(), Direction.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState state)
    {
        return true;
    }
}