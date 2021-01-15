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

public class SpectralPhantomEntity extends TameableEntity implements ItemSteerable, JumpingMount, Saddleable {
    private static final Logger LOGGER = LogManager.getLogger(BluePhantom.MODID + SpectralPhantomEntity.class.getName());
    private static final Ingredient TEMPTING_ITEMS = Ingredient.ofItems(Items.CARROT_ON_A_STICK, Items.POTATO, Items.BAKED_POTATO);
    private static final Predicate<ItemStack> OFFENSIVE_ITEMS = Ingredient.ofItems(Items.SADDLE, Items.LEAD).or((itemStack) -> {
        FoodComponent foodComponent = itemStack.getItem().getFoodComponent();
        return foodComponent != null && foodComponent.isMeat();
    });
    private static final Ingredient TAMING_ITEMS = Ingredient.ofItems(Items.POTATO);
    private static final Ingredient BREEDING_ITEMS = Ingredient.ofItems(Items.POTATO, Items.WHEAT, Items.BAKED_POTATO);
    //private final BoostHelper boostHelper = new BoostHelper(this.dataManager, BOOST_TIME, SADDLED);
    private static final TrackedData<Integer> BOOST_TIME;
    private static final TrackedData<Boolean> SADDLED;
    private int eatingCooldown = 0;
    private SitGoal sitGoal = null;
    private final SaddledComponent saddledComponent;

    static {
        BOOST_TIME = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.INTEGER);
        SADDLED = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    public SpectralPhantomEntity(EntityType<? extends SpectralPhantomEntity> entityIn, World world) {
        super(entityIn, world);
        this.setTamed(false);
        this.setSitting(false);

        this.saddledComponent = new SaddledComponent(this.dataTracker, BOOST_TIME, SADDLED);
    }

    @Override
    protected void initGoals() {
        this.sitGoal = new SitGoal(this);
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.5D));
        this.goalSelector.add(2, this.sitGoal);
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new LeapAtFoodGoal(this, 0.2F, 50));
        this.goalSelector.add(3, new TemptGoal(this, 1.0D, TEMPTING_ITEMS, false));
        this.goalSelector.add(3, new AvoidOffensiveItemsGoal(this, 16f, 1.2D, 2D, OFFENSIVE_ITEMS));
        this.goalSelector.add(7, new NuzzlePlayerGoal(this, 0.75D, false));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(5, new FleeEntityGoal<>(this, SpectralPhantomEntity.class, 6.0F, 1.0D, 1.0D));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 0.4D, 300.0F, 10.0F, false));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        //this.targetSelector.add(2, new OwnerHurtTargetGoal(this));

        this.targetSelector.add(4, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, true, (entity) -> !this.canBeSeenByEntity(entity) && !this.hasPlayerRider()));
        super.initGoals();
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

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_GHAST_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_COW_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_COW_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15F, 1.0F);
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return (SpectralPhantomEntity) getType().create(serverWorld);
    }

    public static DefaultAttributeContainer.Builder createSpectralPhantomAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.1).add(EntityAttributes.GENERIC_ATTACK_SPEED, 10.0);
    }

    public void onTrackedDataSet(TrackedData<?> data) {
        if (BOOST_TIME.equals(data) && this.world.isClient) {
            this.saddledComponent.boost();
        }

        super.onTrackedDataSet(data);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BOOST_TIME, 0);
        this.dataTracker.startTracking(SADDLED, false);
    }

    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        this.saddledComponent.toTag(tag);
    }

    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.saddledComponent.fromTag(tag);
    }

    /*protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.1D);
    }*/


    public boolean isTamingItem(ItemStack stack) {
        return TAMING_ITEMS.test(stack);
    }

    public boolean isOffensiveItem(ItemStack stack) {
        return OFFENSIVE_ITEMS.test(stack);
    }

    public boolean isTemptingItem(ItemStack stack) {
        return TEMPTING_ITEMS.test(stack);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_ITEMS.test(stack);
    }


    /**
     * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
     * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
     */
    @Override
    public Entity getPrimaryPassenger() {
        return this.getPassengerList().isEmpty() ? null : this.getPassengerList().get(0);
    }

    /**
     * returns true if all the conditions for steering the entity are met.
     */

    public boolean canBeControlledByRider() {
        Entity entity = this.getPrimaryPassenger();

        if (entity == null) {
            return false;
        }

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            for (ItemStack itemStack : player.getItemsHand()) {
                if (this.isTemptingItem(itemStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setJumpStrength(int strength) {
    }

    @Override
    public boolean canJump() {
        return this.hasPlayerRider() && this.canBeControlledByRider();
    }

    @Override
    public void startJumping(int height) {
        this.jump();
    }

    @Override
    public void stopJumping() {
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        player.getItemsHand();
        ItemStack itemStack = player.getStackInHand(hand);

        if (this.isTamingItem(itemStack) && !this.isTamed()) {

            if (this.getRandom().nextInt(6) == 0) {
                this.setOwner(player);
                LOGGER.info("Tamed!");
            }
            this.eat(player, itemStack);
            return ActionResult.CONSUME;
        }

        if (this.eatingCooldown <= 0 && getHealth() < getMaxHealth()) {
            this.eat(player, itemStack);
            this.eatingCooldown = 50;
            return ActionResult.CONSUME;
        }

        /*if (itemStack.getItem() == Items.SADDLE && player == this.getOwner()) {
            if (this.world.isClient) {
                player.startRiding(this);
            }
            return ActionResult.success(this.world.isClient);
        }

        if (itemStack.isEmpty()) {
            this.setSitting(!this.isSitting());
        }*/
        if (itemStack.getItem() == Items.SADDLE) {
            itemStack.useOnEntity(player, this, hand) ;
        }

        if (itemStack.isEmpty()) {
            if (isSaddled() && this.world.isClient) {
                player.startRiding(this);
            } else {
                this.setSitting(!this.isSitting());
            }
            return ActionResult.SUCCESS;
        }


        return super.interactMob(player, hand);
    }

    public void mobTick() {
        if (this.eatingCooldown > 0) {
            --this.eatingCooldown;
        }
        LOGGER.info(Arrays.toString(goalSelector.getRunningGoals().map((PrioritizedGoal g) -> g.getGoal().getClass().getSimpleName()).toArray()));
        /*if (isTamed()) {
            LOGGER.info(distanceTo(getLovingPlayer()));
        }*/
        super.mobTick();
    }

    @Override
    public boolean consumeOnAStickItem() {
        return this.getRandom().nextInt(30) == 0;
    }

    @Override
    public void setMovementInput(Vec3d movementInput) {
        super.travel(movementInput);
    }

    @Override
    public void travel(Vec3d movementInput) {
        //this.setMovementSpeed(this.getSpeed());
        this.travel(this, this.saddledComponent, movementInput);
    }

    @Override
    public float getSaddledSpeed() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.5f;
    }

    @Override
    public boolean canBeSaddled() {
        return this.isTamed();
    }

    @Override
    public void saddle(SoundCategory sound) {
        this.saddledComponent.setSaddled(true);
        if (sound != null) {
            this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_PIG_SADDLE, sound, 0.5F, 1.0F);
        }
    }

    @Override
    public boolean isSaddled() {
        return saddledComponent.isSaddled();
    }


    private static class SitMaybeGoal extends SitGoal {
        private final TameableEntity tameable;
        private final int getupChance;

        public SitMaybeGoal(TameableEntity entityIn, int sittingChanceIn) {
            super(entityIn);
            this.tameable = entityIn;
            this.getupChance = sittingChanceIn;
        }

        @Override
        public boolean shouldContinue() {
            if (this.tameable.getRandom().nextInt(getupChance) == 0) {
                return false;
            }
            return super.shouldContinue();
        }
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

