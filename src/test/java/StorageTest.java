import it.einjojo.jobs.db.SQLJobStorage;
import it.einjojo.jobs.player.PlayerJobImpl;
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
        var jobPlayer = new PlayerJobImpl(UUID.randomUUID(), "job1");
        storage.saveJobPlayer(jobPlayer);
        var loaded = storage.loadJobPlayer(jobPlayer.playerUUID());
        assertNotNull(loaded);
        assertEquals(jobPlayer, loaded);
        loaded.setCurrentJobName("job2");
        storage.saveJobPlayer(loaded);
        var reloaded = storage.loadJobPlayer(loaded.playerUUID());
        assertNotNull(reloaded);
        assertEquals(loaded, reloaded);
    }

    @Test
    void writeReadJobProgression() {
        var progression = new PlayerJobProgression(UUID.randomUUID(), "job1", 0, 0);
        storage.saveJobProgression(progression);
        var loaded = storage.loadJobProgression(progression.player(), progression.jobName());
        assertNotNull(loaded);
        assertEquals(progression, loaded);
        progression.setLevel(1);
        progression.setXp(100);
        storage.saveJobProgression(progression);
        var reloaded = storage.loadJobProgression(progression.player(), progression.jobName());
        assertNotNull(reloaded);
        assertEquals(progression, reloaded);

    }


    @AfterAll
    void tearDown() {
        hikariCP.close();
    }

}
