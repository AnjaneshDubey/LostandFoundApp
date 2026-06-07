package frontend;

import backend.User;
import backend.UserOperations;
import backend.ValidationUtil;

public class LoginView {
    
    public static User showLoginScreen() {
        ConsoleUI.printHeader("LOGIN TO LOST & FOUND TRACKER");
        
        String username = ConsoleUI.getInput("Username");
        String password = ConsoleUI.getPassword("Password");
        
        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(password)) {
            ConsoleUI.printError("Username and password cannot be empty!");
            ConsoleUI.pause();
            return null;
        }
        
        User user = UserOperations.loginUser(username, password);
        
        if (user != null) {
            ConsoleUI.printSuccess("Login successful! Welcome back, " + user.getUsername() + "!");
            ConsoleUI.pause();
        } else {
            ConsoleUI.printError("Login failed! Please check your credentials.");
            ConsoleUI.pause();
        }
        
        return user;
    }
    
    public static User showRegistrationScreen() {
        ConsoleUI.printHeader("REGISTER NEW ACCOUNT");
        
        String username = ConsoleUI.getInput("Username (3-20 characters, alphanumeric)");
        String email = ConsoleUI.getInput("Email");
        String phone = ConsoleUI.getInput("Phone (10 digits)");
        String password = ConsoleUI.getPassword("Password (minimum 6 characters)");
        String confirmPassword = ConsoleUI.getPassword("Confirm Password");
        
        if (!password.equals(confirmPassword)) {
            ConsoleUI.printError("Passwords do not match!");
            ConsoleUI.pause();
            return null;
        }
        
        String roleChoice = ConsoleUI.getInputWithDefault("Register as (STUDENT/ADMIN)", "STUDENT");
        String role = roleChoice.toUpperCase();
        
        if (!role.equals("STUDENT") && !role.equals("ADMIN")) {
            role = "STUDENT";
        }
        
        User user = UserOperations.registerUser(username, password, email, phone, role);
        
        if (user != null) {
            ConsoleUI.printSuccess("Registration successful! You can now login.");
            ConsoleUI.pause();
        } else {
            ConsoleUI.printError("Registration failed! Please try again.");
            ConsoleUI.pause();
        }
        
        return user;
    }
    
    public static User showWelcomeScreen() {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("COLLEGE LOST & FOUND TRACKER");
            ConsoleUI.printInfo("Keep track of lost and found items on campus!");
            
            String[] options = {
                "Login",
                "Register New Account",
                "Exit"
            };
            
            int choice = ConsoleUI.getMenuChoice(options);
            
            switch (choice) {
                case 1:
                    User loggedInUser = showLoginScreen();
                    if (loggedInUser != null) {
                        return loggedInUser;
                    }
                    break;
                    
                case 2:
                    User registeredUser = showRegistrationScreen();
                    if (registeredUser != null) {
                        ConsoleUI.printInfo("Please login with your new account.");
                        ConsoleUI.pause();
                    }
                    break;
                    
                case 3:
                    if (ConsoleUI.confirm("Are you sure you want to exit?")) {
                        ConsoleUI.printInfo("Thank you for using Lost & Found Tracker!");
                        return null;
                    }
                    break;
                    
                default:
                    ConsoleUI.printError("Invalid choice!");
                    ConsoleUI.pause();
            }
        }
    }
}
