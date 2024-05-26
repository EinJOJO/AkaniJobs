package it.einjojo.jobs.db;

import it.einjojo.akani.core.api.AkaniCore;

import java.sql.Connection;
import java.sql.SQLException;

public class AkaniJobStorage extends AbstractSQLJobStorage {
    private final AkaniCore akaniCore;

    public AkaniJobStorage(AkaniCore akaniCore) {
        this.akaniCore = akaniCore;
    }

    @Override
    protected Connection getConnection() throws SQLException {
        return akaniCore.dataSourceProxy().getConnection();
    }
}
