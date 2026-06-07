package backend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    
    private static final int SALT_LENGTH = 16;
    
    public static String hashPassword(String password) {
        try {
            byte[] salt = generateSalt();
            String saltString = Base64.getEncoder().encodeToString(salt);
            String hashedPassword = hashWithSHA256(password, salt);
            return saltString + ":" + hashedPassword;
        } catch (Exception e) {
            System.err.println("❌ Error hashing password: " + e.getMessage());
            return null;
        }
    }
    
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String hashedPassword = hashWithSHA256(password, salt);
            
            return hashedPassword.equals(parts[1]);
        } catch (Exception e) {
            System.err.println("❌ Error verifying password: " + e.getMessage());
            return false;
        }
    }
    
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
    
    private static String hashWithSHA256(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
    
    public static String generateSimpleHash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("❌ SHA-256 algorithm not found!");
            return null;
        }
    }
    
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        
        return hasUpper && hasLower && hasDigit;
    }
}
