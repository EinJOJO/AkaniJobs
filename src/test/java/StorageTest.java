import it.einjojo.jobs.Job;
import it.einjojo.jobs.db.AbstractSQLJobStorage;
import it.einjojo.jobs.db.HikariSQLJobStorage;
import it.einjojo.jobs.db.MariaHikariCP;
import it.einjojo.jobs.player.JobPlayerImpl;
import it.einjojo.jobs.player.progression.JobProgression;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StorageTest {
    private MariaHikariCP hikariCP;
    private AbstractSQLJobStorage storage;

    @BeforeAll
    void setUp() {
        hikariCP = new MariaHikariCP("localhost", 3306, "test", "root", "0");
        storage = new HikariSQLJobStorage(hikariCP.dataSource());
        storage.init();
    }

    @Test
    void initDatabase() {
        assertTrue(storage.init());
    }

    @Test
    void writeReadJobPlayer() {
        var jobPlayer = new JobPlayerImpl(UUID.randomUUID(), Job.MINER);
        storage.saveJobPlayer(jobPlayer);
        var loaded = storage.loadJobPlayer(jobPlayer.playerUuid());
        assertNotNull(loaded);
        assertEquals(jobPlayer, loaded);
        loaded.setCurrentJob(Job.FARMER);
        storage.saveJobPlayer(loaded);
        var reloaded = storage.loadJobPlayer(loaded.playerUuid());
        assertNotNull(reloaded);
        assertEquals(loaded, reloaded);
    }

    @Test
    void writeReadJobProgression() {
        var progression = new JobProgression(UUID.randomUUID(), Job.MINER, 0, 0);
        storage.saveJobProgression(progression);
        var loaded = storage.loadJobProgression(progression.playerUuid(), progression.job());
        assertNotNull(loaded);
        assertEquals(progression, loaded);
        progression.setLevel(1);
        progression.setXp(100);
        storage.saveJobProgression(progression);
        var reloaded = storage.loadJobProgression(progression.playerUuid(), progression.job());
        assertNotNull(reloaded);
        assertEquals(progression, reloaded);
    }

    @Test
    void testLocks() {
        var player = UUID.randomUUID();
        assertFalse(storage.isPlayerLocked(player));
        storage.lockPlayer(player);
        assertTrue(storage.isPlayerLocked(player));
        storage.unlockPlayer(player);
        assertFalse(storage.isPlayerLocked(player));
    }


    @AfterAll
    void tearDown() {
        hikariCP.close();
    }

}
