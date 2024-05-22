package it.einjojo.jobs.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MariaHikariCP {
    private final HikariDataSource dataSource;

    public MariaHikariCP(String host, int port, String database, String username, String password) {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MariaDB driver not found", e);
        }
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mariadb://%s:%d/%s".formatted(host, port, database));
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        this.dataSource = new HikariDataSource(hikariConfig);
    }

    public HikariDataSource dataSource() {
        return dataSource;
    }

    public void close() {
        dataSource.close();
    }
}
