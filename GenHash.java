import backend.PasswordUtil;

public class GenHash {
    public static void main(String[] args) {
        System.out.println("admin: " + PasswordUtil.hashPassword("admin123"));
        System.out.println("student: " + PasswordUtil.hashPassword("student123"));
    }
}
