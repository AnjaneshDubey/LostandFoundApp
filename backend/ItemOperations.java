package backend;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ItemOperations {
    
    private static int sequenceCounter = 1;
    
    public static String generateTrackingNumber() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateStr = today.format(formatter);
        String trackingNumber = String.format("LF-%s-%04d", dateStr, sequenceCounter++);
        return trackingNumber;
    }
    
    public static Item reportLostItem(int userId, String itemName, String description, 
                                     String category, String location, Date dateLost) {
        if (!ValidationUtil.isValidItemName(itemName)) {
            System.out.println("❌ " + ValidationUtil.getValidationError("itemname", itemName));
            return null;
        }
        if (!ValidationUtil.isValidCategory(category)) {
            System.out.println("❌ Invalid category!");
            return null;
        }
        if (!ValidationUtil.isValidLocation(location)) {
            System.out.println("❌ " + ValidationUtil.getValidationError("location", location));
            return null;
        }
        
        String trackingNumber = generateTrackingNumber();
        String sql = "INSERT INTO items (tracking_number, user_id, item_name, description, category, location, date_lost, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'LOST')";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, trackingNumber);
            pstmt.setInt(2, userId);
            pstmt.setString(3, itemName);
            pstmt.setString(4, description);
            pstmt.setString(5, category.toUpperCase());
            pstmt.setString(6, location);
            pstmt.setDate(7, dateLost);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int itemId = generatedKeys.getInt(1);
                        System.out.println("✅ Lost item reported! Tracking Number: " + trackingNumber);
                        return new Item(itemId, trackingNumber, itemName, description, category, location, "LOST", null);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reporting lost item: " + e.getMessage());
        }
        return null;
    }
    
    public static Item reportFoundItem(int userId, String itemName, String description, 
                                      String category, String location, Date dateFound) {
        if (!ValidationUtil.isValidItemName(itemName)) {
            System.out.println("❌ " + ValidationUtil.getValidationError("itemname", itemName));
            return null;
        }
        
        String trackingNumber = generateTrackingNumber();
        String sql = "INSERT INTO items (tracking_number, user_id, item_name, description, category, location, date_found, status, found_by) VALUES (?, ?, ?, ?, ?, ?, ?, 'FOUND', ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, trackingNumber);
            pstmt.setInt(2, userId);
            pstmt.setString(3, itemName);
            pstmt.setString(4, description);
            pstmt.setString(5, category.toUpperCase());
            pstmt.setString(6, location);
            pstmt.setDate(7, dateFound);
            pstmt.setInt(8, userId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int itemId = generatedKeys.getInt(1);
                        System.out.println("✅ Found item reported! Tracking Number: " + trackingNumber);
                        return new Item(itemId, trackingNumber, itemName, description, category, location, "FOUND", null);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reporting found item: " + e.getMessage());
        }
        return null;
    }
    
    public static List<Item> getItemsByUser(int userId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, u.username as owner_name FROM items i JOIN users u ON i.user_id = u.user_id WHERE i.user_id = ? AND i.is_active = TRUE ORDER BY i.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Item item = createItemFromResultSet(rs);
                items.add(item);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching user items: " + e.getMessage());
        }
        return items;
    }
    
    public static List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, u.username as owner_name FROM items i JOIN users u ON i.user_id = u.user_id WHERE i.is_active = TRUE ORDER BY i.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                items.add(createItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching items: " + e.getMessage());
        }
        return items;
    }
    
    public static boolean updateItemStatus(int itemId, String newStatus, int updatedBy) {
        if (!ValidationUtil.isValidStatus(newStatus)) {
            System.out.println("❌ Invalid status!");
            return false;
        }
        
        String sql = "UPDATE items SET status = ? WHERE item_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus.toUpperCase());
            pstmt.setInt(2, itemId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logStatusUpdate(itemId, newStatus.toUpperCase(), updatedBy);
                System.out.println("✅ Item status updated to: " + newStatus);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error updating status: " + e.getMessage());
        }
        return false;
    }
    
    public static boolean deleteItem(int itemId) {
        String sql = "UPDATE items SET is_active = FALSE WHERE item_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Item deleted successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error deleting item: " + e.getMessage());
        }
        return false;
    }
    
    public static Item getItemByTrackingNumber(String trackingNumber) {
        String sql = "SELECT i.*, u.username as owner_name FROM items i JOIN users u ON i.user_id = u.user_id WHERE i.tracking_number = ? AND i.is_active = TRUE";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, trackingNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createItemFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching item: " + e.getMessage());
        }
        return null;
    }
    
    private static void logStatusUpdate(int itemId, String newStatus, int updatedBy) {
        String sql = "INSERT INTO status_updates (item_id, new_status, updated_by) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            pstmt.setString(2, newStatus);
            pstmt.setInt(3, updatedBy);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error logging status update: " + e.getMessage());
        }
    }
    
    private static Item createItemFromResultSet(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setItemId(rs.getInt("item_id"));
        item.setTrackingNumber(rs.getString("tracking_number"));
        item.setUserId(rs.getInt("user_id"));
        item.setItemName(rs.getString("item_name"));
        item.setDescription(rs.getString("description"));
        item.setCategory(rs.getString("category"));
        item.setLocation(rs.getString("location"));
        item.setDateLost(rs.getDate("date_lost"));
        item.setDateFound(rs.getDate("date_found"));
        item.setStatus(rs.getString("status"));
        item.setFoundBy((Integer) rs.getObject("found_by"));
        item.setActive(rs.getBoolean("is_active"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        item.setOwnerUsername(rs.getString("owner_name"));
        return item;
    }
}
