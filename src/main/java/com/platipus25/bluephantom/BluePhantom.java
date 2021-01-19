package com.platipus25.bluephantom;

import com.platipus25.bluephantom.common.entity.SpectralPhantomEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.SpawnSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
public class BluePhantom implements ModInitializer {
    public static final String MODID = "bluephantom";

    public static final EntityType<SpectralPhantomEntity> spectral_phantom = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("bluephantom", "spectral_phantom"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, SpectralPhantomEntity::new).dimensions(EntityDimensions.fixed(0.9f, 0.9f)).build()
    );

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        /*
         * Register our Pig Entity's default attributes.
         * Attributes are properties or stats of the mobs, including things like attack damage and health.
         * The game will crash if the entity doesn't have the proper attributes registered in time.
         *
         * In 1.15, this was done by a method override inside the entity class.
         * Most vanilla entities have a static method (eg. ZombieEntity#createZombieAttributes) for initializing their attributes.
         */
        FabricDefaultAttributeRegistry.register(spectral_phantom, SpectralPhantomEntity.createSpectralPhantomAttributes());

        BiomeModifications.create(new Identifier("bluephantom:biome_mods")).add(ModificationPhase.ADDITIONS, BiomeSelectors.spawnsOneOf(EntityType.PIG), biomeModificationContext -> biomeModificationContext.getSpawnSettings().addSpawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(spectral_phantom, 8, 4, 10)));

        LOGGER.debug("The pig has landed.");
    }
}
