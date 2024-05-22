package it.einjojo.jobs.player.progression;

import it.einjojo.jobs.DataSaveTask;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class JobProgressionObserverImpl implements JobProgressionObserver {

    private final DataSaveTask dataSaveTask;

    public JobProgressionObserverImpl(DataSaveTask dataSaveTask) {
        this.dataSaveTask = dataSaveTask;
    }

    @Override
    public void onXPChange(JobProgression progression, int oldXP, int newXP) {
        progression.player().ifPresent((bukkitPlayer) -> {
            bukkitPlayer.sendActionBar(createActionBarMessage(newXP - oldXP));
        });
        dataSaveTask.add(progression);
    }

    private Component createActionBarMessage(int deltaXp) {
        return Component.text("§a+" + deltaXp + "§7 XP");
    }

    @Override
    public void onLevelChange(JobProgression progression, int oldLevel, int newLevel) {
        dataSaveTask.add(progression);
    }

    private void levelupAnimation(Player player) {


    }
}
