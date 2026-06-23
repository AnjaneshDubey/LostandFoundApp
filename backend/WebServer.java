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
    private static final Gson gson = new Gson();

    public static void start() {
        int port = 8080;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isEmpty()) {
            try {
                port = Integer.parseInt(envPort);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid PORT environment variable. Defaulting to 8080.");
            }
        }

        try {
            // Test database connection and initialize schema if using H2
            try {
                java.util.Properties props = new java.util.Properties();
                try (FileInputStream fis = new FileInputStream("database.properties")) {
                    props.load(fis);
                }
                String url = props.getProperty("db.url");
                
                // Initialize DatabaseConnection singleton to verify connection
                DatabaseConnection dbConn = DatabaseConnection.getInstance();

                if (url != null && url.startsWith("jdbc:h2:")) {
                    try (java.sql.Statement s = dbConn.getConnection().createStatement()) {
                        s.execute("RUNSCRIPT FROM 'schema.sql'");
                        System.out.println("✅ Database schema initialized for H2 database.");
                    }
                } else {
                    System.out.println("✅ Using persistent database. Skipping H2 schema initialization script.");
                }
            } catch (Exception e) {
                System.out.println("❌ Database initialization error: " + e.getMessage());
            }

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            
            server.createContext("/api/login", new LoginHandler());
            server.createContext("/api/register", new RegisterHandler());
            server.createContext("/api/user/action", new UserActionHandler());
            server.createContext("/api/items", new ItemsHandler());
            server.createContext("/api/items/action", new ItemActionHandler());
            server.createContext("/", new StaticFileHandler());
            
            server.setExecutor(null); // creates a default executor
            server.start();
            System.out.println("✅ Web Server started at http://localhost:" + port);
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

    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    Map<String, String> data = gson.fromJson(isr, Map.class);
                    
                    String username = data.get("username");
                    String password = data.get("password");
                    String email = data.get("email");
                    String phone = data.get("phone");
                    String role = "STUDENT"; // Always force STUDENT for new registrations via UI
                    String collegeName = data.getOrDefault("collegeName", "G.L Bajaj Institute of Technology and Management, Greater Noida");
                    
                    User user = UserOperations.registerUser(username, password, email, phone, role, collegeName);
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    if (user != null) {
                        String response = gson.toJson(user);
                        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(200, bytes.length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(bytes);
                        os.close();
                    } else {
                        String response = "{\"error\":\"Registration failed. User may already exist or data is invalid.\"}";
                        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(400, bytes.length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(bytes);
                        os.close();
                    }
                } catch (Exception e) {
                    String response = "{\"error\":\"Internal server error.\"}";
                    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(500, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    static class UserActionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    Map<String, Object> data = gson.fromJson(isr, Map.class);
                    
                    String action = (String) data.get("action");
                    
                    if ("UPDATE_PROFILE".equals(action)) {
                        int userId = ((Number) data.get("userId")).intValue();
                        String username = (String) data.get("username");
                        String email = (String) data.get("email");
                        String phone = (String) data.get("phone");
                        String collegeName = (String) data.get("collegeName");
                        
                        String error = UserOperations.updateUserProfile(userId, username, email, phone, collegeName);
                        
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        if (error == null) {
                            User updatedUser = UserOperations.getUserById(userId);
                            String response = gson.toJson(updatedUser);
                            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                            exchange.sendResponseHeaders(200, bytes.length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(bytes);
                            os.close();
                        } else {
                            String response = "{\"error\":\"" + error.replace("\"", "\\\"") + "\"}";
                            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                            exchange.sendResponseHeaders(400, bytes.length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(bytes);
                            os.close();
                        }
                    } else if ("UPDATE_AVATAR".equals(action)) {
                        int userId = ((Number) data.get("userId")).intValue();
                        String avatarBase64 = (String) data.get("avatarBase64");
                        
                        boolean success = UserOperations.updateUserAvatar(userId, avatarBase64);
                        
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        if (success) {
                            User updatedUser = UserOperations.getUserById(userId);
                            String response = gson.toJson(updatedUser);
                            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                            exchange.sendResponseHeaders(200, bytes.length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(bytes);
                            os.close();
                        } else {
                            String response = "{\"error\":\"Failed to update avatar.\"}";
                            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                            exchange.sendResponseHeaders(500, bytes.length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(bytes);
                            os.close();
                        }
                    } else {
                        String response = "{\"error\":\"Unknown action\"}";
                        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(400, bytes.length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(bytes);
                        os.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String response = "{\"error\":\"Internal server error.\"}";
                    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(500, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    static class ItemsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String collegeName = "G.L Bajaj Institute of Technology and Management, Greater Noida";
                if (query != null) {
                    for (String param : query.split("&")) {
                        if (param.startsWith("college=")) {
                            collegeName = java.net.URLDecoder.decode(param.substring(8), StandardCharsets.UTF_8.name());
                            break;
                        }
                    }
                }
                List<Item> items = ItemOperations.getAllItems(collegeName);
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

    static class ItemActionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    Map<String, Object> data = gson.fromJson(isr, Map.class);
                    
                    String action = (String) data.get("action");
                    boolean success = false;
                    
                    if ("DELETE".equals(action)) {
                        int itemId = ((Number) data.get("itemId")).intValue();
                        success = ItemOperations.deleteItem(itemId);
                    } else if ("UPDATE_STATUS".equals(action)) {
                        int itemId = ((Number) data.get("itemId")).intValue();
                        int adminId = ((Number) data.get("adminId")).intValue();
                        String newStatus = (String) data.get("status");
                        success = ItemOperations.updateItemStatus(itemId, newStatus, adminId);
                    } else if ("REPORT".equals(action)) {
                        String status = (String) data.get("status");
                        String itemName = (String) data.get("itemName");
                        String description = (String) data.get("description");
                        String category = (String) data.get("category");
                        String location = (String) data.get("location");
                        String collegeName = (String) data.get("collegeName");
                        String imageBase64 = (String) data.get("imageBase64");
                        int userId = ((Number) data.get("userId")).intValue();
                        long dateMillis = ((Number) data.get("date")).longValue();
                        java.sql.Date sqlDate = new java.sql.Date(dateMillis);
                        
                        if ("LOST".equals(status)) {
                            success = ItemOperations.reportLostItem(userId, itemName, description, category, location, sqlDate, collegeName, imageBase64) != null;
                        } else {
                            success = ItemOperations.reportFoundItem(userId, itemName, description, category, location, sqlDate, collegeName, imageBase64) != null;
                        }
                    } else if ("EDIT".equals(action)) {
                        int itemId = ((Number) data.get("itemId")).intValue();
                        String itemName = (String) data.get("itemName");
                        String description = (String) data.get("description");
                        String category = (String) data.get("category");
                        String location = (String) data.get("location");
                        String imageBase64 = (String) data.get("imageBase64");
                        success = ItemOperations.updateItem(itemId, itemName, description, category, location, imageBase64);
                    }
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    if (success) {
                        String response = "{\"success\":true}";
                        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(200, bytes.length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(bytes);
                        os.close();
                    } else {
                        String response = "{\"error\":\"Action failed.\"}";
                        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(400, bytes.length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(bytes);
                        os.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String response = "{\"error\":\"Internal server error.\"}";
                    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(500, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
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
                else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) contentType = "image/jpeg";
                else if (path.endsWith(".png")) contentType = "image/png";
                else if (path.endsWith(".ico")) contentType = "image/x-icon";
                
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
