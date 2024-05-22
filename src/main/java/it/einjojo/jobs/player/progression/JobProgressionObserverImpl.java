package it.einjojo.jobs.player.progression;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class JobProgressionObserverImpl implements JobProgressionObserver {
    @Override
    public void onXPChange(PlayerJobProgression progression, int oldXP, int newXP) {
        progression.player().ifPresent((bukkitPlayer) -> {
            bukkitPlayer.sendActionBar(createActionBarMessage(newXP - oldXP));
        });
    }

    private Component createActionBarMessage(int deltaXp) {
        return Component.text("§a+" + deltaXp + "§7 XP");
    }

    @Override
    public void onLevelChange(PlayerJobProgression progression, int oldLevel, int newLevel) {

    }

    private void levelupAnimation(Player player) {


    }
}
