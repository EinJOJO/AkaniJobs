package it.einjojo.jobs;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.einjojo.jobs.db.JobStorage;
import it.einjojo.jobs.handler.JobHandlerRegistry;
import it.einjojo.jobs.player.JobChangeObserver;
import it.einjojo.jobs.player.JobChangeObserverImpl;
import it.einjojo.jobs.player.JobPlayer;
import it.einjojo.jobs.player.JobPlayerImpl;
import it.einjojo.jobs.player.progression.JobProgression;
import it.einjojo.jobs.player.progression.JobProgressionObserver;
import it.einjojo.jobs.player.progression.JobProgressionObserverImpl;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Jobs {
    private final JobStorage storage;
    private final JobProgressionObserver progressionObserver;
    private final JobChangeObserver currentJobObserver;
    private final JobHandlerRegistry handlerRegistry = new JobHandlerRegistry(this);
    private final AsyncLoadingCache<UUID, JobPlayer> jobPlayers = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(3))
            .buildAsync(this::loadPlayer);
    private final AsyncLoadingCache<UUID, Optional<JobProgression>> activeProgressions = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(3))
            .buildAsync(this::loadProgression);

    public Jobs(JobStorage storage, DataSaveTask saveTask) {
        this.storage = storage;
        this.progressionObserver = new JobProgressionObserverImpl(saveTask);
        this.currentJobObserver = new JobChangeObserverImpl(this, saveTask);
    }

    public CompletableFuture<JobPlayerImpl> loadPlayer(UUID uuid, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            var jobPlayer = storage.loadJobPlayer(uuid);
            jobPlayer.setJobs(this);
            jobPlayer.setCurrentJobObserver(currentJobObserver);
            return (jobPlayer);
        }, executor);
    }

    public CompletableFuture<Optional<JobProgression>> loadProgression(UUID key, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            var jobPlayer = jobPlayers.synchronous().get(key);
            Job currentJob = jobPlayer.currentJob();
            if (currentJob == null) {
                return Optional.empty();
            }
            var progression = storage.loadJobProgression(key, currentJob);
            progression.setObserver(progressionObserver);
            return Optional.of(progression);
        }, executor);
    }


    public JobStorage storage() {
        return storage;
    }

    public AsyncLoadingCache<UUID, JobPlayer> jobPlayers() {
        return jobPlayers;
    }


    public JobProgressionObserver progressionObserver() {
        return progressionObserver;
    }

    public JobChangeObserver currentJobObserver() {
        return currentJobObserver;
    }

    public JobHandlerRegistry handlerRegistry() {
        return handlerRegistry;
    }

    public AsyncLoadingCache<UUID, Optional<JobProgression>> activeProgressions() {
        return activeProgressions;
    }
}
