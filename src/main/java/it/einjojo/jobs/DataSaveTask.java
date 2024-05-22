package it.einjojo.jobs;

import it.einjojo.jobs.db.JobStorage;
import it.einjojo.jobs.player.JobPlayer;
import it.einjojo.jobs.player.progression.JobProgression;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataSaveTask implements Runnable {
    private static final int SAVE_INTERVAL = 20 * 60;
    private final JobStorage storage;
    private final Queue<JobPlayer> jobPlayers = new ConcurrentLinkedQueue<>();
    private final Queue<JobProgression> progressions = new ConcurrentLinkedQueue<>();


    public DataSaveTask(JavaPlugin plugin, JobStorage storage) {
        this.storage = storage;
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, SAVE_INTERVAL, SAVE_INTERVAL);
    }

    public void add(JobPlayer jobPlayer) {
        if (jobPlayers.contains(jobPlayer)) return;
        jobPlayers.add(jobPlayer);
    }

    public void remove(JobPlayer jobPlayer) {
        jobPlayers.remove(jobPlayer);
    }

    public void add(JobProgression progression) {
        if (progressions.contains(progression)) return;
        progressions.add(progression);
    }

    public void remove(JobProgression progression) {
        progressions.remove(progression);
    }

    public void save() {
        while (!jobPlayers.isEmpty()) {
            JobPlayer jobPlayer = jobPlayers.poll();
            storage.saveJobPlayer(jobPlayer);
        }
        while (!progressions.isEmpty()) {
            JobProgression progression = progressions.poll();
            storage.saveJobProgression(progression);
        }
    }

    @Override
    public void run() {
        save();
    }
}
