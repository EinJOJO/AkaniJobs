package it.einjojo.jobs.handler;

import com.google.common.base.Preconditions;
import it.einjojo.jobs.Job;
import it.einjojo.jobs.Jobs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class JobHandlerRegistry {
    private final Map<Job, AbstractJobHandler> handlers = new HashMap<>();
    private final Jobs jobs;

    public JobHandlerRegistry(@NotNull Jobs jobs) {
        Preconditions.checkNotNull(jobs, "Jobs cannot be null");
        this.jobs = jobs;
    }

    @Deprecated
    public @Nullable AbstractJobHandler handler(@Nullable String jobName) {
        if (jobName == null) {
            return null;
        }
        return handlers.get(Job.valueOf(jobName));
    }

    public @Nullable AbstractJobHandler handler(@Nullable Job job) {
        if (job == null) {
            return null;
        }
        return handlers.get(job);
    }


    public void register(AbstractJobHandler handler) {
        handlers.put(handler.handlingJob(), handler);
        handler.setJobs(jobs);
    }
}