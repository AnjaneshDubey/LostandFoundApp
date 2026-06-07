package frontend;

import backend.User;
import backend.UserOperations;
import backend.ItemOperations;
import java.util.List;

public class AdminMenu {
    
    public static void showAdminDashboard(User admin) {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("ADMIN DASHBOARD");
            ConsoleUI.printInfo("Logged in as: " + admin.getUsername() + " (Administrator)");
            
            String[] options = {
                "View All Items",
                "View All Users",
                "Search Items",
                "Update Item Status",
                "Delete Item",
                "View Item Details",
                "Deactivate User Account",
                "Generate Reports",
                "Student Functions",
                "Logout"
            };
            
            int choice = ConsoleUI.getMenuChoice(options);
            
            switch (choice) {
                case 1:
                    viewAllItems();
                    break;
                    
                case 2:
                    viewAllUsers();
                    break;
                    
                case 3:
                    SearchView.showSearchMenu();
                    break;
                    
                case 4:
                    ItemView.updateItemStatus(admin.getUserId(), true);
                    break;
                    
                case 5:
                    deleteItem();
                    break;
                    
                case 6:
                    ItemView.viewItemDetails();
                    break;
                    
                case 7:
                    deactivateUser();
                    break;
                    
                case 8:
                    generateReports();
                    break;
                    
                case 9:
                    StudentMenu.showStudentDashboard(admin);
                    break;
                    
                case 10:
                    if (ConsoleUI.confirm("Are you sure you want to logout?")) {
                        ConsoleUI.printSuccess("Logged out successfully!");
                        ConsoleUI.pause();
                        return;
                    }
                    break;
                    
                default:
                    ConsoleUI.printError("Invalid choice!");
                    ConsoleUI.pause();
            }
        }
    }
    
    private static void viewAllItems() {
        ConsoleUI.printHeader("ALL ITEMS IN SYSTEM");
        ItemView.viewAllItems();
    }
    
    private static void viewAllUsers() {
        ConsoleUI.printHeader("ALL REGISTERED USERS");
        
        List<User> users = UserOperations.getAllUsers();
        
        if (users.isEmpty()) {
            ConsoleUI.printInfo("No users in the system.");
            ConsoleUI.pause();
            return;
        }
        
        String[] headers = {"User ID", "Username", "Email", "Phone", "Role", "Status"};
        String[][] data = new String[users.size()][6];
        
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            data[i][0] = String.valueOf(user.getUserId());
            data[i][1] = user.getUsername();
            data[i][2] = user.getEmail();
            data[i][3] = user.getPhone() != null ? user.getPhone() : "N/A";
            data[i][4] = user.getRole();
            data[i][5] = user.isActive() ? "Active" : "Inactive";
        }
        
        ConsoleUI.showTable(headers, data);
        ConsoleUI.printInfo("Total Users: " + users.size());
        ConsoleUI.pause();
    }
    
    private static void deleteItem() {
        ConsoleUI.printHeader("DELETE ITEM");
        
        String trackingNumber = ConsoleUI.getInput("Enter Tracking Number to delete");
        
        backend.Item item = ItemOperations.getItemByTrackingNumber(trackingNumber);
        
        if (item == null) {
            ConsoleUI.printError("Item not found!");
            ConsoleUI.pause();
            return;
        }
        
        ConsoleUI.printInfo("Item: " + item.getItemName() + " (" + item.getCategory() + ")");
        ConsoleUI.printInfo("Location: " + item.getLocation());
        ConsoleUI.printInfo("Status: " + item.getStatus());
        
        if (ConsoleUI.confirm("Are you sure you want to delete this item?")) {
            if (ItemOperations.deleteItem(item.getItemId())) {
                ConsoleUI.printSuccess("Item deleted successfully!");
            } else {
                ConsoleUI.printError("Failed to delete item!");
            }
        } else {
            ConsoleUI.printInfo("Delete operation cancelled.");
        }
        
        ConsoleUI.pause();
    }
    
    private static void deactivateUser() {
        ConsoleUI.printHeader("DEACTIVATE USER ACCOUNT");
        
        int userId = ConsoleUI.getIntInput("Enter User ID to deactivate");
        
        User user = UserOperations.getUserById(userId);
        
        if (user == null) {
            ConsoleUI.printError("User not found!");
            ConsoleUI.pause();
            return;
        }
        
        if (!user.isActive()) {
            ConsoleUI.printWarning("User is already inactive!");
            ConsoleUI.pause();
            return;
        }
        
        ConsoleUI.printInfo("Username: " + user.getUsername());
        ConsoleUI.printInfo("Email: " + user.getEmail());
        ConsoleUI.printInfo("Role: " + user.getRole());
        
        if (ConsoleUI.confirm("Are you sure you want to deactivate this user?")) {
            if (UserOperations.deactivateUser(userId)) {
                ConsoleUI.printSuccess("User deactivated successfully!");
            } else {
                ConsoleUI.printError("Failed to deactivate user!");
            }
        } else {
            ConsoleUI.printInfo("Deactivation cancelled.");
        }
        
        ConsoleUI.pause();
    }
    
    private static void generateReports() {
        ConsoleUI.printHeader("SYSTEM REPORTS");
        
        String[] reportOptions = {
            "Items by Status Report",
            "Items by Category Report",
            "User Activity Report",
            "Back to Admin Menu"
        };
        
        int choice = ConsoleUI.getMenuChoice(reportOptions);
        
        switch (choice) {
            case 1:
                itemsByStatusReport();
                break;
            case 2:
                itemsByCategoryReport();
                break;
            case 3:
                userActivityReport();
                break;
            case 4:
                return;
        }
    }
    
    private static void itemsByStatusReport() {
        ConsoleUI.printSubHeader("Items by Status Report");
        
        int lostCount = backend.SearchOperations.getLostItems().size();
        int foundCount = backend.SearchOperations.getFoundItems().size();
        int claimedCount = backend.SearchOperations.getClaimedItems().size();
        
        ConsoleUI.printDivider();
        System.out.println("Lost Items     : " + lostCount);
        System.out.println("Found Items    : " + foundCount);
        System.out.println("Claimed Items  : " + claimedCount);
        ConsoleUI.printDivider();
        
        ConsoleUI.pause();
    }
    
    private static void itemsByCategoryReport() {
        ConsoleUI.printSubHeader("Items by Category Report");
        
        String[] categories = {"ELECTRONICS", "BOOKS", "PERSONAL", "DOCUMENTS", "ACCESSORIES", "OTHER"};
        
        ConsoleUI.printDivider();
        for (String category : categories) {
            int count = backend.SearchOperations.searchByCategory(category).size();
            System.out.printf("%-15s : %d items%n", category, count);
        }
        ConsoleUI.printDivider();
        
        ConsoleUI.pause();
    }
    
    private static void userActivityReport() {
        ConsoleUI.printSubHeader("User Activity Report");
        
        List<User> users = UserOperations.getAllUsers();
        int activeUsers = 0;
        int inactiveUsers = 0;
        int adminCount = 0;
        int studentCount = 0;
        
        for (User user : users) {
            if (user.isActive()) activeUsers++;
            else inactiveUsers++;
            
            if (user.isAdmin()) adminCount++;
            else studentCount++;
        }
        
        ConsoleUI.printDivider();
        System.out.println("Total Users       : " + users.size());
        System.out.println("Active Users      : " + activeUsers);
        System.out.println("Inactive Users    : " + inactiveUsers);
        System.out.println("Administrators    : " + adminCount);
        System.out.println("Students          : " + studentCount);
        ConsoleUI.printDivider();
        
        ConsoleUI.pause();
    }
}
