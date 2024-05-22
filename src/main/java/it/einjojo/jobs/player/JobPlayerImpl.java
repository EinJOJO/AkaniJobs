package it.einjojo.jobs.player;

import it.einjojo.jobs.Job;
import it.einjojo.jobs.Jobs;
import it.einjojo.jobs.player.progression.JobProgression;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JobPlayerImpl implements JobPlayer {
    private final UUID playerUuid;
    private @Nullable Job currentJob;

    private transient @Nullable Jobs jobs;
    private transient @Nullable JobChangeObserver currentJobObserver;


    public JobPlayerImpl(UUID playerUuid, @Nullable Job currentJob) {
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
    public @Nullable String currentJobName() {
        if (currentJob == null) {
            return null;
        }
        return currentJob.name();
    }

    @Override
    public void setCurrentJobName(@Nullable String currentJob) {
        this.currentJob = Job.valueOf(currentJob);
    }


    @Override
    public @Nullable Job currentJob() {
        return currentJob;
    }

    @Override
    public void setCurrentJob(@Nullable Job newJob) {
        if (Objects.equals(this.currentJob, newJob)) {
            return;
        }
        if (currentJobObserver() != null) {
            currentJobObserver().onJobChange(this, currentJob, newJob);
        }
        currentJob = newJob;
    }

    public void setJobs(@Nullable Jobs jobs) {
        this.jobs = jobs;
    }

    public Optional<JobProgression> progression() {
        if (jobs == null) {
            throw new IllegalStateException("Jobs instance not set");
        }
        return jobs.activeProgressions().synchronous().get(playerUuid());
    }

    public JobChangeObserver currentJobObserver() {
        return currentJobObserver;
    }

    public void setCurrentJobObserver(@Nullable JobChangeObserver currentJobObserver) {
        this.currentJobObserver = currentJobObserver;
    }

    public CompletableFuture<Optional<JobProgression>> progressionAsync() {
        if (jobs == null) {
            throw new IllegalStateException("Jobs instance not set");
        }
        return jobs.activeProgressions().get(playerUuid());
    }


    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof JobPlayerImpl playerJob)) return false;

        return playerUuid().equals(playerJob.playerUuid()) && Objects.equals(currentJobName(), playerJob.currentJobName());
    }

    @Override
    public int hashCode() {
        int result = playerUuid().hashCode();
        result = 31 * result + Objects.hashCode(currentJobName());
        return result;
    }

    @Override
    public String toString() {
        return "JobPlayer{" +
                "playerUUID=" + playerUuid() +
                ", currentJobName='" + currentJobName() + '\'' +
                '}';
    }

}
