package it.einjojo.jobs.reward;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.einjojo.jobs.Jobs;
import it.einjojo.jobs.db.JobStorage;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RewardManager {
    private final JobStorage jobStorage;
    private final AsyncLoadingCache<UUID, Set<String>> claimCache;
    private final Jobs jobs;

    public RewardManager(JobStorage jobStorage, Jobs jobs) {
        this.jobStorage = jobStorage;
        this.claimCache = Caffeine.newBuilder()
                .maximumSize(50)
                .expireAfterAccess(Duration.ofMinutes(5))
                .buildAsync(jobStorage::loadClaimedRewards);
        this.jobs = jobs;
    }

    public void deleteClaimedReward(UUID uuid, String rewardId) {
        jobStorage.deleteClaimedReward(uuid, rewardId);
        claimCache.synchronous().get(uuid).remove(rewardId);
    }

    public boolean hasClaimed(UUID uuid, String rewardId) {
        return claimCache.synchronous().get(uuid).contains(rewardId);
    }

    public CompletableFuture<Boolean> hasClaimedAsync(UUID uuid, String rewardId) {
        return claimCache.get(uuid).thenApply(claimedRewards -> claimedRewards.contains(rewardId));
    }

    public void claim(UUID uuid, AbstractJobReward reward) {
        jobStorage.saveClaimedReward(uuid, reward.rewardName());
        claimCache.synchronous().get(uuid).add(reward.rewardName());
        jobs.jobPlayers().get(uuid).thenAcceptAsync(jobPlayer -> {
            reward.onClaim(Bukkit.getPlayer(uuid), jobPlayer);
        });
    }


    public CompletableFuture<Void> claimAsync(UUID uuid, AbstractJobReward reward) {
        return CompletableFuture.supplyAsync(() -> {
            jobStorage.saveClaimedReward(uuid, reward.rewardName());
            claimCache.synchronous().get(uuid).add(reward.rewardName());
            return null;
        }).thenApplyAsync((ignore) -> jobs.jobPlayers().synchronous().get(uuid)).thenAcceptAsync((jobPlayer) -> {
            reward.onClaim(Bukkit.getPlayer(uuid), jobPlayer);
        });
    }


}
