package dev.light.addon.entity;

import net.minecraft.entity.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CatToiletEntity extends Entity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CatToiletEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip      = true;
        this.setNoGravity(true);
        this.setInvisible(false);
    }

    @Override protected void initDataTracker(DataTracker.Builder b) {}
    @Override protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override protected void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar reg) {
        reg.add(new AnimationController<>(this, "idle", 0, state ->
            state.setAndContinue(
                RawAnimation.begin().thenLoop("animation.cat_toilet.idle")
            )
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
