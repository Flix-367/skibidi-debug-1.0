package dev.light.addon;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Category;
import dev.light.addon.modules.MusicHub;

public class SkibidiAddon extends MeteorAddon {

    public static final Category CATEGORY = new Category("Skibidi Debug");

    @Override
    public void onInitialize() {
        Modules.get().add(new MusicHub());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }
}
