package it.einjojo.jobs.db;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariSQLJobStorage extends AbstractSQLJobStorage {

    private final HikariDataSource dataSource;

    public HikariSQLJobStorage(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public HikariDataSource dataSource() {
        return dataSource;
    }
}
