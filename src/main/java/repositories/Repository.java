package repositories;

import utils.DatabaseSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Repository {

    @SuppressWarnings("java:S112")
    protected Connection openConnection() throws SQLException {
        Connection connection;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        connection = DriverManager.getConnection(DatabaseSettings.URL, DatabaseSettings.USERNAME, DatabaseSettings.PASSWORD);
        return connection;
    }
}
