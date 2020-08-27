package com.platipus25.bluephantom.common.entity.ai.goal;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;

import java.util.function.Predicate;

public class ItemDetectionGoal<T extends LivingEntity> extends Goal {
    public static final TargetPredicate DEFAULT_PREDICATE = TargetPredicate.DEFAULT.setBaseMaxDistance(16f);
    public static final TargetPredicate BACKSTABBING_TAMEABLE = DEFAULT_PREDICATE.ignoreEntityTargetRules().includeTeammates();
    protected TargetPredicate targetEntitySelector;
    protected Predicate<ItemStack> itemStackPredicate;
    protected final Class<T> targetClass;
    protected final LivingEntity goalOwner;
    protected T target;

    // Predicate<LivingEntity> to Ingredient (Predicate<ItemStack>)

    public ItemDetectionGoal(LivingEntity entityIn, Class<T> targetClass, Predicate<ItemStack> itemStackPredicateIn, TargetPredicate entityPredicateIn) {
        this.goalOwner = entityIn;
        this.targetClass = targetClass;
        this.itemStackPredicate = itemStackPredicateIn;
        this.targetEntitySelector = entityPredicateIn;
    }

    protected boolean testHands(LivingEntity livingEntity) {
        for (ItemStack itemStack : livingEntity.getItemsHand()) {
            if (itemStackPredicate.test(itemStack)) {
                return true;
            }
        }
        return false;
    }

    public boolean canStart() {
        if (!this.updateTarget()) {
            return false;
        }

        return this.testHands(this.target);
    }

    public void resetTask() {
        this.target = null;
    }

    protected Box getTargetableArea() {
        double targetDistance = getTargetDistance();
        return this.goalOwner.getBoundingBox().expand(targetDistance, 4.0D, targetDistance);
    }

    protected double getTargetDistance() {
        return this.goalOwner.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }

    protected boolean updateTarget() {
        this.target = this.goalOwner.getEntityWorld().getClosestEntity(targetClass, targetEntitySelector, goalOwner, goalOwner.getX(), goalOwner.getY(), goalOwner.getZ(), this.getTargetableArea());
        return this.target != null;
    }

}
