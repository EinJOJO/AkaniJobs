package it.einjojo.jobs.player;

import it.einjojo.jobs.Job;
import it.einjojo.jobs.Jobs;
import it.einjojo.jobs.handler.AbstractJobHandler;
import it.einjojo.jobs.handler.JobHandlerRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class JobChangeObserverImpl implements JobChangeObserver {

    private final Jobs jobs;

    public JobChangeObserverImpl(Jobs jobs) {
        this.jobs = jobs;
    }

    @Override
    public void onJobChange(@NotNull JobPlayerImpl jobHolder, @Nullable Job oldJob, @Nullable Job newJob) {
        jobs.activeProgressions().synchronous().invalidate(jobHolder.playerUuid());
        updateJobHandlers(jobHolder.playerUuid(), oldJob, newJob);
    }

    private void updateJobHandlers(UUID player, @Nullable Job oldJob , @Nullable Job newJob) {
        AbstractJobHandler oldHandler = jobHandlerRegistry().handler(oldJob);
        if (oldHandler != null) {
            oldHandler.untrackPlayer(player);
        }
        AbstractJobHandler newHandler = jobHandlerRegistry().handler(newJob);
        if (newHandler != null) {
            newHandler.trackPlayer(player);
        }
    }

    private JobHandlerRegistry jobHandlerRegistry() {
        return jobs.handlerRegistry();
    }
}
