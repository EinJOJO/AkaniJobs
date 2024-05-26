package it.einjojo.jobs.reward;

import it.einjojo.jobs.Job;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the rewards for each job
 */
public class JobRewardRegistry {
    private final Map<Job, AbstractJobReward[]> rewards = new HashMap<>();

    public JobRewardRegistry() {
    }

    public void setRewards(Job job, AbstractJobReward... rewards) {
        this.rewards.put(job, rewards);
    }

    /**
     * Get the rewards for a job
     */
    public AbstractJobReward[] getRewards(Job job) {
        return rewards.get(job);
    }


}
