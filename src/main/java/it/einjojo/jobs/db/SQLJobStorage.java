package it.einjojo.jobs.db;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariDataSource;
import it.einjojo.jobs.Job;
import it.einjojo.jobs.player.JobPlayer;
import it.einjojo.jobs.player.JobPlayerImpl;
import it.einjojo.jobs.player.progression.JobProgression;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;

public record SQLJobStorage(HikariDataSource dataSource) implements JobStorage {
    private static final Logger logger = LoggerFactory.getLogger(SQLJobStorage.class);


    @Override
    public boolean init() {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS jobs_progressions " + "(player_uuid VARCHAR(36) NOT NULL, " + "job_name VARCHAR(16) NOT NULL, " + "level INT NOT NULL, " + "experience INT NOT NULL, " + "PRIMARY KEY (player_uuid, job_name));");
            statement.execute("CREATE TABLE IF NOT EXISTS jobs_players " + "(player_uuid VARCHAR(36) NOT NULL, " + "job_name VARCHAR(16) NULL, " + "PRIMARY KEY (player_uuid));");
            statement.execute("CREATE TABLE IF NOT EXISTS jobs_locks " + "(player_uuid VARCHAR(36) NOT NULL, " + "PRIMARY KEY (player_uuid));");
            logger.info("SQLJobStorage initialized");
            return true;
        } catch (Exception e) {
            throw new StorageException("init", e);
        }
    }

    @Override
    public void saveJobPlayer(@NotNull JobPlayer jobPlayer) {
        Preconditions.checkNotNull(jobPlayer, "playerJob cannot be null");
        String sql = "INSERT INTO jobs_players (player_uuid, job_name) " + "VALUES (?, ?) " + "ON DUPLICATE KEY UPDATE job_name = ?;";

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, jobPlayer.playerUuid().toString());
            ps.setString(2, jobPlayer.currentJobName());
            ps.setString(3, jobPlayer.currentJobName());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new StorageException("save %s".formatted(jobPlayer), e);
        }
    }

    @Override
    public void saveJobProgression(@NotNull JobProgression progression) {
        Preconditions.checkNotNull(progression, "progression cannot be null");
        String sql = "INSERT INTO jobs_progressions (player_uuid, job_name, level, experience) " + "VALUES (?, ?, ?, ?) " + "ON DUPLICATE KEY UPDATE level = ?, experience = ?;";

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, progression.playerUuid().toString());
            ps.setString(2, progression.jobName());
            ps.setInt(3, progression.level());
            ps.setInt(4, progression.xp());
            ps.setInt(5, progression.level());
            ps.setInt(6, progression.xp());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new StorageException("save %s".formatted(progression), e);
        }
    }


    @Override
    public @NotNull JobProgression loadJobProgression(@NotNull UUID player, @NotNull Job job) {
        String sql = "SELECT level, experience FROM jobs_progressions WHERE player_uuid = ? AND job_name = ?;";

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player.toString());
            ps.setString(2, job.name());

            var rs = ps.executeQuery();
            if (rs.next()) {
                return new JobProgression(player, job, rs.getInt("level"), rs.getInt("experience"));
            }
        } catch (Exception e) {
            throw new StorageException("load JobProgression (%s, %s)".formatted(player, job.name()), e);
        }
        return new JobProgression(player, job, 0, 0);
    }

    @Override
    public @NotNull JobPlayerImpl loadJobPlayer(@NotNull UUID player) {
        String sql = "SELECT job_name FROM jobs_players WHERE player_uuid = ?;";
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player.toString());
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String jobName = rs.getString("job_name");
                    Job optionalJob = jobName != null ? Job.valueOf(jobName) : null;
                    return new JobPlayerImpl(player, optionalJob);
                }
            }
        } catch (Exception e) {
            throw new StorageException("load JobPlayer (%s)".formatted(player), e);
        }
        return new JobPlayerImpl(player, null);
    }

    @Override
    public void lockPlayer(@NotNull UUID player) {
        String sql = "INSERT INTO jobs_locks (player_uuid) VALUES (?) ON DUPLICATE KEY UPDATE player_uuid = player_uuid;";
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player.toString());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new StorageException("lock player %s".formatted(player), e);
        }
    }

    @Override
    public void unlockPlayer(@NotNull UUID player) {
        String sql = "DELETE FROM jobs_locks WHERE player_uuid = ?";
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player.toString());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new StorageException("unlock player %s".formatted(player), e);
        }
    }

    @Override
    public boolean isPlayerLocked(@NotNull UUID player) {
        String sql = "SELECT player_uuid FROM jobs_locks WHERE player_uuid = ?";
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player.toString());
            try (var rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new StorageException("is player locked %s".formatted(player), e);
        }
    }
}
