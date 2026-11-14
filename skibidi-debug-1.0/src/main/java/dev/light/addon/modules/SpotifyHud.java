package dev.light.addon.modules;

import de.labystudio.spotifyapi.SpotifyAPI;
import de.labystudio.spotifyapi.SpotifyAPIFactory;
import de.labystudio.spotifyapi.SpotifyListenerAdapter;
import de.labystudio.spotifyapi.model.Track;
import dev.light.addon.SkibidiAddon;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.util.math.MatrixStack;

public class SpotifyHud extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgHud     = settings.createGroup("HUD");

    private final Setting<Boolean> showProgress = sgGeneral.add(new BoolSetting.Builder()
        .name("show-progress").description("Show progress bar.").defaultValue(true).build());

    private final Setting<Boolean> showEqualizer = sgGeneral.add(new BoolSetting.Builder()
        .name("show-equalizer").description("Show EQ bars.").defaultValue(true).build());

    private final Setting<Integer> hudX = sgHud.add(new IntSetting.Builder()
        .name("x").description("HUD X position.").defaultValue(10).min(0).max(1920).build());

    private final Setting<Integer> hudY = sgHud.add(new IntSetting.Builder()
        .name("y").description("HUD Y position.").defaultValue(10).min(0).max(1080).build());

    private final Setting<Integer> panelWidth = sgHud.add(new IntSetting.Builder()
        .name("width").description("Panel width.").defaultValue(240).min(120).max(600).build());

    private SpotifyAPI api      = null;
    private Track      track    = null;
    private int        position = 0;
    private boolean    playing  = false;

    private final float[] barHeights = new float[12];
    private final float[] barPhase   = new float[12];
    private final java.util.Random rng = new java.util.Random();

    public SpotifyHud() {
        super(SkibidiAddon.CATEGORY, "spotify-hud",
              "Shows the track playing in the desktop Spotify app.");
        for (int i = 0; i < barPhase.length; i++)
            barPhase[i] = rng.nextFloat() * 6.28f;
    }

    @Override
    public void onActivate() {
        try {
            api = SpotifyAPIFactory.create();
            api.registerListener(new SpotifyListenerAdapter() {
                @Override public void onTrackChanged(Track t)      { track = t; }
                @Override public void onPositionChanged(int pos)   { position = pos; }
                @Override public void onPlayBackChanged(boolean p) { playing = p; }
                @Override public void onConnect()                  { info("§aSpotify connected."); }
                @Override public void onDisconnect(Exception e)    { info("§cSpotify disconnected."); }
            });
            api.initialize();
        } catch (Exception e) {
            error("Failed to connect to Spotify: " + e.getMessage());
        }
    }

    @Override
    public void onDeactivate() {
        if (api != null) {
            try { api.stop(); } catch (Exception ignored) {}
            api   = null;
            track = null;
        }
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        if (track == null) return;

        MatrixStack matrices = event.matrices;
        int px = hudX.get(), py = hudY.get();
        int w  = panelWidth.get();
        int h  = 52;

        // background
        Renderer2D.COLOR.begin();
        Renderer2D.COLOR.quad(matrices, px, py, w, h, 15, 15, 15, 200);
        Renderer2D.COLOR.render(matrices);

        // progress bar
        if (showProgress.get() && track.getLength() > 0) {
            float frac = Math.min(1f, (float) position / track.getLength());
            int barY = py + h - 6;
            Renderer2D.COLOR.begin();
            Renderer2D.COLOR.quad(matrices, px + 4, barY, w - 8, 3, 50, 50, 50, 255);
            Renderer2D.COLOR.quad(matrices, px + 4, barY, (int)((w - 8) * frac), 3, 30, 215, 96, 255);
            Renderer2D.COLOR.render(matrices);
        }

        // EQ bars
        if (showEqualizer.get() && playing) {
            updateEq();
            int eqX = px + w - 36;
            int eqY = py + 8;
            Renderer2D.COLOR.begin();
            for (int i = 0; i < barHeights.length; i++) {
                int bh = (int)(barHeights[i] * 20);
                Renderer2D.COLOR.quad(matrices, eqX + i * 3, eqY + 20 - bh, 2, bh, 30, 215, 96, 200);
            }
            Renderer2D.COLOR.render(matrices);
        }

        // text
        mc.textRenderer.drawWithShadow(matrices, "♪ " + track.getName(),   px + 5, py + 5,  0xFFFFFF);
        mc.textRenderer.drawWithShadow(matrices, track.getArtist(),         px + 5, py + 16, 0xAAAAAA);
        mc.textRenderer.drawWithShadow(matrices, fmt(position) + " / " + fmt(track.getLength()),
                                                                            px + 5, py + 27, 0x888888);
        mc.textRenderer.drawWithShadow(matrices, playing ? "▶" : "⏸",     px + 5, py + 38, 0x1ED760);
    }

    private void updateEq() {
        double t = System.currentTimeMillis() / 1000.0;
        for (int i = 0; i < barHeights.length; i++)
            barHeights[i] = (float)(0.4 + 0.6 * Math.abs(Math.sin(t * (1.5 + i * 0.3) + barPhase[i])));
    }

    private static String fmt(int ms) {
        int s = ms / 1000;
        return String.format("%d:%02d", s / 60, s % 60);
    }
}
