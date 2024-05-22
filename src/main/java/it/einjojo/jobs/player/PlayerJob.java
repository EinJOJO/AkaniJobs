package it.einjojo.jobs.player;

import it.einjojo.jobs.player.progression.PlayerJobProgression;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerJob {

    UUID playerUuid();

    @Nullable
    String currentJob();

    void setCurrentJob(@Nullable String newJob);

    PlayerJobProgression progression();

    CompletableFuture<PlayerJobProgression> progressionAsync();

}
