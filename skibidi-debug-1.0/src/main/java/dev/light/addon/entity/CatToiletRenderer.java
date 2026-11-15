package dev.light.addon.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CatToiletRenderer extends GeoEntityRenderer<CatToiletEntity> {

    public CatToiletRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CatToiletModel());
    }
}
