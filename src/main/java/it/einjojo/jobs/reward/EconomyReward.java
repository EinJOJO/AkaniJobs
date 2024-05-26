package it.einjojo.jobs.reward;

import it.einjojo.akani.core.api.economy.EconomyManager;
import it.einjojo.jobs.player.JobPlayer;
import org.bukkit.entity.Player;

public class EconomyReward extends AbstractJobReward {
    private final int amount;
    private final transient RewardManager rewardManager;
    private final transient EconomyManager economyManager;

    public EconomyReward(String rewardName, int amount, RewardManager rewardManager, EconomyManager economyManager) {
        super(rewardName);
        this.amount = amount;
        this.rewardManager = rewardManager;
        this.economyManager = economyManager;
    }


    @Override
    public void onClaim(Player bukkitPlayer, JobPlayer jobPlayer) {
        economyManager.playerEconomyAsync(bukkitPlayer.getUniqueId()).thenAccept((economyHolder) -> {
            economyHolder.orElseThrow().addBalance(amount);
            bukkitPlayer.sendMessage("You have claimed %d money".formatted(amount));
        }).exceptionally(throwable -> {
            rewardManager().deleteClaimedReward(bukkitPlayer.getUniqueId(), rewardName());
            bukkitPlayer.sendMessage("An error occurred while claiming the reward");
            return null;
        });
    }

    @Override
    public String[] rewardLore() {
        return new String[]{"§e%d §7Coins".formatted(amount)};
    }

    @Override
    public RewardManager rewardManager() {
        return rewardManager;
    }
}
