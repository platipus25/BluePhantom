package com.platipus25.bluephantom.common.entity.ai.goal;
/*
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class TemptEatingGoal extends PlayerItemDetectionGoal {
    CreatureEntity creature;
    double speed;

    private static EntityPredicate getEntityPredicate() {
        return (new EntityPredicate()).setLineOfSiteRequired().allowInvulnerable().allowFriendlyFire().setSkipAttackChecks().setDistance(20.0D);
    }

    public TemptEatingGoal(CreatureEntity entityIn, Predicate<ItemStack> itemStackPredicateIn, double speedIn) {
        super(entityIn, itemStackPredicateIn, itemStackPredicateIn, getEntityPredicate().setCustomPredicate((livingEntity -> {
            if (livingEntity.getItemInUseCount() > 0) {
                //SpectralMobs.LOGGER.info(livingEntity.getItemInUseCount());
                return true;
            }
            return false;
        })));
        creature = entityIn;
        speed = speedIn;
    }

    public void tick() {
        this.creature.getLookController().setLookPositionWithEntity(this.target, (float)(this.creature.getHorizontalFaceSpeed() + 20), (float)this.creature.getVerticalFaceSpeed());
        if (this.creature.getDistanceSq(this.target) < 2.25D) {
            this.creature.getNavigator().clearPath();
        } else {
            this.creature.getNavigator().tryMoveToEntityLiving(this.target, this.speed);
        }
    }

}
*/