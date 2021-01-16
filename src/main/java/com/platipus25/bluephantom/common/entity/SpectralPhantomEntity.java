package com.platipus25.bluephantom.common.entity;

import com.platipus25.bluephantom.BluePhantom;
import com.platipus25.bluephantom.common.entity.ai.goal.AvoidOffensiveItemsGoal;
import com.platipus25.bluephantom.common.entity.ai.goal.LeapAtFoodGoal;
//import com.platipus25.bluephantom.common.entity.ai.goal.TemptEatingGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.function.Predicate;

public class SpectralPhantomEntity extends PigEntity {
    private static final Logger LOGGER = LogManager.getLogger(BluePhantom.MODID + SpectralPhantomEntity.class.getName());

    private static final Ingredient TEMPTING_ITEMS = Ingredient.ofItems(Items.CARROT_ON_A_STICK, Items.POTATO, Items.BAKED_POTATO);
    private static final Predicate<ItemStack> OFFENSIVE_ITEMS = Ingredient.ofItems(Items.SADDLE, Items.LEAD).or((itemStack) -> {
        FoodComponent foodComponent = itemStack.getItem().getFoodComponent();
        return foodComponent != null && foodComponent.isMeat();
    });


    public SpectralPhantomEntity(EntityType<? extends SpectralPhantomEntity> entityIn, World world) {
        super(entityIn, world);
   }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.5D));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new LeapAtFoodGoal(this, 0.2F, 50));
        this.goalSelector.add(3, new TemptGoal(this, 1.0D, TEMPTING_ITEMS, false));
        this.goalSelector.add(3, new AvoidOffensiveItemsGoal(this, 16f, 1.2D, 2D, OFFENSIVE_ITEMS));
        this.goalSelector.add(7, new NuzzlePlayerGoal(this, 0.75D, false));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(5, new FleeEntityGoal<>(this, SpectralPhantomEntity.class, 6.0F, 1.0D, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        //this.targetSelector.add(2, new OwnerHurtTargetGoal(this));

        this.targetSelector.add(4, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, (entity) -> !this.canBeSeenByEntity(entity) && !this.hasPlayerRider()));
        super.initGoals();
    }

    public static DefaultAttributeContainer.Builder createSpectralPhantomAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.1D);
    }

    /**
     * returns true if the entity provided in the argument can see this entity. (Raytrace)
     */
    public boolean canBeSeenByEntity(Entity entityIn) {
        Vec3d lookVec = entityIn.getRotationVec(0);
        Vec3d posDiffVec = this.getPos().subtract(entityIn.getPos()).normalize(); // vector pointing from entityIn to this

        double diff = lookVec.dotProduct(posDiffVec);
        //LOGGER.info("Look diff: "+diff)

        return diff > 0;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_GHAST_AMBIENT;
    }

    @Override
    public PigEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return (SpectralPhantomEntity) getType().create(serverWorld);
    }

    private static class NuzzlePlayerGoal extends MeleeAttackGoal {
        SpectralPhantomEntity creature;

        public NuzzlePlayerGoal(SpectralPhantomEntity creature, double speedIn, boolean useLongMemory) {
            super(creature, speedIn, useLongMemory);
            this.creature = creature;
        }

        @Override
        public boolean shouldContinue() {
            if (creature.getTarget() != null && creature.canBeSeenByEntity(creature.getTarget())) {
                creature.setTarget(null);
                return false;
            }
            return super.shouldContinue();
        }

        protected void attack(LivingEntity target, double squaredDistance) {
            double d = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= d /*&& this.field_24667 <= 0*/) {
                this.method_28346();
                this.mob.swingHand(Hand.MAIN_HAND);
                this.mob.tryAttack(target);
                mob.setTarget(null);
                //enemy.setMotion(new Vector3d(0, enemy.jumpMovementFactor * 1.2, 0));
            }
        }
    }
}

