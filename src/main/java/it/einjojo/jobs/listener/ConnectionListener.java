package it.einjojo.jobs.listener;

import it.einjojo.jobs.DataSaveTask;
import it.einjojo.jobs.Jobs;
import it.einjojo.jobs.db.JobStorage;
import it.einjojo.jobs.handler.AbstractJobHandler;
import it.einjojo.jobs.player.JobPlayer;
import it.einjojo.jobs.player.progression.JobProgression;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class ConnectionListener implements Listener {
    private static final Logger log = LoggerFactory.getLogger(ConnectionListener.class);
    private final JavaPlugin plugin;
    private final JobStorage storage;
    private final DataSaveTask dataSaveTask;
    private final Jobs jobs;

    public ConnectionListener(JavaPlugin plugin, JobStorage storage, DataSaveTask dataSaveTask, Jobs jobs) {
        this.plugin = plugin;
        this.storage = storage;
        this.dataSaveTask = dataSaveTask;
        this.jobs = jobs;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
                var handler = jobs.handlerRegistry().handler(jobPlayer.currentJob());
                if (handler != null) {
                    handler.untrackPlayer(playerUuid);
                }
            }
            @Nullable Optional<JobProgression> progression = jobs.activeProgressions().synchronous().getIfPresent(playerUuid);
            if (progression != null && progression.isPresent()) {
                jobs.activeProgressions().synchronous().invalidate(playerUuid);
                storage.saveJobProgression(progression.get());
                dataSaveTask.remove(progression.get());
                log.debug("Player {} progression saved and invalidated", playerUuid);
            }
            storage.unlockPlayer(playerUuid);
            log.debug("Player {} is unlocked and saved", playerUuid);
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
                    log.debug("Attempt {} - Player {} is locked, retrying in 10 ticks", tries, playerUuid);
                    return;
                } else {
                    log.warn("Player {} is locked, but max tries reached. Will overwrite cached values from storage", playerUuid);
                }
            }
            // overwrite to fast loaded data
            jobs.jobPlayers().synchronous().invalidate(playerUuid);
            jobs.activeProgressions().synchronous().invalidate(playerUuid);
            AbstractJobHandler handler = jobs.handlerRegistry().handler(jobs.jobPlayers().synchronous().get(playerUuid).currentJob());
            if (handler != null) {
                handler.trackPlayer(playerUuid);
            }
            storage.lockPlayer(playerUuid); // Player belongs now to this server. If switching servers, the player will be unlocked and data will be loaded again.
            plugin.getSLF4JLogger().debug("Successfully loaded player {} from storage and locked in", playerUuid);
        }, 10L);
    }
}
