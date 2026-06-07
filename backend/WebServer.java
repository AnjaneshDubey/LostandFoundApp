package backend;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class WebServer {
    private static final int PORT = 8080;
    private static final Gson gson = new Gson();

    public static void start() {
        try {
            // Initialize database schema once
            try (java.sql.Connection c = java.sql.DriverManager.getConnection("jdbc:h2:mem:lostfound_db;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
                 java.sql.Statement s = c.createStatement()) {
                s.execute("RUNSCRIPT FROM 'schema.sql'");
                System.out.println("✅ Database schema initialized.");
            } catch (Exception e) {
                System.out.println("ℹ️ Database already initialized or schema error: " + e.getMessage());
            }

            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            server.createContext("/api/login", new LoginHandler());
            server.createContext("/api/items", new ItemsHandler());
            server.createContext("/", new StaticFileHandler());
            
            server.setExecutor(null); // creates a default executor
            server.start();
            System.out.println("✅ Web Server started at http://localhost:" + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                Map<String, String> credentials = gson.fromJson(isr, Map.class);
                
                String username = credentials.get("username");
                String password = credentials.get("password");
                
                User user = UserOperations.loginUser(username, password);
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                if (user != null) {
                    String response = gson.toJson(user);
                    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                } else {
                    String response = "{\"error\":\"Invalid credentials\"}";
                    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(401, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class ItemsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Item> items = ItemOperations.getAllItems();
                String response = gson.toJson(items);
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }
            
            File file = new File("public" + path);
            if (file.exists() && !file.isDirectory()) {
                String contentType = "text/plain";
                if (path.endsWith(".html")) contentType = "text/html";
                else if (path.endsWith(".css")) contentType = "text/css";
                else if (path.endsWith(".js")) contentType = "application/javascript";
                
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, file.length());
                
                try (OutputStream os = exchange.getResponseBody()) {
                    Files.copy(file.toPath(), os);
                }
            } else {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
