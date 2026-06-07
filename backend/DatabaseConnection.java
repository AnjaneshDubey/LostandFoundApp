package backend;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;

    private DatabaseConnection() {
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream("database.properties");
            props.load(fis);
            fis.close();

            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");

            Class.forName(props.getProperty("db.driver"));
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Database connected successfully!");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("❌ Could not load database.properties file!");
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing database connection!");
            e.printStackTrace();
        }
    }

    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
