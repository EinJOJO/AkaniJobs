package it.einjojo.jobs;


import it.einjojo.akani.core.api.AkaniCore;
import it.einjojo.jobs.db.JobStorage;
import it.einjojo.jobs.db.SQLJobStorage;
import it.einjojo.jobs.handler.MinerJobHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class JobsPlugin extends JavaPlugin {

    private Jobs jobs;

    @Override
    public void onEnable() {
        RegisteredServiceProvider<AkaniCore> akaniCoreProvider = Bukkit.getServer().getServicesManager().getRegistration(AkaniCore.class);
        JobStorage storage;
        if (akaniCoreProvider != null) {
            AkaniCore core = akaniCoreProvider.getProvider();
            storage = new SQLJobStorage(core.dataSource());
        } else {
            getSLF4JLogger().error("AkaniCore not found, disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        jobs = new Jobs(storage);
        jobs.handlerRegistry().register(new MinerJobHandler(this));
    }

    public Jobs jobs() {
        return jobs;
    }
}
