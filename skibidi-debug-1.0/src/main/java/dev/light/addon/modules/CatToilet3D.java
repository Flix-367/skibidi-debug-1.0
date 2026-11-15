package dev.light.addon.modules;

import dev.light.addon.SkibidiAddon;
import dev.light.addon.entity.CatToiletEntity;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;

public class CatToilet3D extends Module {

    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<Double> scale = sg.add(new DoubleSetting.Builder()
        .name("scale").description("Model scale.").defaultValue(1.0).min(0.1).max(5.0).build());

    private final Setting<Double> offsetX = sg.add(new DoubleSetting.Builder()
        .name("offset-x").description("X offset from player.").defaultValue(2.0).min(-10.0).max(10.0).build());

    private final Setting<Double> offsetY = sg.add(new DoubleSetting.Builder()
        .name("offset-y").description("Y offset from player.").defaultValue(0.0).min(-5.0).max(5.0).build());

    private final Setting<Double> offsetZ = sg.add(new DoubleSetting.Builder()
        .name("offset-z").description("Z offset from player.").defaultValue(0.0).min(-10.0).max(10.0).build());

    private final Setting<Boolean> followPlayer = sg.add(new BoolSetting.Builder()
        .name("follow-player").description("Model follows player.").defaultValue(true).build());

    private final Setting<Boolean> facePlayer = sg.add(new BoolSetting.Builder()
        .name("face-player").description("Model faces player.").defaultValue(true).build());

    private CatToiletEntity entity = null;

    public CatToilet3D() {
        super(SkibidiAddon.CATEGORY, "cat-toilet-3d", "Renders the Cat Toilet 3D model in-game.");
    }

    @Override
    public void onActivate() {
        if (mc.world == null || mc.player == null) return;
        spawnEntity();
    }

    @Override
    public void onDeactivate() {
        removeEntity();
    }

    private void spawnEntity() {
        if (mc.world == null || mc.player == null) return;
        entity = new CatToiletEntity(SkibidiAddon.CAT_TOILET_TYPE, mc.world);
        Vec3d pos = mc.player.getPos().add(offsetX.get(), offsetY.get(), offsetZ.get());
        entity.setPosition(pos);
        entity.setYaw(mc.player.getYaw() + 180);
        mc.world.addEntity(entity);
        info("§aCat Toilet spawned.");
    }

    private void removeEntity() {
        if (entity != null) {
            entity.discard();
            entity = null;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (entity == null || mc.player == null || mc.world == null) return;

        // respawn if removed by server
        if (entity.isRemoved()) { spawnEntity(); return; }

        if (followPlayer.get()) {
            Vec3d pos = mc.player.getPos().add(offsetX.get(), offsetY.get(), offsetZ.get());
            entity.setPosition(pos);
        }

        if (facePlayer.get()) {
            entity.setYaw(mc.player.getYaw() + 180);
        }
    }
}
