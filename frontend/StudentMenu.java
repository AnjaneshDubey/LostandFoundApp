package frontend;

import backend.User;

public class StudentMenu {
    
    public static void showStudentDashboard(User student) {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("STUDENT DASHBOARD");
            ConsoleUI.printInfo("Logged in as: " + student.getUsername() + " (" + student.getEmail() + ")");
            
            String[] options = {
                "Report Lost Item",
                "Report Found Item",
                "View My Items",
                "Search Items",
                "View Item Details",
                "Update Item Status",
                "View All Lost Items",
                "View All Found Items",
                "Logout"
            };
            
            int choice = ConsoleUI.getMenuChoice(options);
            
            switch (choice) {
                case 1:
                    ItemView.reportLostItem(student.getUserId());
                    break;
                    
                case 2:
                    ItemView.reportFoundItem(student.getUserId());
                    break;
                    
                case 3:
                    ItemView.viewMyItems(student.getUserId());
                    break;
                    
                case 4:
                    SearchView.showSearchMenu();
                    break;
                    
                case 5:
                    ItemView.viewItemDetails();
                    break;
                    
                case 6:
                    ItemView.updateItemStatus(student.getUserId(), false);
                    break;
                    
                case 7:
                    viewQuickLostItems();
                    break;
                    
                case 8:
                    viewQuickFoundItems();
                    break;
                    
                case 9:
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
    
    private static void viewQuickLostItems() {
        ConsoleUI.printHeader("ALL LOST ITEMS");
        ItemView.viewAllItems();
    }
    
    private static void viewQuickFoundItems() {
        ConsoleUI.printHeader("ALL FOUND ITEMS");
        ItemView.viewAllItems();
    }
}
