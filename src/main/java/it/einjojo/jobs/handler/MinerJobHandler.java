package it.einjojo.jobs.handler;

import it.einjojo.jobs.Job;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class MinerJobHandler extends AbstractJobHandler implements Listener {
    private Map<Material, ORE_QUALITY> oreQualityMap;

    public MinerJobHandler(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        if (!isTracked(playerUuid)) return;
        playerJobProgression(playerUuid).thenAccept((progression) -> {
            progression.addXp(1);

        });
    }

    ORE_QUALITY oreQuality(Material material) {
        return oreQualityMap.getOrDefault(material, ORE_QUALITY.NOT_A_ORE);
    }

    @Override
    Job handlingJob() {
        return Job.MINER;
    }

    private enum ORE_QUALITY {
        NOT_A_ORE,
        LOW,
        MID,
        HIGH,
    }
}
