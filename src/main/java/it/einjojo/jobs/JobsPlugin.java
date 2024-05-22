package it.einjojo.jobs;


import it.einjojo.jobs.db.JobStorageFactory;
import it.einjojo.jobs.db.SQLJobStorage;
import it.einjojo.jobs.handler.MinerJobHandler;
import it.einjojo.jobs.listener.ConnectionListener;
import it.einjojo.jobs.util.AkaniUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class JobsPlugin extends JavaPlugin {

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
            getConfig().options().copyDefaults(true);
            saveConfig();
            storage = storageFactory.createConfigJobStorage(getConfig());
        }
        storage.init();
        DataSaveTask saveTask = new DataSaveTask(this, storage);
        jobs = new Jobs(storage, saveTask);
        jobs.handlerRegistry().register(new MinerJobHandler(this));
        new ConnectionListener(this, storage, saveTask, jobs);
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
