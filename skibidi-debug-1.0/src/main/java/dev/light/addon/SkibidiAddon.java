package dev.light.addon;

import dev.light.addon.entity.*;
import dev.light.addon.modules.*;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Category;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.entity.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SkibidiAddon extends MeteorAddon {

    public static final Category CATEGORY = new Category("Skibidi Debug");
    public static EntityType<CatToiletEntity> CAT_TOILET_TYPE;

    @Override
    public void onInitialize() {
        // register entity type
        CAT_TOILET_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("skibidi-debug", "cat_toilet"),
            FabricEntityTypeBuilder.<CatToiletEntity>create(SpawnGroup.MISC, CatToiletEntity::new)
                .dimensions(EntityDimensions.fixed(1.0f, 1.5f))
                .build()
        );

        // register renderer (client-side)
        EntityRendererRegistry.register(CAT_TOILET_TYPE, CatToiletRenderer::new);

        // modules
        Modules.get().add(new MusicHub());
        Modules.get().add(new SpotifyHud());
        Modules.get().add(new CatToilet3D());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() { return "dev.light.addon"; }
}
