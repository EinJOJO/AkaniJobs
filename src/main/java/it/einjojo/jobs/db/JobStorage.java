package it.einjojo.jobs.db;

import it.einjojo.jobs.Job;
import it.einjojo.jobs.player.JobPlayer;
import it.einjojo.jobs.player.JobPlayerImpl;
import it.einjojo.jobs.player.progression.PlayerJobProgression;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface JobStorage {

    boolean init();

    void saveJobProgression(@NotNull PlayerJobProgression progression);


    void saveJobPlayer(@NotNull JobPlayer jobPlayer);

    @NotNull
    JobPlayerImpl loadJobPlayer(@NotNull UUID player);

    @NotNull
    PlayerJobProgression loadJobProgression(@NotNull UUID player, @NotNull Job job);


}
