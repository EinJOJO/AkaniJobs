package it.einjojo.jobs.db;

import it.einjojo.jobs.player.JobPlayer;
import it.einjojo.jobs.player.progression.PlayerJobProgression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface JobStorage {

    boolean init();

    void saveJobProgression(@NotNull PlayerJobProgression progression);

    void saveJobPlayer(JobPlayer jobPlayer);

    JobPlayer loadJobPlayer(UUID player);

    @Nullable
    PlayerJobProgression loadJobProgression(UUID player, String jobName);


}
