package frontend;

import java.util.Scanner;

public class ConsoleUI {
    
    private static Scanner scanner = new Scanner(System.in);
    
    public static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
    
    public static void printSubHeader(String subtitle) {
        System.out.println("\n--- " + subtitle + " ---");
    }
    
    public static void printSuccess(String message) {
        System.out.println("✅ " + message);
    }
    
    public static void printError(String message) {
        System.out.println("❌ " + message);
    }
    
    public static void printInfo(String message) {
        System.out.println("ℹ️  " + message);
    }
    
    public static void printWarning(String message) {
        System.out.println("⚠️  " + message);
    }
    
    public static void printDivider() {
        System.out.println("-".repeat(60));
    }
    
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    public static String getInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }
    
    public static String getInputWithDefault(String prompt, String defaultValue) {
        System.out.print(prompt + " [" + defaultValue + "]: ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
    
    public static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Invalid number! Please enter a valid integer.");
            }
        }
    }
    
    public static int getIntInputInRange(String prompt, int min, int max) {
        while (true) {
            int value = getIntInput(prompt);
            if (value >= min && value <= max) {
                return value;
            } else {
                printError("Please enter a number between " + min + " and " + max);
            }
        }
    }
    
    public static String getPassword(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }
    
    public static boolean confirm(String message) {
        System.out.print(message + " (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }
    
    public static void showMenu(String[] options) {
        printDivider();
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        printDivider();
    }
    
    public static int getMenuChoice(String[] options) {
        showMenu(options);
        return getIntInputInRange("Enter your choice", 1, options.length);
    }
    
    public static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    public static void showTable(String[] headers, String[][] data) {
        int[] columnWidths = new int[headers.length];
        
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();
        }
        
        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                if (row[i] != null && row[i].length() > columnWidths[i]) {
                    columnWidths[i] = row[i].length();
                }
            }
        }
        
        printDivider();
        for (int i = 0; i < headers.length; i++) {
            System.out.print(String.format("%-" + (columnWidths[i] + 2) + "s", headers[i]));
        }
        System.out.println();
        printDivider();
        
        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                String value = (row[i] != null) ? row[i] : "N/A";
                System.out.print(String.format("%-" + (columnWidths[i] + 2) + "s", value));
            }
            System.out.println();
        }
        printDivider();
    }
    
    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
