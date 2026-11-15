package dev.light.addon.entity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CatToiletModel extends GeoModel<CatToiletEntity> {

    @Override
    public Identifier getModelResource(CatToiletEntity e) {
        return Identifier.of("skibidi-debug", "geo/cat_toilet.geo.json");
    }

    @Override
    public Identifier getTextureResource(CatToiletEntity e) {
        return Identifier.of("skibidi-debug", "textures/entity/cat_toilet.png");
    }

    @Override
    public Identifier getAnimationResource(CatToiletEntity e) {
        return Identifier.of("skibidi-debug", "animations/cat_toilet.animation.json");
    }
}
