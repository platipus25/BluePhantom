package com.platipus25.bluephantom;

import com.platipus25.bluephantom.common.entity.SpectralPhantomEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class BluePhantomClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        /*
         * Registers our Pig Entity's renderer, which provides a model and texture for the entity.
         *
         * Entity Renderers can also manipulate the model before it renders based on entity context (EndermanEntityRenderer#render).
         */
        EntityRendererRegistry.INSTANCE.register(BluePhantom.spectral_phantom, (dispatcher, context) -> new SpectralPhantomEntityRenderer(dispatcher));
    }
}
