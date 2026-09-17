package com.alcatrazescapee.notreepunching.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.alcatrazescapee.notreepunching.Config;

public class KnifeItem extends SwordItem
{
    public KnifeItem(Tier tier, int attackDamage, float attackSpeed, Properties properties)
    {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, attackDamage, attackSpeed)));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity)
    {
        if (!level.isClientSide && (state.getDestroySpeed(level, pos) != 0.0F || Config.INSTANCE.doInstantBreakBlocksDamageKnives.getAsBoolean()))
        {
            stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand)
    {
        return Items.SHEARS.interactLivingEntity(stack, player, entity, hand);
    }
}