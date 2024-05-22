package it.einjojo.jobs.player;

import it.einjojo.jobs.Job;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface JobChangeObserver {

    void onJobChange(@NotNull JobPlayer jobHolder, @Nullable Job oldJob, @Nullable Job newJob);

}
