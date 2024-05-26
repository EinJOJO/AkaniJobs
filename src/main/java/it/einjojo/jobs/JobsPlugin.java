package it.einjojo.jobs;


import co.aikar.commands.PaperCommandManager;
import it.einjojo.akani.core.api.AkaniCoreProvider;
import it.einjojo.jobs.command.JobsCommand;
import it.einjojo.jobs.db.AbstractSQLJobStorage;
import it.einjojo.jobs.db.HikariSQLJobStorage;
import it.einjojo.jobs.db.JobStorageFactory;
import it.einjojo.jobs.handler.MinerJobHandler;
import it.einjojo.jobs.listener.ConnectionListener;
import it.einjojo.jobs.reward.AbstractJobReward;
import it.einjojo.jobs.reward.EconomyReward;
import it.einjojo.jobs.reward.ItemReward;
import it.einjojo.jobs.util.AkaniUtil;
import mc.obliviate.inventory.InventoryAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class JobsPlugin extends JavaPlugin {
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static final Component PREFIX = MINI_MESSAGE.deserialize("<dark_gray>[<gradient:#ff2000:#ff6600>Jobs</gradient>]");
    private Jobs jobs;
    private AbstractSQLJobStorage storage;


    @Override
    public void onEnable() {
        JobStorageFactory storageFactory = new JobStorageFactory();
        if (AkaniUtil.isAkaniCoreAvailable()) {
            storage = storageFactory.createAkaniJobStorage();
        } else {
            saveDefaultConfig();
            getConfig().options().copyDefaults(true);
            saveConfig();
            storage = storageFactory.createConfigJobStorage(getConfig());
        }
        storage.init();
        DataSaveTask saveTask = new DataSaveTask(this, storage);
        jobs = new Jobs(storage, saveTask);

        jobs.handlerRegistry().register(new MinerJobHandler(this));

        jobs.rewardRegistry().setRewards(Job.MINER, rewards(Job.MINER));
        jobs.rewardRegistry().setRewards(Job.FARMER, rewards(Job.FARMER));
        jobs.rewardRegistry().setRewards(Job.WOODCUTTER, rewards(Job.WOODCUTTER));
        jobs.rewardRegistry().setRewards(Job.HUNTER, rewards(Job.HUNTER));


        new ConnectionListener(this, storage, saveTask, jobs);
        new InventoryAPI(this).init();
        registerCommands();
    }

    private AbstractJobReward[] rewards(Job job) {
        AbstractJobReward[] rewards = new AbstractJobReward[200];
        if (AkaniUtil.isAkaniCoreAvailable()) { // Supports Economy Rewards
            for (int i = 0; i < rewards.length; i++) {
                rewards[i] = new EconomyReward(job.name().toLowerCase() + "_economy_" + i, i, jobs.rewardManager(), AkaniCoreProvider.get().coinsManager());
            }
        } else {
            for (int i = 0; i < rewards.length; i++) {
                ItemStack is = new ItemStack(Material.DIAMOND);
                is.setAmount(i);
                rewards[i] = new ItemReward(job.name().toLowerCase() + "_item_" + i, is, jobs.rewardManager());
            }
        }
        return rewards;
    }


    private void registerCommands() {
        PaperCommandManager cm = new PaperCommandManager(this);
        cm.enableUnstableAPI("brigadier");
        cm.registerCommand(new JobsCommand(cm, jobs, this));
    }


    @Override
    public void onDisable() {
        if (storage instanceof HikariSQLJobStorage hikariStorage) {
            hikariStorage.dataSource().close();
        }
    }

    public Jobs jobs() {
        return jobs;
    }
}
