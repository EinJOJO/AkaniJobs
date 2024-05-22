package it.einjojo.jobs.player;

import it.einjojo.jobs.Jobs;
import it.einjojo.jobs.player.progression.PlayerJobProgression;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerJobImpl implements PlayerJob {
    private final UUID playerUuid;
    private @Nullable String currentJob;

    private transient @Nullable Jobs jobs;
    private transient JobChangeObserver currentJobObserver;


    public PlayerJobImpl(UUID playerUuid, @Nullable String currentJob) {
        this.playerUuid = playerUuid;
        this.currentJob = currentJob;
    }

    public Jobs jobs() {
        return jobs;
    }

    @Override
    public UUID playerUuid() {
        return playerUuid;
    }

    @Override
    public @Nullable String currentJob() {
        return currentJob;
    }

    @Override
    public void setCurrentJob(@Nullable String currentJob) {
        if (Objects.equals(this.currentJob, currentJob)) {
            return;
        }
        if (currentJobObserver != null) {
            currentJobObserver.onJobChange(this, this.currentJob, currentJob);
        }
        this.currentJob = currentJob;
    }

    public void setJobs(@Nullable Jobs jobs) {
        this.jobs = jobs;
    }

    public PlayerJobProgression progression() {
        if (jobs == null) {
            throw new IllegalStateException("Jobs instance not set");
        }
        return jobs.jobProgressions().synchronous().get(this);
    }

    public JobChangeObserver currentJobObserver() {
        return currentJobObserver;
    }

    public void setCurrentJobObserver(JobChangeObserver currentJobObserver) {
        this.currentJobObserver = currentJobObserver;
    }

    public CompletableFuture<PlayerJobProgression> progressionAsync() {
        if (jobs == null) {
            throw new IllegalStateException("Jobs instance not set");
        }
        return jobs.jobProgressions().get(this);
    }


    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PlayerJobImpl playerJob)) return false;

        return playerUuid().equals(playerJob.playerUuid()) && Objects.equals(currentJob(), playerJob.currentJob());
    }

    @Override
    public int hashCode() {
        int result = playerUuid().hashCode();
        result = 31 * result + Objects.hashCode(currentJob());
        return result;
    }

    @Override
    public String toString() {
        return "JobPlayer{" +
                "playerUUID=" + playerUuid() +
                ", currentJobName='" + currentJob() + '\'' +
                '}';
    }

}
