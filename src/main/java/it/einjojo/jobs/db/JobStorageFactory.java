package it.einjojo.jobs.db;

import it.einjojo.akani.core.api.AkaniCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

public class JobStorageFactory {

    public SQLJobStorage createConfigJobStorage(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("mariadb");
        if (section == null) throw new IllegalArgumentException("no mariadb section found in config.yml");
        MariaHikariCP hikariCP = new MariaHikariCP(
                section.getString("host"),
                section.getInt("port"),
                section.getString("database"),
                section.getString("username"),
                section.getString("password")
        );
        return new SQLJobStorage(hikariCP.dataSource());

    }



    public SQLJobStorage createAkaniJobStorage() {

        RegisteredServiceProvider<AkaniCore> akaniCoreProvider = Bukkit.getServer().getServicesManager().getRegistration(AkaniCore.class);
        if (akaniCoreProvider != null) {
            AkaniCore core = akaniCoreProvider.getProvider();
            return new SQLJobStorage(core.dataSource());
        } else {
            throw new IllegalStateException("Service provider not found for AkaniCore. Maybe AkaniCore is not loaded? ");
        }
    }

}
