package it.einjojo.jobs.player.progression;

public interface JobProgressionObserver {

    void onXPChange(PlayerJobProgression progression, int oldXP, int newXP);
    void onLevelChange(PlayerJobProgression progression, int oldLevel, int newLevel);

}
