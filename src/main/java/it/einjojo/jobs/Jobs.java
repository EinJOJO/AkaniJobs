package it.einjojo.jobs;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.einjojo.jobs.db.JobStorage;
import it.einjojo.jobs.player.JobChangeObserver;
import it.einjojo.jobs.player.JobChangeObserverImpl;
import it.einjojo.jobs.player.PlayerJobImpl;
import it.einjojo.jobs.player.progression.JobProgressionObserver;
import it.einjojo.jobs.player.progression.JobProgressionObserverImpl;
import it.einjojo.jobs.player.progression.PlayerJobProgression;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Jobs {
    private final JobProgressionObserver progressionObserver = new JobProgressionObserverImpl();
    private final JobChangeObserver currentJobObserver = new JobChangeObserverImpl();
    private final JobStorage storage;
    private final AsyncLoadingCache<UUID, PlayerJobImpl> jobPlayers = Caffeine.newBuilder().buildAsync(this::loadPlayer);
    private final AsyncLoadingCache<UUID, PlayerJobProgression> activeProgressions = Caffeine.newBuilder().buildAsync(this::loadProgression);

    public Jobs(JobStorage storage) {
        this.storage = storage;
    }

    private CompletableFuture<PlayerJobImpl> loadPlayer(UUID uuid, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            var jobPlayer = storage.loadJobPlayer(uuid);
            jobPlayer.setJobs(this);
            jobPlayer.setCurrentJobObserver(currentJobObserver);
            return jobPlayer;
        }, executor);
    }

    private CompletableFuture<PlayerJobProgression> loadProgression(UUID key, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            var jobPlayer = jobPlayers.synchronous().get(key);
            if (jobPlayer.currentJob() == null) {
                return null;
            }
            var progression = storage.loadJobProgression(key, jobPlayer.currentJob());
            progression.setObserver(progressionObserver);
            return progression;
        }, executor);
    }


    public JobStorage storage() {
        return storage;
    }

    public AsyncLoadingCache<UUID, PlayerJobImpl> jobPlayers() {
        return jobPlayers;
    }

    public AsyncLoadingCache<UUID, PlayerJobProgression> jobProgressions() {
        return activeProgressions;
    }
}
