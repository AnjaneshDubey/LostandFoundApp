package frontend;

import backend.Item;
import backend.SearchOperations;
import java.util.List;

public class SearchView {
    
    public static void showSearchMenu() {
        while (true) {
            ConsoleUI.printHeader("SEARCH ITEMS");
            
            String[] options = {
                "Search by Keyword",
                "Search by Category",
                "Search by Location",
                "Search by Status",
                "Advanced Search",
                "View All Lost Items",
                "View All Found Items",
                "Back to Main Menu"
            };
            
            int choice = ConsoleUI.getMenuChoice(options);
            
            switch (choice) {
                case 1:
                    searchByKeyword();
                    break;
                case 2:
                    searchByCategory();
                    break;
                case 3:
                    searchByLocation();
                    break;
                case 4:
                    searchByStatus();
                    break;
                case 5:
                    advancedSearch();
                    break;
                case 6:
                    viewLostItems();
                    break;
                case 7:
                    viewFoundItems();
                    break;
                case 8:
                    return;
                default:
                    ConsoleUI.printError("Invalid choice!");
            }
        }
    }
    
    private static void searchByKeyword() {
        ConsoleUI.printSubHeader("Search by Keyword");
        
        String keyword = ConsoleUI.getInput("Enter search keyword (item name/description)");
        
        if (keyword.isEmpty()) {
            ConsoleUI.printError("Keyword cannot be empty!");
            ConsoleUI.pause();
            return;
        }
        
        List<Item> results = SearchOperations.searchByKeyword(keyword);
        
        if (results.isEmpty()) {
            ConsoleUI.printInfo("No items found matching: " + keyword);
        } else {
            ConsoleUI.printSuccess("Found " + results.size() + " items");
            ItemView.displayItemsTable(results);
        }
        
        ConsoleUI.pause();
    }
    
    private static void searchByCategory() {
        ConsoleUI.printSubHeader("Search by Category");
        
        ConsoleUI.printInfo("Available Categories:");
        System.out.println("1. ELECTRONICS");
        System.out.println("2. BOOKS");
        System.out.println("3. PERSONAL");
        System.out.println("4. DOCUMENTS");
        System.out.println("5. ACCESSORIES");
        System.out.println("6. OTHER");
        
        String category = ConsoleUI.getInput("Enter category").toUpperCase();
        
        List<Item> results = SearchOperations.searchByCategory(category);
        
        if (results.isEmpty()) {
            ConsoleUI.printInfo("No items found in category: " + category);
        } else {
            ConsoleUI.printSuccess("Found " + results.size() + " items");
            ItemView.displayItemsTable(results);
        }
        
        ConsoleUI.pause();
    }
    
    private static void searchByLocation() {
        ConsoleUI.printSubHeader("Search by Location");
        
        String location = ConsoleUI.getInput("Enter location (e.g., Library, Cafeteria)");
        
        if (location.isEmpty()) {
            ConsoleUI.printError("Location cannot be empty!");
            ConsoleUI.pause();
            return;
        }
        
        List<Item> results = SearchOperations.searchByLocation(location);
        
        if (results.isEmpty()) {
            ConsoleUI.printInfo("No items found at location: " + location);
        } else {
            ConsoleUI.printSuccess("Found " + results.size() + " items");
            ItemView.displayItemsTable(results);
        }
        
        ConsoleUI.pause();
    }
    
    private static void searchByStatus() {
        ConsoleUI.printSubHeader("Search by Status");
        
        ConsoleUI.printInfo("Available Statuses:");
        System.out.println("1. LOST");
        System.out.println("2. FOUND");
        System.out.println("3. CLAIMED");
        System.out.println("4. RETURNED");
        
        String status = ConsoleUI.getInput("Enter status").toUpperCase();
        
        List<Item> results = SearchOperations.searchByStatus(status);
        
        if (results.isEmpty()) {
            ConsoleUI.printInfo("No items with status: " + status);
        } else {
            ConsoleUI.printSuccess("Found " + results.size() + " items");
            ItemView.displayItemsTable(results);
        }
        
        ConsoleUI.pause();
    }
    
    private static void advancedSearch() {
        ConsoleUI.printSubHeader("Advanced Search");
        ConsoleUI.printInfo("Leave fields empty to skip that filter");
        
        String keyword = ConsoleUI.getInput("Keyword (optional)");
        String category = ConsoleUI.getInput("Category (optional)");
        String location = ConsoleUI.getInput("Location (optional)");
        String status = ConsoleUI.getInput("Status (optional)");
        
        keyword = keyword.isEmpty() ? null : keyword;
        category = category.isEmpty() ? null : category.toUpperCase();
        location = location.isEmpty() ? null : location;
        status = status.isEmpty() ? null : status.toUpperCase();
        
        List<Item> results = SearchOperations.advancedSearch(keyword, category, location, status);
        
        if (results.isEmpty()) {
            ConsoleUI.printInfo("No items found matching your criteria");
        } else {
            ConsoleUI.printSuccess("Found " + results.size() + " items");
            ItemView.displayItemsTable(results);
        }
        
        ConsoleUI.pause();
    }
    
    private static void viewLostItems() {
        ConsoleUI.printSubHeader("All Lost Items");
        
        List<Item> results = SearchOperations.getLostItems();
        
        if (results.isEmpty()) {
            ConsoleUI.printInfo("No lost items reported");
        } else {
            ConsoleUI.printSuccess("Total lost items: " + results.size());
            ItemView.displayItemsTable(results);
        }
        
        ConsoleUI.pause();
    }
    
    private static void viewFoundItems() {
        ConsoleUI.printSubHeader("All Found Items");
        
        List<Item> results = SearchOperations.getFoundItems();
        
        if (results.isEmpty()) {
            ConsoleUI.printInfo("No found items reported");
        } else {
            ConsoleUI.printSuccess("Total found items: " + results.size());
            ItemView.displayItemsTable(results);
        }
        
        ConsoleUI.pause();
    }
}
