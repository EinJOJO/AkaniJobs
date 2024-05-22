package it.einjojo.jobs.player;

import org.jetbrains.annotations.Nullable;

public interface JobChangeObserver {

    void onJobChange(PlayerJobImpl oldJob, @Nullable String oldJobName, @Nullable String newJobName);

}
