package com.platipus25.bluephantom.common.entity.ai.goal;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class PlayerItemDetectionGoal extends ItemDetectionGoal<PlayerEntity> {

    public PlayerItemDetectionGoal(LivingEntity entityIn, Predicate<ItemStack> itemStackPredicateIn) {
        super(entityIn, PlayerEntity.class, itemStackPredicateIn, BACKSTABBING_TAMEABLE);
    }

    public PlayerItemDetectionGoal(LivingEntity entityIn, Predicate<ItemStack> itemStackPredicateIn, TargetPredicate entityPredicateIn) {
        super(entityIn, PlayerEntity.class, itemStackPredicateIn, entityPredicateIn);
    }

    @Override
    protected boolean updateTarget() {
        target = goalOwner.world.getClosestPlayer(this.goalOwner, getTargetDistance());
        return target != null;
    }
}
