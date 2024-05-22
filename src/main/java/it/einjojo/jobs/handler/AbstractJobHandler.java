package it.einjojo.jobs.handler;

import it.einjojo.jobs.Job;
import it.einjojo.jobs.Jobs;
import it.einjojo.jobs.player.progression.JobProgression;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractJobHandler {

    protected final Set<UUID> trackingPlayers;
    protected transient Jobs jobs;

    public AbstractJobHandler() {
        this.trackingPlayers = new HashSet<>();
    }

    public void untrackPlayer(UUID player) {
        trackingPlayers.remove(player);
    }

    public void trackPlayer(UUID player) {
        trackingPlayers.add(player);
    }

    public void setJobs(Jobs jobs) {
        this.jobs = jobs;
    }

    public boolean isTracked(Player player) {
        return isTracked(player.getUniqueId());
    }

    public boolean isTracked(UUID player) {
        return trackingPlayers.contains(player);
    }

    public @NotNull Jobs jobs() {
        if (jobs == null) {
            throw new IllegalStateException("JobsHandler is not registered");
        }
        return jobs;
    }


    /**
     * Get the player's job progression object
     *
     * @param uuid The player's UUID
     * @return The player's job progression object
     * @throws IllegalStateException If the player is not tracking the correct job progression object
     */
    protected CompletableFuture<JobProgression> playerJobProgression(UUID uuid) {
        return jobs.activeProgressions().get(uuid)
                .thenApply((opt) -> opt.orElseThrow(() -> new IllegalStateException("Expected the tracking player to have a progression object")))
                .thenApply((progression) -> {
                    if (progression.job().equals(handlingJob())) {
                        return progression;
                    }
                    throw new IllegalStateException("Expected the player to be tracking the correct job progression object");
                });
    }

    abstract Job handlingJob();

}
