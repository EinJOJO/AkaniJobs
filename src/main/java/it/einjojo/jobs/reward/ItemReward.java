package it.einjojo.jobs.reward;

import it.einjojo.jobs.player.JobPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemReward extends AbstractJobReward {
    private final ItemStack itemStack;
    private final transient RewardManager rewardManager;

    public ItemReward(String rewardName, ItemStack itemStack, RewardManager rewardManager) {
        super(rewardName);
        this.itemStack = itemStack;
        this.rewardManager = rewardManager;
    }


    @Override
    public void onClaim(Player bukkitPlayer, JobPlayer jobPlayer) {
        bukkitPlayer.getInventory().addItem(itemStack);
        bukkitPlayer.sendMessage("You have claimed %s".formatted(itemStack.getItemMeta().getDisplayName()));
    }

    @Override
    public RewardManager rewardManager() {
        return rewardManager;
    }

    @Override
    public String[] rewardLore() {
        ItemMeta itemMeta = itemStack.getItemMeta();

        return new String[]{
                "%dx %s".formatted(itemStack.getAmount(), itemMeta.getDisplayName()),
        };
    }
}
