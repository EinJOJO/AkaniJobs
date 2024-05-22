import it.einjojo.jobs.Job;
import it.einjojo.jobs.db.SQLJobStorage;
import it.einjojo.jobs.player.JobPlayerImpl;
import it.einjojo.jobs.player.progression.PlayerJobProgression;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StorageTest {
    private HikariCP hikariCP;
    private SQLJobStorage storage;

    @BeforeAll
    void setUp() {
        hikariCP = new HikariCP();
        storage = new SQLJobStorage(hikariCP.dataSource());
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
        loaded.setCurrentJobName("job2");
        storage.saveJobPlayer(loaded);
        var reloaded = storage.loadJobPlayer(loaded.playerUuid());
        assertNotNull(reloaded);
        assertEquals(loaded, reloaded);
    }

    @Test
    void writeReadJobProgression() {
        var progression = new PlayerJobProgression(UUID.randomUUID(), Job.MINER, 0, 0);
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


    @AfterAll
    void tearDown() {
        hikariCP.close();
    }

}
