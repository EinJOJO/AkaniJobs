package it.einjojo.jobs.listener;

import it.einjojo.jobs.DataSaveTask;
import it.einjojo.jobs.Job;
import it.einjojo.jobs.Jobs;
import it.einjojo.jobs.db.JobStorage;
import it.einjojo.jobs.player.JobPlayer;
import it.einjojo.jobs.player.progression.JobProgression;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ConnectionListener implements Listener {

    private final JavaPlugin plugin;
    private final JobStorage storage;
    private final DataSaveTask dataSaveTask;
    private final Jobs jobs;

    public ConnectionListener(JavaPlugin plugin, JobStorage storage, DataSaveTask dataSaveTask, Jobs jobs) {
        this.plugin = plugin;
        this.storage = storage;
        this.dataSaveTask = dataSaveTask;
        this.jobs = jobs;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            UUID playerUuid = event.getPlayer().getUniqueId();
            JobPlayer jobPlayer = jobs.jobPlayers().synchronous().getIfPresent(playerUuid);
            if (jobPlayer != null) {
                jobs.jobPlayers().synchronous().invalidate(playerUuid);
                storage.saveJobPlayer(jobPlayer);
                dataSaveTask.remove(jobPlayer);
            }
            @Nullable Optional<JobProgression> progression = jobs.activeProgressions().synchronous().getIfPresent(playerUuid);
            if (progression != null && progression.isPresent()) {
                jobs.activeProgressions().synchronous().invalidate(playerUuid);
                storage.saveJobProgression(progression.get());
                dataSaveTask.remove(progression.get());
            }
            storage.unlockPlayer(playerUuid);
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        tryLoadPlayerIfNotLocked(playerUuid, 0);
    }

    public void tryLoadPlayerIfNotLocked(UUID playerUuid, int tries) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if (storage.isPlayerLocked(playerUuid)) {
                if (tries < 5) {
                    tryLoadPlayerIfNotLocked(playerUuid, tries + 1);
                    plugin.getSLF4JLogger().warn("Attempt {} - Player {} is locked, retrying in 10 ticks", tries, playerUuid);
                    return;
                } else {
                    plugin.getSLF4JLogger().warn("Player {} is locked, but max tries reached. Will overwrite cached values from storage", playerUuid);
                }
            }
            // overwrite to fast loaded data
            JobPlayer jobPlayer = storage.loadJobPlayer(playerUuid);
            jobs.jobPlayers().synchronous().put(playerUuid, jobPlayer);
            Job currentJob = jobPlayer.currentJob();
            if (currentJob != null) {
                JobProgression progression = storage.loadJobProgression(playerUuid, currentJob);
                jobs.activeProgressions().synchronous().put(playerUuid, Optional.of(progression));
            }
            storage.lockPlayer(playerUuid); // Player belongs now to this server. If switching servers, the player will be unlocked and data will be loaded again.
        }, 10L);
    }
}
