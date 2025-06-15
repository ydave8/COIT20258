package disasterresponsesystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Initializes the  database and tables if they don't exist.
 */
public class DBInitializer {

    private static final String ROOT_URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "mysql";

    public static void initialize() {
        try {
            // 1. Connect to MySQL without specifying a database
            Connection conn = DriverManager.getConnection(ROOT_URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();

            // 2. Create the database if it doesn't exist
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS disasterdb");
            conn.close();

            // 3. Connect to the disasterdb
            Connection dbConn = DriverManager.getConnection(ROOT_URL + "disasterdb", USER, PASSWORD);
            Statement dbStmt = dbConn.createStatement();

            // 4. Create incidents table
//            String sql = 
//                "CREATE TABLE IF NOT EXISTS incidents ("
//                 +   "id INT AUTO_INCREMENT PRIMARY KEY,"
//                 +   "type VARCHAR(50),"
//                 +   "location VARCHAR(100),"
//                 +   "severity VARCHAR(20),"
//                 +   "reported_by VARCHAR(100),"
//                 +   "reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
//                 + ");"
                    
            String sql = "CREATE TABLE IF NOT EXISTS incidents ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "type VARCHAR(50), "
                    + "location VARCHAR(100), "
                    + "severity VARCHAR(20), "
                    + "department VARCHAR(100), "
                    + "status VARCHAR(20) DEFAULT 'Pending', "
                    + "reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "responder VARCHAR(100) DEFAULT 'Rescue'"
                    + ");";                    
            ;
            dbStmt.executeUpdate(sql);

            dbConn.close();
            System.out.println("✅ Database and table initialized.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}