package it.einjojo.jobs.db;

import it.einjojo.jobs.player.PlayerJob;
import it.einjojo.jobs.player.PlayerJobImpl;
import it.einjojo.jobs.player.progression.PlayerJobProgression;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface JobStorage {

    boolean init();

    void saveJobProgression(@NotNull PlayerJobProgression progression);


    void saveJobPlayer(@NotNull PlayerJob playerJob);

    @NotNull
    PlayerJobImpl loadJobPlayer(@NotNull UUID player);

    @NotNull
    PlayerJobProgression loadJobProgression(@NotNull UUID player, @NotNull String jobName);


}
