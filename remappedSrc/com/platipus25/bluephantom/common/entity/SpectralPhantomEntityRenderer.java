package com.platipus25.bluephantom.common.entity;


import com.platipus25.bluephantom.BluePhantom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpectralPhantomEntityRenderer extends MobEntityRenderer<SpectralPhantomEntity, SpectralPhantomModel<SpectralPhantomEntity>> {
    private static final Identifier TEXTURE = new Identifier(BluePhantom.MODID,"textures/entity/green_pig.png");

    public SpectralPhantomEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SpectralPhantomModel<>(), 0.7F);
        //this.addFeature(new SaddleFeatureRenderer(this, new PigEntityModel(0.5F), new Identifier("textures/entity/pig/pig_saddle.png")));
    }

    public Identifier getTexture(SpectralPhantomEntity spectralPhantomEntity) {
        return TEXTURE;
    }
}
