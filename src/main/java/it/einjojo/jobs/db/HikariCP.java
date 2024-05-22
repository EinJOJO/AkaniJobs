package it.einjojo.jobs.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariCP {
    private final HikariDataSource dataSource;

    public HikariCP() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MariaDB driver not found", e);
        }
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mariadb://localhost:3306/test");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("8JKzDDUUzNMES4dNex3XXXe7FuDAroZ");
        this.dataSource = new HikariDataSource(hikariConfig);
        System.out.println("Created it.einjojo.jobs.db.HikariCP");
    }

    public HikariDataSource dataSource() {
        return dataSource;
    }

    public void close() {
        System.out.println("Closing it.einjojo.jobs.db.HikariCP");
        dataSource.close();
    }
}
