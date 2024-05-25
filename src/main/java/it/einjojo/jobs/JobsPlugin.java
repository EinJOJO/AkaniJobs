package it.einjojo.jobs;


import co.aikar.commands.PaperCommandManager;
import it.einjojo.jobs.command.JobsCommand;
import it.einjojo.jobs.db.JobStorageFactory;
import it.einjojo.jobs.db.SQLJobStorage;
import it.einjojo.jobs.handler.MinerJobHandler;
import it.einjojo.jobs.listener.ConnectionListener;
import it.einjojo.jobs.util.AkaniUtil;
import mc.obliviate.inventory.InventoryAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

public class JobsPlugin extends JavaPlugin {
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static final Component PREFIX = MINI_MESSAGE.deserialize("<dark_gray>[<gradient:#ff2000:#ff6600>Jobs</gradient>]");
    private Jobs jobs;
    private SQLJobStorage storage;
    private boolean usingAkaniDataSourceForStorage;


    @Override
    public void onEnable() {
        JobStorageFactory storageFactory = new JobStorageFactory();
        if (AkaniUtil.isAkaniCoreAvailable()) {
            storage = storageFactory.createAkaniJobStorage();
            usingAkaniDataSourceForStorage = true;
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
        new ConnectionListener(this, storage, saveTask, jobs);
        new InventoryAPI(this).init();
        registerCommands();
    }

    private void registerCommands() {
        PaperCommandManager cm = new PaperCommandManager(this);
        cm.enableUnstableAPI("brigadier");
        cm.registerCommand(new JobsCommand(cm, jobs, this));
    }


    @Override
    public void onDisable() {
        if (!usingAkaniDataSourceForStorage && storage != null) {
            storage.dataSource().close();
        }
    }

    public Jobs jobs() {
        return jobs;
    }
}
