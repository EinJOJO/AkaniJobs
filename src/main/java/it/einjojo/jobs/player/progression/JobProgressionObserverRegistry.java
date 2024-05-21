package it.einjojo.jobs.player.progression;

import java.util.LinkedList;
import java.util.List;

public class JobProgressionObserverRegistry implements JobProgressionObserver {

    private List<JobProgressionObserver> observers = new LinkedList<>();


    public void subscribe(JobProgressionObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(JobProgressionObserver observer) {
        observers.remove(observer);
    }


    @Override
    public void onXPChange(PlayerJobProgression progression, int oldXP, int newXP) {
        for (JobProgressionObserver observer : observers) {
            observer.onXPChange(progression, oldXP, newXP);
        }
    }

    @Override
    public void onLevelChange(PlayerJobProgression progression, int oldLevel, int newLevel) {
        for (JobProgressionObserver observer : observers) {
            observer.onLevelChange(progression, oldLevel, newLevel);
        }
    }
}
