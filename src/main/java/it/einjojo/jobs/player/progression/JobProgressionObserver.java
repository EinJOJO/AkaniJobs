package it.einjojo.jobs.player.progression;

public interface JobProgressionObserver {

    void onXPChange(JobProgression progression, int oldXP, int newXP);
    void onLevelChange(JobProgression progression, int oldLevel, int newLevel);

}
