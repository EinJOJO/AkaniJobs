package it.einjojo.jobs.player.progression;

import java.util.Objects;
import java.util.UUID;

public class PlayerJobProgression {
    private final UUID player;
    private final String jobName;
    private int level;
    private int xp;

    private transient JobProgressionObserver observer;

    public PlayerJobProgression(UUID player, String jobName, int level, int xp) {
        this.player = player;
        this.jobName = jobName;
        this.level = level;
        this.xp = xp;
    }

    public UUID player() {
        return player;
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
        return jobName;
    }

    public int xp() {
        return xp;
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

        return level == that.level && xp == that.xp && player.equals(that.player) && jobName.equals(that.jobName) && Objects.equals(observer, that.observer);
    }

    @Override
    public int hashCode() {
        int result = player.hashCode();
        result = 31 * result + jobName.hashCode();
        result = 31 * result + level;
        result = 31 * result + xp;
        result = 31 * result + Objects.hashCode(observer);
        return result;
    }

    @Override
    public String toString() {
        return "PlayerJobProgression{" +
                "player=" + player +
                ", jobName='" + jobName + '\'' +
                ", xp=" + xp +
                ", level=" + level +
                '}';
    }
}
