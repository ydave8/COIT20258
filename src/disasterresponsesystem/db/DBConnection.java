package disasterresponsesystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton-style helper for obtaining a connection to the MySQL
 * database used by DRS-Enhanced.
 *
 * Update URL, USER and PASSWORD to match your local MySQL setup.
 */
public class DBConnection {

    // Change “disasterdb” (DB name), user and password as required
    private static final String URL      = "jdbc:mysql://localhost:3306/disasterdb";
    private static final String USER     = "root";
    private static final String PASSWORD = "mysql";

    private DBConnection() { /* prevent instantiation */ }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}