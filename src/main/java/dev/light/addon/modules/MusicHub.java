package dev.light.addon.modules;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import dev.light.addon.SkibidiAddon;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicHub extends Module {

    private final SettingGroup sgGeneral  = settings.getDefaultGroup();
    private final SettingGroup sgPlayback = settings.createGroup("Playback");

    private final Setting<String> musicFolder = sgGeneral.add(new StringSetting.Builder()
        .name("music-folder")
        .description("Absolute path to folder containing .wav / .au files.")
        .defaultValue(System.getProperty("user.home") + File.separator + "music")
        .build()
    );

    private final Setting<Double> volume = sgPlayback.add(new DoubleSetting.Builder()
        .name("volume")
        .description("Playback volume (0.0 - 1.0).")
        .defaultValue(0.5).min(0.0).max(1.0).sliderMax(1.0)
        .build()
    );

    private final Setting<Boolean> loop = sgPlayback.add(new BoolSetting.Builder()
        .name("loop").description("Loop the playlist.").defaultValue(true).build()
    );

    private final Setting<Boolean> shuffle = sgPlayback.add(new BoolSetting.Builder()
        .name("shuffle").description("Shuffle playlist on load.").defaultValue(false).build()
    );

    private Clip         currentClip   = null;
    private List<File>   playlist      = new ArrayList<>();
    private int          trackIndex    = 0;
    private boolean      paused        = false;
    private long         pausePosition = 0L;

    public MusicHub() {
        super(SkibidiAddon.CATEGORY, "music-hub",
              "Plays local audio files in-game.");
    }

    @Override
    public void onActivate() {
        loadPlaylist();
        if (!playlist.isEmpty()) playTrack(trackIndex);
    }

    @Override
    public void onDeactivate() {
        stopCurrent();
        playlist.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (currentClip == null || paused) return;
        if (!currentClip.isRunning()) nextTrack();
    }

    public void nextTrack() {
        stopCurrent();
        if (playlist.isEmpty()) return;
        trackIndex = (trackIndex + 1) % playlist.size();
        if (trackIndex == 0 && !loop.get()) { toggle(); return; }
        playTrack(trackIndex);
    }

    public void prevTrack() {
        stopCurrent();
        if (playlist.isEmpty()) return;
        trackIndex = (trackIndex - 1 + playlist.size()) % playlist.size();
        playTrack(trackIndex);
    }

    public void togglePause() {
        if (currentClip == null) return;
        if (paused) {
            currentClip.setMicrosecondPosition(pausePosition);
            applyVolume(currentClip);
            currentClip.start();
            paused = false;
            info("Resumed: §a" + currentTrackName());
        } else {
            pausePosition = currentClip.getMicrosecondPosition();
            currentClip.stop();
            paused = true;
            info("Paused: §e" + currentTrackName());
        }
    }

    public void reloadPlaylist() {
        stopCurrent();
        loadPlaylist();
        trackIndex = 0;
        if (!playlist.isEmpty()) playTrack(trackIndex);
    }

    private void loadPlaylist() {
        playlist.clear();
        File dir = new File(musicFolder.get());
        if (!dir.isDirectory()) {
            error("music-folder not found: §c" + dir.getAbsolutePath());
            return;
        }
        File[] files = dir.listFiles(f -> {
            String n = f.getName().toLowerCase();
            return n.endsWith(".wav") || n.endsWith(".au");
        });
        if (files == null || files.length == 0) {
            warning("No .wav/.au files in §e" + dir.getAbsolutePath());
            return;
        }
        for (File f : files) playlist.add(f);
        if (shuffle.get()) java.util.Collections.shuffle(playlist);
        info("Loaded §a" + playlist.size() + "§r tracks.");
    }

    private void playTrack(int index) {
        if (index < 0 || index >= playlist.size()) return;
        File f = playlist.get(index);
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            currentClip = AudioSystem.getClip();
            currentClip.open(ais);
            applyVolume(currentClip);
            currentClip.start();
            paused = false;
            info("Now playing: §b" + f.getName());
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            error("Failed: §c" + f.getName() + " -- " + e.getMessage());
            nextTrack();
        }
    }

    private void stopCurrent() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
        paused = false;
        pausePosition = 0L;
    }

    private void applyVolume(Clip clip) {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) return;
        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float)(20.0 * Math.log10(Math.max(volume.get(), 0.0001)));
        dB = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));
        gain.setValue(dB);
    }

    private String currentTrackName() {
        if (trackIndex < 0 || trackIndex >= playlist.size()) return "none";
        return playlist.get(trackIndex).getName();
    }
}
