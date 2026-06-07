package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchOperations {
    
    public static List<Item> searchByKeyword(String keyword) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, u.username as owner_name FROM items i " +
                    "JOIN users u ON i.user_id = u.user_id " +
                    "WHERE (i.item_name LIKE ? OR i.description LIKE ? OR i.category LIKE ?) " +
                    "AND i.is_active = TRUE " +
                    "ORDER BY i.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(createItemFromResultSet(rs));
            }
            
            System.out.println("✅ Found " + items.size() + " items matching: " + keyword);
        } catch (SQLException e) {
            System.out.println("❌ Search failed: " + e.getMessage());
        }
        return items;
    }
    
    public static List<Item> searchByCategory(String category) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, u.username as owner_name FROM items i " +
                    "JOIN users u ON i.user_id = u.user_id " +
                    "WHERE i.category = ? AND i.is_active = TRUE " +
                    "ORDER BY i.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(createItemFromResultSet(rs));
            }
            
            System.out.println("✅ Found " + items.size() + " items in category: " + category);
        } catch (SQLException e) {
            System.out.println("❌ Category search failed: " + e.getMessage());
        }
        return items;
    }
    
    public static List<Item> searchByLocation(String location) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, u.username as owner_name FROM items i " +
                    "JOIN users u ON i.user_id = u.user_id " +
                    "WHERE i.location LIKE ? AND i.is_active = TRUE " +
                    "ORDER BY i.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + location + "%";
            pstmt.setString(1, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(createItemFromResultSet(rs));
            }
            
            System.out.println("✅ Found " + items.size() + " items at location: " + location);
        } catch (SQLException e) {
            System.out.println("❌ Location search failed: " + e.getMessage());
        }
        return items;
    }
    
    public static List<Item> searchByStatus(String status) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, u.username as owner_name FROM items i " +
                    "JOIN users u ON i.user_id = u.user_id " +
                    "WHERE i.status = ? AND i.is_active = TRUE " +
                    "ORDER BY i.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(createItemFromResultSet(rs));
            }
            
            System.out.println("✅ Found " + items.size() + " items with status: " + status);
        } catch (SQLException e) {
            System.out.println("❌ Status search failed: " + e.getMessage());
        }
        return items;
    }
    
    public static List<Item> advancedSearch(String keyword, String category, String location, String status) {
        List<Item> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT i.*, u.username as owner_name FROM items i " +
            "JOIN users u ON i.user_id = u.user_id " +
            "WHERE i.is_active = TRUE"
        );
        
        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            conditions.add("(i.item_name LIKE ? OR i.description LIKE ?)");
            String searchPattern = "%" + keyword + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }
        
        if (category != null && !category.trim().isEmpty()) {
            conditions.add("i.category = ?");
            parameters.add(category.toUpperCase());
        }
        
        if (location != null && !location.trim().isEmpty()) {
            conditions.add("i.location LIKE ?");
            parameters.add("%" + location + "%");
        }
        
        if (status != null && !status.trim().isEmpty()) {
            conditions.add("i.status = ?");
            parameters.add(status.toUpperCase());
        }
        
        if (!conditions.isEmpty()) {
            sql.append(" AND ").append(String.join(" AND ", conditions));
        }
        
        sql.append(" ORDER BY i.created_at DESC");
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(createItemFromResultSet(rs));
            }
            
            System.out.println("✅ Advanced search found " + items.size() + " items");
        } catch (SQLException e) {
            System.out.println("❌ Advanced search failed: " + e.getMessage());
        }
        return items;
    }
    
    public static List<Item> getLostItems() {
        return searchByStatus("LOST");
    }
    
    public static List<Item> getFoundItems() {
        return searchByStatus("FOUND");
    }
    
    public static List<Item> getClaimedItems() {
        return searchByStatus("CLAIMED");
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
