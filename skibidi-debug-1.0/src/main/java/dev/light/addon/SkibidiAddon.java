package dev.light.addon;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Category;
import dev.light.addon.modules.MusicHub;
import dev.light.addon.modules.SpotifyHud;

public class SkibidiAddon extends MeteorAddon {

    public static final Category CATEGORY = new Category("Skibidi Debug");

    @Override
    public void onInitialize() {
        Modules.get().add(new MusicHub());
        Modules.get().add(new SpotifyHud());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "dev.light.addon";
    }
}
