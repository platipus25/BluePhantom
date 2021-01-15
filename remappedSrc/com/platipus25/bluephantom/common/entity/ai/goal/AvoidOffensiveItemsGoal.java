package com.platipus25.bluephantom.common.entity.ai.goal;

import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;


/**
 * A mixture of AvoidEntityGoal and TemptGoal
 */
public class AvoidOffensiveItemsGoal extends FleeEntityGoal<PlayerEntity> {

    public AvoidOffensiveItemsGoal(PathAwareEntity fleeingEntity, float fleeDistance, double fleeSlowSpeed, double fleeFastSpeed, Predicate<ItemStack> offensiveItems) {
        super(fleeingEntity, PlayerEntity.class, fleeDistance, fleeSlowSpeed, fleeFastSpeed, (livingEntity) -> {
            if (livingEntity instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity) livingEntity;
                for (ItemStack itemStack: playerEntity.getItemsHand()) {
                    if (offensiveItems.test(itemStack)) {
                        return true;
                    }
                }

            }
            return false;
        });
    }
}
