package it.einjojo.jobs.player.progression;

import it.einjojo.jobs.Job;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PlayerJobProgression {
    private final UUID playerUuid;
    private final Job job;
    private int level;
    private int xp;

    private transient @Nullable JobProgressionObserver observer;

    public PlayerJobProgression(UUID playerUuid, Job job, int level, int xp) {
        this.playerUuid = playerUuid;
        this.job = job;
        this.level = level;
        this.xp = xp;
    }


    public Optional<Player> player() {
        return Optional.ofNullable(Bukkit.getPlayer(playerUuid));
    }

    public UUID playerUuid() {
        return playerUuid;
    }


    public void setObserver(@Nullable JobProgressionObserver observer) {
        this.observer = observer;
    }

    public int level() {
        return level;
    }

    public void setLevel(int level) {
        if (level < 0) {
            throw new IllegalArgumentException("Level cannot be negative");
        }
        if (this.level == level) {
            return;
        }
        if (observer != null) {
            observer.onLevelChange(this, this.level, level);
        }
        this.level = level;

    }

    public String jobName() {
        return job.name();
    }

    public Job job() {
        return job;
    }

    public int xp() {
        return xp;
    }

    public void addXp(int xp) {
        setXp(this.xp + xp);
    }

    public void setXp(int xp) {
        if (xp < 0) {
            throw new IllegalArgumentException("XP cannot be negative");
        }
        if (observer != null) {
            observer.onXPChange(this, this.xp, xp);
        }
        this.xp = xp;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PlayerJobProgression that)) return false;

        return level == that.level && xp == that.xp && playerUuid.equals(that.playerUuid) && job == that.job;
    }

    @Override
    public int hashCode() {
        int result = playerUuid.hashCode();
        result = 31 * result + Objects.hashCode(job);
        result = 31 * result + level;
        result = 31 * result + xp;
        return result;
    }

    @Override
    public String toString() {
        return "PlayerJobProgression{" + "xp=" + xp +
                ", level=" + level +
                ", job=" + job +
                ", playerUuid=" + playerUuid +
                '}';
    }
}
