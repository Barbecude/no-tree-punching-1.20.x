package com.alcatrazescapee.notreepunching.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import com.alcatrazescapee.notreepunching.Config;
import com.alcatrazescapee.notreepunching.common.blockentity.LargeVesselBlockEntity;
import com.alcatrazescapee.notreepunching.common.blockentity.ModBlockEntities;
import com.alcatrazescapee.notreepunching.platform.XPlatform;

public class LargeVesselBlock extends Block implements EntityBlock
{
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 14, 14);

    public LargeVesselBlock()
    {
        super(Properties.of().sound(SoundType.STONE).strength(1.0f));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!newState.is(state.getBlock()))
        {
            level.getBlockEntity(pos, ModBlockEntities.LARGE_VESSEL.get()).ifPresent(vessel -> {
                if (!Config.INSTANCE.largeVesselKeepsContentsWhenBroken.getAsBoolean())
                {
                    for (int i = 0; i < vessel.size(); i++)
                    {
                        final ItemStack stack = vessel.get(i);
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                        vessel.set(i, ItemStack.EMPTY);
                    }
                }
                level.updateNeighbourForOutputSignal(pos, this);
            });
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (player instanceof ServerPlayer serverPlayer && !player.isShiftKeyDown())
        {
            level.getBlockEntity(pos, ModBlockEntities.LARGE_VESSEL.get()).ifPresent(tile -> XPlatform.INSTANCE.openScreen(serverPlayer, tile, pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        // In 1.21, use DataComponents.CUSTOM_NAME instead of hasCustomHoverName()
        if (stack.get(DataComponents.CUSTOM_NAME) != null)
        {
            level.getBlockEntity(pos, ModBlockEntities.LARGE_VESSEL.get()).ifPresent(tile -> tile.setCustomName(stack.getHoverName()));
        }
    }

    /**
     * Causes the block to drop with contents in creative (1.21: playerWillDestroy returns BlockState)
     */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        level.getBlockEntity(pos, ModBlockEntities.LARGE_VESSEL.get()).ifPresent(vessel -> {
            if (!level.isClientSide && player.isCreative() && !vessel.isEmpty())
            {
                ItemStack stack = new ItemStack(this);
                // 1.21: saveToItem requires registries - use level's registry access
                vessel.saveToItem(stack, level.registryAccess());

                // 1.21: use DataComponents.CUSTOM_NAME to set custom name
                stack.set(DataComponents.CUSTOM_NAME, vessel.getDisplayName());
                ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        });
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state)
    {
        final ItemStack stack = super.getCloneItemStack(level, pos, state);
        // Need a Level (not just LevelReader) for registryAccess; cast is safe in practice
        if (level instanceof Level fullLevel)
        {
            fullLevel.getBlockEntity(pos, ModBlockEntities.LARGE_VESSEL.get()).ifPresent(tile -> tile.saveToItem(stack, fullLevel.registryAccess()));
        }
        return stack;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new LargeVesselBlockEntity(pos, state);
    }
}