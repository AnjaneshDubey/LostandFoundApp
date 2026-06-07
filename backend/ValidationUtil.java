package backend;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PHONE_REGEX = "^[0-9]{10}$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,20}$";
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);
    
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }
    
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    public static boolean isValidItemName(String itemName) {
        return isNotEmpty(itemName) && itemName.trim().length() >= 3 && itemName.trim().length() <= 100;
    }
    
    public static boolean isValidCategory(String category) {
        if (category == null) return false;
        String upper = category.toUpperCase();
        return upper.equals("ELECTRONICS") || upper.equals("BOOKS") || 
               upper.equals("PERSONAL") || upper.equals("DOCUMENTS") || 
               upper.equals("ACCESSORIES") || upper.equals("OTHER");
    }
    
    public static boolean isValidLocation(String location) {
        return isNotEmpty(location) && location.trim().length() >= 3 && location.trim().length() <= 100;
    }
    
    public static boolean isValidStatus(String status) {
        if (status == null) return false;
        String upper = status.toUpperCase();
        return upper.equals("LOST") || upper.equals("FOUND") || 
               upper.equals("CLAIMED") || upper.equals("RETURNED");
    }
    
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[<>\"';]", "");
    }
    
    public static boolean isValidTrackingNumber(String trackingNumber) {
        if (trackingNumber == null) return false;
        return trackingNumber.matches("^LF-\\d{8}-\\d{4}$");
    }
    
    public static String getValidationError(String field, String value) {
        switch (field.toLowerCase()) {
            case "email":
                return isValidEmail(value) ? null : "Invalid email format. Use: user@example.com";
            case "phone":
                return isValidPhone(value) ? null : "Invalid phone number. Use 10 digits.";
            case "username":
                return isValidUsername(value) ? null : "Username must be 3-20 characters (letters, numbers, underscore only)";
            case "password":
                return isValidPassword(value) ? null : "Password must be at least 6 characters";
            case "itemname":
                return isValidItemName(value) ? null : "Item name must be 3-100 characters";
            case "location":
                return isValidLocation(value) ? null : "Location must be 3-100 characters";
            default:
                return null;
        }
    }
}
