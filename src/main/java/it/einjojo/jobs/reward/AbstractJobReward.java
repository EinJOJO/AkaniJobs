package it.einjojo.jobs.reward;

import it.einjojo.jobs.player.JobPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractJobReward {
    protected final String rewardName;

    protected AbstractJobReward(String rewardName) {
        this.rewardName = rewardName;
    }

    public boolean hasClaimed(UUID uuid) {
        return rewardManager().hasClaimed(uuid, rewardName);
    }

    public CompletableFuture<Boolean> hasClaimedAsync(UUID uuid) {
        return rewardManager().hasClaimedAsync(uuid, rewardName).exceptionally(throwable -> {
            throw new RuntimeException("Error checking if reward is claimed", throwable);
        });
    }

    public String rewardName() {
        return rewardName;
    }

    public void claim(UUID uuid) {
        rewardManager().claim(uuid, this);
    }

    public void claimAsync(UUID uuid) {
        rewardManager().claimAsync(uuid, this).exceptionally(throwable -> {
            throw new RuntimeException("Error claiming reward", throwable);
        });
    }

    /**
     * Not thread safe method
     *
     * @param bukkitPlayer the player that claimed the reward
     * @param jobPlayer    the job player that claimed the reward
     */
    public abstract void onClaim(Player bukkitPlayer, JobPlayer jobPlayer);

    public abstract RewardManager rewardManager();

    public abstract String[] rewardLore();

}
