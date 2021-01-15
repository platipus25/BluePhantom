package com.platipus25.bluephantom;

import net.fabricmc.api.ModInitializer;
import com.platipus25.bluephantom.common.entity.SpectralPhantomEntity;
import com.platipus25.bluephantom.common.entity.SpectralPhantomEntityRenderer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

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
        /*
         * Register our Cube Entity's default attributes.
         * Attributes are properties or stats of the mobs, including things like attack damage and health.
         * The game will crash if the entity doesn't have the proper attributes registered in time.
         *
         * In 1.15, this was done by a method override inside the entity class.
         * Most vanilla entities have a static method (eg. ZombieEntity#createZombieAttributes) for initializing their attributes.
         */
        FabricDefaultAttributeRegistry.register(spectral_phantom, SpectralPhantomEntity.createSpectralPhantomAttributes());


        System.out.println("Hello Fabric world!");
    }

    /*private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        for (Biome biome : Biome.BIOMES) {
            if (biome != null) {
                biome.getSpawns(spectral_phantom.getClassification()).add(new Biome.SpawnListEntry(spectral_phantom, 12, 1, 100));
            }
        }
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        RenderingRegistry.registerEntityRenderingHandler(spectral_phantom, SpectralPhantomEntityRenderer::new);
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo(SpectralMobs.MODID, "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(modid = SpectralMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            // register a new item here
            LOGGER.info("HELLO from Register Item");
            itemRegistryEvent.getRegistry().registerAll(
                    setup(new SwordItem(ItemTier.DIAMOND, 2, 10f, new Item.Properties().group(ItemGroup.COMBAT)), "an_item")
            );
        }

        @SubscribeEvent
        public static void onEntitiesRegistry(final RegistryEvent.Register<EntityType<?>> entityRegistryEvent) {
            // register entity here

            LOGGER.info("HELLO from Register Entities");
            EntityType.Builder<SpectralPhantomEntity> spectralPhantomEntityTypeBuilder = EntityType.Builder.create(SpectralPhantomEntity::new, EntityClassification.CREATURE).size(0.7F, 0.9F);
            EntityType<SpectralPhantomEntity> spectralPhantomEntityType = spectralPhantomEntityTypeBuilder.build("spectral_phantom");

            entityRegistryEvent.getRegistry().registerAll(
                    setup(spectralPhantomEntityType, "spectral_phantom")
            );

        }

        public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final String name) {
            return setup(entry, new ResourceLocation(SpectralMobs.MODID, name));
        }

        public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final ResourceLocation registryName) {
            entry.setRegistryName(registryName);
            return entry;
        }
    }*/
}
