package it.einjojo.jobs.db;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariDataSource;
import it.einjojo.jobs.Job;
import it.einjojo.jobs.player.JobPlayer;
import it.einjojo.jobs.player.JobPlayerImpl;
import it.einjojo.jobs.player.progression.PlayerJobProgression;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;

public class SQLJobStorage implements JobStorage {
    private final HikariDataSource dataSource;

    public SQLJobStorage(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean init() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS jobs_progressions " +
                    "(player_uuid VARCHAR(36) NOT NULL, " +
                    "job_name VARCHAR(16) NOT NULL, " +
                    "level INT NOT NULL, " +
                    "experience INT NOT NULL, " +
                    "PRIMARY KEY (player_uuid, job_name));");
            statement.execute("CREATE TABLE IF NOT EXISTS jobs_players " +
                    "(player_uuid VARCHAR(36) NOT NULL, " +
                    "job_name VARCHAR(16) NULL, " +
                    "PRIMARY KEY (player_uuid));");

            return true;
        } catch (Exception e) {
            throw new StorageException("init", e);
        }
    }

    @Override
    public void saveJobPlayer(@NotNull JobPlayer jobPlayer) {
        Preconditions.checkNotNull(jobPlayer, "playerJob cannot be null");
        String sql = "INSERT INTO jobs_players (player_uuid, job_name) " +
                "VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE job_name = ?;";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, jobPlayer.playerUuid().toString());
            ps.setString(2, jobPlayer.currentJobName());
            ps.setString(3, jobPlayer.currentJobName());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new StorageException("save %s".formatted(jobPlayer), e);
        }
    }

    @Override
    public void saveJobProgression(@NotNull PlayerJobProgression progression) {
        Preconditions.checkNotNull(progression, "progression cannot be null");
        String sql = "INSERT INTO jobs_progressions (player_uuid, job_name, level, experience) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE level = ?, experience = ?;";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
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
    public @NotNull PlayerJobProgression loadJobProgression(@NotNull UUID player, @NotNull Job job) {
        String sql = "SELECT level, experience FROM jobs_progressions WHERE player_uuid = ? AND job_name = ?;";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player.toString());
            ps.setString(2, job.name());

            var rs = ps.executeQuery();
            if (rs.next()) {
                return new PlayerJobProgression(player, job, rs.getInt("level"), rs.getInt("experience"));
            }
        } catch (Exception e) {
            throw new StorageException("load JobProgression (%s, %s)".formatted(player, job.name()), e);
        }
        return new PlayerJobProgression(player, job, 0, 0);
    }

    @Override
    public @NotNull JobPlayerImpl loadJobPlayer(@NotNull UUID player) {
        String sql = "SELECT job_name FROM jobs_players WHERE player_uuid = ?;";
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player.toString());
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new JobPlayerImpl(player, Job.valueOf(rs.getString("job_name")));
                }
            }
        } catch (Exception e) {
            throw new StorageException("load JobPlayer (%s)".formatted(player), e);
        }
        return new JobPlayerImpl(player, null);
    }
}
