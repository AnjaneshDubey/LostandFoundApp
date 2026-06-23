import backend.DatabaseConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

public class InitDB {
    public static void main(String[] args) {
        try {
            System.out.println("Initializing TiDB Database...");
            String schema = new String(Files.readAllBytes(Paths.get("schema.sql")));
            // Split by ';' but avoid splitting inside strings if possible, though simple split is usually fine for this schema
            String[] queries = schema.split(";");
            
            Connection conn = DatabaseConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            
            for (String query : queries) {
                if (!query.trim().isEmpty()) {
                    stmt.execute(query);
                }
            }
            System.out.println("✅ Schema and default users successfully injected into TiDB!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
