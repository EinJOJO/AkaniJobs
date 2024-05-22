package it.einjojo.jobs.player;

import it.einjojo.jobs.Job;
import it.einjojo.jobs.player.progression.PlayerJobProgression;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface JobPlayer {

    UUID playerUuid();

    @Nullable
    String currentJobName();

    @Nullable
    Job currentJob();

    /**
     * @param newJob The new job to set
     * @deprecated Use {@link #setCurrentJob(Job)} instead
     */
    @Deprecated
    void setCurrentJobName(@Nullable String newJob);

    void setCurrentJob(Job newJob);

    Optional<PlayerJobProgression> progression();

    CompletableFuture<Optional<PlayerJobProgression>> progressionAsync();

}
