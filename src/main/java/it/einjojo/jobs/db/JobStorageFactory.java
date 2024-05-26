package it.einjojo.jobs.db;

import com.zaxxer.hikari.HikariDataSource;
import it.einjojo.akani.core.api.AkaniCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobStorageFactory {

    private static final Logger log = LoggerFactory.getLogger(JobStorageFactory.class);

    public AbstractSQLJobStorage createConfigJobStorage(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("mariadb");
        if (section == null) throw new IllegalArgumentException("no mariadb section found in config.yml");
        MariaHikariCP hikariCP = new MariaHikariCP(
                section.getString("host"),
                section.getInt("port"),
                section.getString("database"),
                section.getString("username"),
                section.getString("password")
        );
        return new HikariSQLJobStorage(hikariCP.dataSource());

    }


    public AbstractSQLJobStorage createAkaniJobStorage() {
        log.info("Loading {}", HikariDataSource.class.getName());
        RegisteredServiceProvider<AkaniCore> provider = Bukkit.getServer().getServicesManager().getRegistration(AkaniCore.class);
        if (provider != null) {
            return new AkaniJobStorage(provider.getProvider());
        } else {
            throw new IllegalStateException("Service provider not found for AkaniCore. Maybe AkaniCore is not loaded? ");
        }
    }

}
