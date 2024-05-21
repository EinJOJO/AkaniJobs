package it.einjojo.jobs.player;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class JobPlayer {
    private final UUID playerUUID;
    private @Nullable String currentJobName;

    public JobPlayer(UUID playerUUID, @Nullable String currentJobName) {
        this.playerUUID = playerUUID;
        this.currentJobName = currentJobName;
    }

    public UUID playerUUID() {
        return playerUUID;
    }

    public @Nullable String currentJobName() {
        return currentJobName;
    }


    public void setCurrentJobName(@Nullable String currentJobName) {
        this.currentJobName = currentJobName;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof JobPlayer jobPlayer)) return false;

        return playerUUID.equals(jobPlayer.playerUUID) && Objects.equals(currentJobName, jobPlayer.currentJobName);
    }

    @Override
    public int hashCode() {
        int result = playerUUID.hashCode();
        result = 31 * result + Objects.hashCode(currentJobName);
        return result;
    }

    @Override
    public String toString() {
        return "JobPlayer{" +
                "playerUUID=" + playerUUID +
                ", currentJobName='" + currentJobName + '\'' +
                '}';
    }

}
