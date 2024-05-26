package it.einjojo.jobs.gui;

import it.einjojo.jobs.Jobs;
import it.einjojo.jobs.player.JobPlayer;
import it.einjojo.jobs.player.progression.JobProgression;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class GuiFactory {
    private final Jobs jobs;

    public GuiFactory(Jobs jobs) {
        this.jobs = jobs;
    }

    public JobInfoGui createJobInfoGui(Player player) {
        return createJobInfoGui(player, jobs.jobPlayers().synchronous().get(player.getUniqueId()));
    }

    public JobInfoGui createJobInfoGui(Player player, JobPlayer jobPlayer) {
        return new JobInfoGui(player, jobPlayer, this);
    }

    public ConfirmGui createConfirmGui(Player player, String confirm, Consumer<Boolean> onConfirm) {
        return new ConfirmGui(player, confirm, onConfirm);
    }

    public JobOverviewGui createJobOverviewGui(Player player, JobPlayer jobPlayer) {
        return new JobOverviewGui(player, jobPlayer, this);
    }

    public LevelRewardsGui createLevelRewardsGui(Player player, JobProgression jobProgression) {
        return new LevelRewardsGui(player, jobProgression, jobs.rewardRegistry(), this);
    }

}
