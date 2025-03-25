package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionUtils {
    public static Connection openConnection() throws SQLException, ClassNotFoundException {
        return openConnection(DatabaseSettings.URL, DatabaseSettings.USERNAME, DatabaseSettings.PASSWORD);
    }

    public static Connection openConnection(String url, String username, String password) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(url,username, password);
    }

    private ConnectionUtils() {}
}
