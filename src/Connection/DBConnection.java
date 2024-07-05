package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String CONNECTION_STRING = "jdbc:sqlserver://ROZTEA;Database=LMS_backup;IntegratedSecurity=true;trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Load the SQL Server JDBC driver
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        
        // Establish the connection
        return DriverManager.getConnection(CONNECTION_STRING);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            // If connection is successful
            System.out.println("Connection established successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("SQL Server JDBC Driver not found. Include it in your library path.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to establish a database connection.");
            e.printStackTrace();
        }
    }
}
