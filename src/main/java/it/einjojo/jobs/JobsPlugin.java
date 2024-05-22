package it.einjojo.jobs;


import it.einjojo.akani.core.api.AkaniCore;
import it.einjojo.jobs.db.HikariCP;
import it.einjojo.jobs.db.JobStorage;
import it.einjojo.jobs.db.SQLJobStorage;
import it.einjojo.jobs.handler.MinerJobHandler;
import it.einjojo.jobs.listener.ConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class JobsPlugin extends JavaPlugin {

    private Jobs jobs;

    @Override
    public void onEnable() {
        JobStorage storage = database();

        if (storage == null) {
            getSLF4JLogger().error("No database found, disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        storage.init();
        DataSaveTask saveTask = new DataSaveTask(this, storage);
        jobs = new Jobs(storage, saveTask);
        jobs.handlerRegistry().register(new MinerJobHandler(this));
        new ConnectionListener(this, storage, saveTask, jobs);
    }


    private JobStorage database() {
        // Akani
        try {
            RegisteredServiceProvider<AkaniCore> akaniCoreProvider = Bukkit.getServer().getServicesManager().getRegistration(AkaniCore.class);
            if (akaniCoreProvider != null) {
                AkaniCore core = akaniCoreProvider.getProvider();
                return new SQLJobStorage(core.dataSource());
            }
        } catch (Exception ex) {
            ex.fillInStackTrace();
        } catch (NoClassDefFoundError ignore) {
        }
        // database config
        try {
            return new SQLJobStorage(new HikariCP().dataSource());
        } catch (Exception e) {
            getSLF4JLogger().error("Failed to connect to localhost database: {}", e.getMessage());
            return null;
        }

    }


    public Jobs jobs() {
        return jobs;
    }
}
