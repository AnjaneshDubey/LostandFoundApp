package frontend;

import backend.Item;
import backend.ItemOperations;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ItemView {
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public static void reportLostItem(int userId) {
        ConsoleUI.printHeader("REPORT LOST ITEM");
        
        String itemName = ConsoleUI.getInput("Item Name");
        String description = ConsoleUI.getInput("Description");
        
        ConsoleUI.printInfo("Categories: ELECTRONICS, BOOKS, PERSONAL, DOCUMENTS, ACCESSORIES, OTHER");
        String category = ConsoleUI.getInput("Category").toUpperCase();
        
        String location = ConsoleUI.getInput("Location (e.g., Library 2nd Floor)");
        
        Date dateLost = getDateInput("Date Lost (YYYY-MM-DD)");
        if (dateLost == null) {
            ConsoleUI.printError("Invalid date format!");
            ConsoleUI.pause();
            return;
        }
        
        Item item = ItemOperations.reportLostItem(userId, itemName, description, category, location, dateLost);
        
        if (item != null) {
            ConsoleUI.printSuccess("Lost item reported successfully!");
            ConsoleUI.printInfo("Tracking Number: " + item.getTrackingNumber());
        }
        
        ConsoleUI.pause();
    }
    
    public static void reportFoundItem(int userId) {
        ConsoleUI.printHeader("REPORT FOUND ITEM");
        
        String itemName = ConsoleUI.getInput("Item Name");
        String description = ConsoleUI.getInput("Description");
        
        ConsoleUI.printInfo("Categories: ELECTRONICS, BOOKS, PERSONAL, DOCUMENTS, ACCESSORIES, OTHER");
        String category = ConsoleUI.getInput("Category").toUpperCase();
        
        String location = ConsoleUI.getInput("Location Found (e.g., Cafeteria)");
        
        Date dateFound = getDateInput("Date Found (YYYY-MM-DD)");
        if (dateFound == null) {
            ConsoleUI.printError("Invalid date format!");
            ConsoleUI.pause();
            return;
        }
        
        Item item = ItemOperations.reportFoundItem(userId, itemName, description, category, location, dateFound);
        
        if (item != null) {
            ConsoleUI.printSuccess("Found item reported successfully!");
            ConsoleUI.printInfo("Tracking Number: " + item.getTrackingNumber());
        }
        
        ConsoleUI.pause();
    }
    
    public static void viewMyItems(int userId) {
        ConsoleUI.printHeader("MY ITEMS");
        
        List<Item> items = ItemOperations.getItemsByUser(userId);
        
        if (items.isEmpty()) {
            ConsoleUI.printInfo("You haven't reported any items yet.");
        } else {
            displayItemsTable(items);
        }
        
        ConsoleUI.pause();
    }
    
    public static void viewAllItems() {
        ConsoleUI.printHeader("ALL ITEMS");
        
        List<Item> items = ItemOperations.getAllItems();
        
        if (items.isEmpty()) {
            ConsoleUI.printInfo("No items in the system.");
        } else {
            displayItemsTable(items);
        }
        
        ConsoleUI.pause();
    }
    
    public static void updateItemStatus(int userId, boolean isAdmin) {
        ConsoleUI.printHeader("UPDATE ITEM STATUS");
        
        String trackingNumber = ConsoleUI.getInput("Enter Tracking Number");
        
        Item item = ItemOperations.getItemByTrackingNumber(trackingNumber);
        
        if (item == null) {
            ConsoleUI.printError("Item not found!");
            ConsoleUI.pause();
            return;
        }
        
        if (!isAdmin && item.getUserId() != userId) {
            ConsoleUI.printError("You can only update your own items!");
            ConsoleUI.pause();
            return;
        }
        
        ConsoleUI.printInfo("Current Status: " + item.getStatus());
        ConsoleUI.printInfo("Available Statuses: LOST, FOUND, CLAIMED, RETURNED");
        
        String newStatus = ConsoleUI.getInput("New Status").toUpperCase();
        
        if (ConsoleUI.confirm("Update status to " + newStatus + "?")) {
            if (ItemOperations.updateItemStatus(item.getItemId(), newStatus, userId)) {
                ConsoleUI.printSuccess("Status updated successfully!");
            }
        }
        
        ConsoleUI.pause();
    }
    
    public static void displayItemsTable(List<Item> items) {
        String[] headers = {"ID", "Tracking #", "Name", "Category", "Location", "Status", "Date", "Owner"};
        String[][] data = new String[items.size()][8];
        
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            data[i][0] = String.valueOf(item.getItemId());
            data[i][1] = item.getTrackingNumber();
            data[i][2] = truncate(item.getItemName(), 20);
            data[i][3] = item.getCategory();
            data[i][4] = truncate(item.getLocation(), 15);
            data[i][5] = item.getStatus();
            data[i][6] = item.getFormattedDate();
            data[i][7] = item.getOwnerUsername();
        }
        
        ConsoleUI.showTable(headers, data);
        ConsoleUI.printInfo("Total Items: " + items.size());
    }
    
    public static void viewItemDetails() {
        ConsoleUI.printHeader("VIEW ITEM DETAILS");
        
        String trackingNumber = ConsoleUI.getInput("Enter Tracking Number");
        
        Item item = ItemOperations.getItemByTrackingNumber(trackingNumber);
        
        if (item == null) {
            ConsoleUI.printError("Item not found!");
            ConsoleUI.pause();
            return;
        }
        
        ConsoleUI.printDivider();
        System.out.println("Tracking Number : " + item.getTrackingNumber());
        System.out.println("Item Name       : " + item.getItemName());
        System.out.println("Description     : " + item.getDescription());
        System.out.println("Category        : " + item.getCategory());
        System.out.println("Location        : " + item.getLocation());
        System.out.println("Status          : " + item.getStatus());
        System.out.println("Date            : " + item.getFormattedDate());
        System.out.println("Reported By     : " + item.getOwnerUsername());
        ConsoleUI.printDivider();
        
        ConsoleUI.pause();
    }
    
    private static Date getDateInput(String prompt) {
        String dateStr = ConsoleUI.getInput(prompt);
        try {
            dateFormat.setLenient(false);
            java.util.Date utilDate = dateFormat.parse(dateStr);
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            return null;
        }
    }
    
    private static String truncate(String str, int maxLength) {
        if (str == null) return "N/A";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}
