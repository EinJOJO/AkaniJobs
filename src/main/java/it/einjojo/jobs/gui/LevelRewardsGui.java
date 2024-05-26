package it.einjojo.jobs.gui;

import it.einjojo.jobs.player.progression.JobProgression;
import it.einjojo.jobs.reward.AbstractJobReward;
import it.einjojo.jobs.reward.JobRewardRegistry;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

public class LevelRewardsGui extends ExtendedGui {
    private final PaginationManager paginationManager = new PaginationManager(this);
    private final @NotNull JobProgression jobProgression;
    private final GuiFactory guiFactory;
    private final JobRewardRegistry rewardRegistry;

    public LevelRewardsGui(@NotNull Player player, @NotNull JobProgression jobProgression, @NotNull JobRewardRegistry rewardRegistry, GuiFactory guiFactory) {
        super(player, "level_rewards", "§6Level Belohnungen", 6);
        this.jobProgression = jobProgression;
        this.guiFactory = guiFactory;
        this.rewardRegistry = rewardRegistry;
    }


    private Icon createRewardIcon(AbstractJobReward reward, int index) {
        Material material = index < jobProgression.xp() ? ((index + 1) % 5 == 0) ? Material.GOLD_INGOT : Material.GOLD_NUGGET : ((index + 1) % 5 == 0) ? Material.IRON_INGOT : Material.IRON_NUGGET; //TODO
        Icon icon = new Icon(material)
                .setLore(reward.rewardLore())
                .setAmount(index + 1);

        if (!reward.hasClaimed(player.getUniqueId()) && index < jobProgression.xp()) { //TODO
            icon.appendLore("",
                    "§aKlicke, um die Belohnung zu erhalten.",
                    "");
            icon.enchant(Enchantment.CHANNELING);
            icon.hideFlags(ItemFlag.HIDE_ENCHANTS);
            icon.onClick((click) -> {
                playClickSound();
                reward.claimAsync(player.getUniqueId());
                runTaskLater(2, (s) -> {
                    paginationManager.getItems().set(index, createRewardIcon(reward, index));
                    paginationManager.update();
                });
            });
        }

        return icon;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        paginationManager.registerPageSlots(generateSnakePattern());
        AbstractJobReward[] rewards = rewardRegistry.getRewards(jobProgression.job());
        Icon[] icons = new Icon[rewards.length];
        for (int i = 0; i < icons.length; i++) {
            AbstractJobReward reward = rewards[i];
            Icon icon = createRewardIcon(reward, i);
            icons[i] = icon;
        }
        paginationManager.addItem(icons);
        addItem(45, new Icon(Material.BARRIER).setName("§7Zurück zur Übersicht").onClick((click) -> {
            guiFactory.createJobInfoGui(player).open();
        }));
        addItem(48, new Icon(Material.ARROW).setName("§7Zurück").onClick((click) -> {
            if (!paginationManager.isFirstPage()) {
                paginationManager.goPreviousPage();
                paginationManager.update();
            }
        }));
        addItem(50, new Icon(Material.ARROW).setName("§7Weiter").onClick((click) -> {
            if (!paginationManager.isLastPage()) {
                paginationManager.goNextPage();
                paginationManager.update();
            }
        }));

        paginationManager.update();

    }


    private Integer[] generateSnakePattern() {
        Integer[] result = new Integer[3 * 9 + 2];
        int index = 0;

        // Add 0-8 in ascending order
        for (int i = 0; i <= 8; i++) {
            result[index++] = i;
        }

        // Add 17
        result[index++] = 17;

        // Add 18-26 in descending order
        for (int i = 26; i >= 18; i--) {
            result[index++] = i;
        }

        // Add 27
        result[index++] = 27;

        // Add 36-44 in ascending order
        for (int i = 36; i <= 44; i++) {
            result[index++] = i;
        }

        return result;
    }
}
