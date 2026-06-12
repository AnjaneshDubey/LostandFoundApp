package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserOperations {

    public static User registerUser(String username, String password, String email, String phone, String role, String collegeName) {
        if (!ValidationUtil.isValidUsername(username)) {
            System.out.println("❌ " + ValidationUtil.getValidationError("username", username));
            return null;
        }
        if (!ValidationUtil.isValidEmail(email)) {
            System.out.println("❌ " + ValidationUtil.getValidationError("email", email));
            return null;
        }
        if (!ValidationUtil.isValidPassword(password)) {
            System.out.println("❌ " + ValidationUtil.getValidationError("password", password));
            return null;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        String sql = "INSERT INTO users (username, hashed_password, email, phone, role, college_name) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, role);
            pstmt.setString(6, collegeName);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        System.out.println("✅ User registered successfully! User ID: " + userId);
                        return new User(userId, username, email, phone, role, true, collegeName, null);
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("❌ Username or email already exists!");
            } else {
                System.out.println("❌ Registration failed: " + e.getMessage());
            }
        }
        return null;
    }

    public static User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = TRUE";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String storedHash = rs.getString("hashed_password");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String role = rs.getString("role");
                String collegeName = rs.getString("college_name");
                String avatarBase64 = rs.getString("avatar_data");
                boolean isActive = rs.getBoolean("is_active");

                if (PasswordUtil.verifyPassword(password, storedHash)) {
                    User user = new User(userId, username, email, phone, role, isActive, collegeName, avatarBase64);

                    resetFailedLoginAttempts(userId);
                    updateLastLogin(userId);

                    System.out.println("✅ Login successful! Welcome " + user.getUsername());
                    return user;
                } else {
                    incrementFailedLoginAttempts(userId);
                    System.out.println("❌ Invalid password!");
                }
            } else {
                System.out.println("❌ User not found or account is inactive!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Login failed: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("role"),
                        rs.getBoolean("is_active"),
                        rs.getString("college_name"),
                        rs.getString("avatar_data")
                );
                rs.close();
                return user;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching user: " + e.getMessage());
        }
        return null;
    }

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("role"),
                        rs.getBoolean("is_active"),
                        rs.getString("college_name"),
                        rs.getString("avatar_data")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching users: " + e.getMessage());
        }
        return users;
    }

    public static boolean updateUser(int userId, String email, String phone) {
        String sql = "UPDATE users SET email = ?, phone = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            pstmt.setInt(3, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ User updated successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Update failed: " + e.getMessage());
        }
        return false;
    }

    public static String updateUserProfile(int userId, String newUsername, String email, String phone, String collegeName) {
        if (!ValidationUtil.isValidUsername(newUsername)) return ValidationUtil.getValidationError("username", newUsername);
        if (!ValidationUtil.isValidEmail(email)) return ValidationUtil.getValidationError("email", email);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            
            String selectSql = "SELECT username, last_username_change FROM users WHERE user_id = ?";
            pstmt = conn.prepareStatement(selectSql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            if (!rs.next()) return "User not found.";
            
            String currentUsername = rs.getString("username");
            Timestamp lastChange = rs.getTimestamp("last_username_change");
            
            boolean usernameChanged = !currentUsername.equalsIgnoreCase(newUsername);
            
            if (usernameChanged) {
                if (lastChange != null) {
                    long diffInMillis = System.currentTimeMillis() - lastChange.getTime();
                    long daysSinceChange = diffInMillis / (1000L * 60 * 60 * 24);
                    if (daysSinceChange < 14) {
                        return "Username can only be changed once per 14 days. Days since last change: " + daysSinceChange;
                    }
                }
                
                String checkUserSql = "SELECT user_id FROM users WHERE username = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkUserSql)) {
                    checkStmt.setString(1, newUsername);
                    ResultSet checkRs = checkStmt.executeQuery();
                    if (checkRs.next()) return "Username already taken.";
                }
            }
            
            String checkEmailSql = "SELECT user_id FROM users WHERE email = ? AND user_id != ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkEmailSql)) {
                checkStmt.setString(1, email);
                checkStmt.setInt(2, userId);
                ResultSet checkRs = checkStmt.executeQuery();
                if (checkRs.next()) return "Email already taken.";
            }
            
            String updateSql;
            if (usernameChanged) {
                updateSql = "UPDATE users SET username = ?, email = ?, phone = ?, college_name = ?, last_username_change = CURRENT_TIMESTAMP WHERE user_id = ?";
            } else {
                updateSql = "UPDATE users SET email = ?, phone = ?, college_name = ? WHERE user_id = ?";
            }
            
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                if (usernameChanged) {
                    updateStmt.setString(1, newUsername);
                    updateStmt.setString(2, email);
                    updateStmt.setString(3, phone);
                    updateStmt.setString(4, collegeName);
                    updateStmt.setInt(5, userId);
                } else {
                    updateStmt.setString(1, email);
                    updateStmt.setString(2, phone);
                    updateStmt.setString(3, collegeName);
                    updateStmt.setInt(4, userId);
                }
                updateStmt.executeUpdate();
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean updateUserAvatar(int userId, String avatarBase64) {
        String sql = "UPDATE users SET avatar_data = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, avatarBase64);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deactivateUser(int userId) {
        String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ User deactivated successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Deactivation failed: " + e.getMessage());
        }
        return false;
    }

    private static void incrementFailedLoginAttempts(int userId) {
        String sql = "UPDATE users SET failed_login_attempts = failed_login_attempts + 1 WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error updating login attempts: " + e.getMessage());
        }
    }

    private static void resetFailedLoginAttempts(int userId) {
        String sql = "UPDATE users SET failed_login_attempts = 0 WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error resetting login attempts: " + e.getMessage());
        }
    }

    private static void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error updating last login: " + e.getMessage());
        }
    }
}
