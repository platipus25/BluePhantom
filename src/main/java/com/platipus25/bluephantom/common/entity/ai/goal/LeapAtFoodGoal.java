package com.platipus25.bluephantom.common.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

/**
 * based on LeapAtTargetGoal
 */
public class LeapAtFoodGoal extends PlayerItemDetectionGoal {
    private final MobEntity leaper;
    private final float leapMotionY;
    private final int leapChance;
    private int leapTimer = 0;
    private boolean shouldLeap = false;

    public LeapAtFoodGoal(MobEntity leapingEntity, float leapMotionYIn, int leapChanceIn) {
        super(leapingEntity, (itemStack) -> itemStack.isFood() && !itemStack.getItem().getFoodComponent().isMeat());
        this.leaper = leapingEntity;
        this.leapMotionY = leapMotionYIn;
        this.leapChance = leapChanceIn;
        this.targetEntitySelector.setBaseMaxDistance(16.0);
        this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE, Goal.Control.LOOK));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean canStart() {
        if (!super.canStart()){
            return false;
        }

        if (this.leapTimer > 0) {
            --this.leapTimer;
            return false;
        }

        double d0 = leaper.distanceTo(this.target);
        if (d0 < 1.0D || d0 > 5.0D) {
            return false;
        }

        if (!this.leaper.isOnGround()) {
            return false;
        }

        if (leaper.hasPassengers()) {
            return false;
        }

        if(target.isUsingItem()) {
            return true;
        }

        return this.leaper.getRandom().nextInt(this.leapChance) == 0;
    }

    private void eatFood(PlayerEntity player) {
        for (ItemStack itemStack : player.getItemsHand()) {
            if (itemStack.isFood()) {
                leaper.eatFood(target.getEntityWorld(), itemStack);
                return;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean shouldContinue() {
        if (!this.leaper.isOnGround() || this.shouldLeap) {
            return true;
        }

        if (this.leaper.distanceTo(this.target) < 2.0D) {
            this.eatFood(this.target);
            this.leapTimer = 100;
        }

        return false;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    public void resetTask() {
        this.leapTimer = 100;
        this.shouldLeap = false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void start() {
        this.shouldLeap = true;
        leaper.lookAtEntity(target, 200f, 200f);
        leaper.getNavigation().startMovingTo(target, leaper.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
    }

    private void leap() {
        Vec3d vec3d = this.leaper.getVelocity();
        Vec3d vec3d1 = new Vec3d(target.getX() - leaper.getX(), 0.0D, target.getZ() - leaper.getZ());
        if (vec3d1.lengthSquared() > 1.0E-7D) {
            vec3d1 = vec3d1.normalize().multiply(0.4D).add(vec3d.multiply(0.2D));
        }

        this.leaper.setVelocity(vec3d1.x, this.leapMotionY, vec3d1.z);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        // wait until done looking
        if (this.shouldLeap && !this.leaper.getLookControl().isActive()) {
            this.leap();
            this.shouldLeap = false;
        }
    }
}