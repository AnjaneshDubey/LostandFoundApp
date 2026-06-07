package frontend;

import backend.DatabaseConnection;
import backend.WebServer;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize Database Connection
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            
            if (dbConnection.testConnection()) {
                System.out.println("\n🚀 Starting Application...");
                
                // Start the new Web Server instead of console UI
                WebServer.start();
                
            } else {
                System.out.println("❌ Application failed to start due to database connection error.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Fatal Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
