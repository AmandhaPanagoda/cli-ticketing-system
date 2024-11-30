import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * Main CLI application class for the Ticketing System.
 * Manages the simulation of ticket vendors, customers, and VIP customers
 * interacting with a shared ticket pool. Provides a command-line interface
 * for controlling the simulation and monitoring its status.
 */
public class TicketingSystemCLI {
    private final TicketPool ticketPool;
    private final ExecutorService executorService;
    private final Scanner scanner;
    private final Configuration configuration;
    private boolean isRunning;
    private final List<Future<?>> vendorTasks;
    private final List<Future<?>> customerTasks;
    private int vendorCounter;
    private int customerCounter;
    private final List<Future<?>> vipCustomerTasks;
    private int vipCustomerCounter;
    private final OutputConsole outputConsole;

    /**
     * Initializes the ticketing system
     * Sets up the thread pool, scanner, and required data structures
     * for managing vendors and customers. Creates the output console
     * for displaying simulation events.
     */
    public TicketingSystemCLI() {
        this.ticketPool = new TicketPool();
        this.executorService = Executors.newCachedThreadPool(); // Grows/shrinks pool as needed
        this.scanner = new Scanner(System.in);
        this.configuration = new Configuration();
        this.isRunning = false;
        this.vendorTasks = new ArrayList<>();
        this.customerTasks = new ArrayList<>();
        this.vipCustomerTasks = new ArrayList<>();
        this.vendorCounter = 0;
        this.customerCounter = 0;
        this.vipCustomerCounter = 0;
        this.outputConsole = OutputConsole.getInstance();
        this.outputConsole.setVisible(true);
    }

    /**
     * Starts the ticketing system by configuring initial parameters
     * and launching the main simulation loop.
     */
    public void start() {
        configureSystem();
        runSimulation();
    }

    /**
     * Prompts user for system configuration parameters including:
     * - Maximum ticket capacity (the maximum allowed in the pool)
     * - Initial total tickets (the initial number of tickets in the pool)
     * - Ticket release rate (the rate at which vendors release tickets this is in
     * miliseconds)
     * - Customer retrieval rate (the rate at which customers retrieve tickets this
     * is in miliseconds)
     * Validates inputs and applies configuration to the ticket pool.
     */
    private void configureSystem() {
        int boxWidth = 80;
        String title = "SYSTEM CONFIGURATION";

        printBorder(title, boxWidth);
        int maxTicketCapacity = getIntInput("Enter maximum ticket capacity: ");

        int totalTickets = getIntInput("Enter total tickets: ");
        while (totalTickets > maxTicketCapacity) {
            System.out
                    .println("Total tickets cannot exceed max capacity (" + maxTicketCapacity + "). Please try again.");
            totalTickets = getIntInput("Enter total tickets: ");
        }

        int ticketReleaseRate = getIntInput("Enter ticket release rate (ms): ");
        int customerRetrievalRate = getIntInput("Enter customer retrieval rate (ms): ");

        System.out.println("-".repeat(82));

        configuration.configure(totalTickets, maxTicketCapacity, ticketReleaseRate, customerRetrievalRate);
        configuration.applyConfiguration(ticketPool);
    }

    /**
     * Draws a formatted border box with a title for CLI output.
     * 
     * @param title    The text to display in the title
     * @param boxWidth The width of the border box
     */
    private void printBorder(String title, int boxWidth) {
        String horizontalLine = "═";
        String verticalLine = "║";
        String topLeft = "╔";
        String topRight = "╗";
        String bottomLeft = "╚";
        String bottomRight = "╝";

        int padding = (boxWidth - title.length() - 2) / 2;

        System.out.println("\n" + topLeft + horizontalLine.repeat(boxWidth) + topRight);
        System.out.println(verticalLine + " ".repeat(padding) + title + " ".repeat(boxWidth - padding - title.length())
                + verticalLine);
        System.out.println(bottomLeft + horizontalLine.repeat(boxWidth) + bottomRight + "\n");
    }

    /**
     * Main simulation loop that handles user commands.
     * Provides options for:
     * - Starting/stopping simulation
     * - Adding/removing actors
     * - Checking system status
     * - Exiting the application
     */
    private void runSimulation() {
        while (true) {
            int boxWidth = 80;
            String title = "MAIN MENU";

            printBorder(title, boxWidth);
            System.out.println("Enter Command:");
            System.out.println("╔════════════════════════════════════════════════════════╗");
            System.out.println("║  start   - Start the simulation                        ║");
            System.out.println("║  stop    - Stop the simulation                         ║");
            System.out.println("║  status  - Display system status                       ║");
            System.out.println("║  add     - Add vendor, customer or VIP customer        ║");
            System.out.println("║  remove  - Remove vendor, customer or VIP customer     ║");
            System.out.println("║  exit    - Exit the application                        ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            System.out.print("\nCommand > ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "start":
                    startSimulation();
                    break;
                case "stop":
                    stopSimulation();
                    break;
                case "status":
                    printStatus();
                    break;
                case "add":
                    handleAddCommand();
                    break;
                case "remove":
                    handleRemoveCommand();
                    break;
                case "exit":
                    exitSimulation();
                    return;
                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }
    }

    /**
     * Handles the addition of new actors (vendor/customer/VIP) to the simulation.
     * Prompts user for actor type and delegates to specific add methods.
     */
    private void handleAddCommand() {
        int boxWidth = 50;
        String title = "ADD USERS";

        printBorder(title, boxWidth);
        System.out.println("Enter type to add (vendor/customer/vip): ");
        String type = scanner.nextLine().trim().toLowerCase();

        switch (type) {
            case "vendor":
                addVendor();
                break;
            case "customer":
                addCustomer();
                break;
            case "vip":
                addVIPCustomer();
                break;
            default:
                System.out.println("Invalid type. Please enter 'vendor', 'customer', or 'vip'.");
        }
    }

    /**
     * Handles the removal of actors from the simulation.
     * Prompts user for actor type and delegates to specific remove methods.
     */
    private void handleRemoveCommand() {
        int boxWidth = 50;
        String title = "REMOVE USERS";

        printBorder(title, boxWidth);
        System.out.println("Enter type to remove (vendor/customer/vip): ");
        String type = scanner.nextLine().trim().toLowerCase();

        switch (type) {
            case "vendor":
                removeVendor();
                break;
            case "customer":
                removeCustomer();
                break;
            case "vip":
                removeVIPCustomer();
                break;
            default:
                System.out.println("Invalid type. Please enter 'vendor' or 'customer'.");
        }
    }

    /**
     * Adds a new vendor to the simulation if it is running.
     * Assigns unique vendor ID and submits vendor task to executor service.
     */
    private void addVendor() {
        if (isRunning) {
            String vendorName = "Vendor-" + ++vendorCounter;
            Future<?> task = executorService.submit(new Vendor(vendorName, ticketPool));
            vendorTasks.add(task);
            System.out.println("New vendor added. Total vendors: " + vendorTasks.size());
            outputConsole.printSystem(
                    String.format("New Vendor added by Admin. Total Vendors: %d", vendorTasks.size()));
        } else {
            System.out.println("Please start the simulation first.");
        }
    }

    /**
     * Adds a new customer to the simulation if it is running.
     * Assigns unique customer ID and submits customer task to executor service.
     */
    private void addCustomer() {
        if (isRunning) {
            String customerName = "Customer-" + ++customerCounter;
            Future<?> task = executorService.submit(new Customer(customerName, ticketPool));
            customerTasks.add(task);
            System.out.println("New customer added. Total customers: " + customerTasks.size());
            outputConsole.printSystem(
                    String.format("New Customer added by Admin. Total Customers: %d", customerTasks.size()));
        } else {
            System.out.println("Please start the simulation first.");
        }
    }

    /**
     * Adds a new VIP customer to the simulation if it is running.
     * Assigns unique VIP ID and submits VIP customer task to executor service.
     */
    private void addVIPCustomer() {
        if (isRunning) {
            String vipCustomerName = "VIP-" + ++vipCustomerCounter;
            Future<?> task = executorService.submit(new VIPCustomer(vipCustomerName, ticketPool));
            vipCustomerTasks.add(task);
            System.out.println("New VIP customer added. Total VIP customers: " + vipCustomerTasks.size());
            outputConsole.printSystem(
                    String.format("New VIP Customer added by Admin. Total VIP Customers: %d", vipCustomerTasks.size()));
        } else {
            System.out.println("Please start the simulation first.");
        }
    }

    /**
     * Removes the most recently added vendor from the simulation.
     * Cancels the vendor's task and updates vendor count.
     */
    private void removeVendor() {
        if (!vendorTasks.isEmpty()) {
            Future<?> task = vendorTasks.removeLast();
            task.cancel(true);
            System.out.println("Vendor removed. Remaining vendors: " + vendorTasks.size());
            outputConsole.printSystem(
                    String.format("Last Vendor removed by Admin. Remaining Vendors: %d", vendorTasks.size()));

        } else {
            System.out.println("No vendors to remove.");
        }
    }

    /**
     * Removes the most recently added customer from the simulation.
     * Cancels the customer's task and updates customer count.
     */
    private void removeCustomer() {
        if (!customerTasks.isEmpty()) {
            Future<?> task = customerTasks.removeLast();
            task.cancel(true);
            System.out.println("Customer removed. Remaining customers: " + customerTasks.size());
            outputConsole.printSystem(
                    String.format("Last Customer removed by Admin. Remaining Customers: %d", customerTasks.size()));
        } else {
            System.out.println("No customers to remove.");
        }
    }

    /**
     * Removes the most recently added VIP customer from the simulation.
     * Cancels the VIP customer's task and updates VIP count.
     */
    private void removeVIPCustomer() {
        if (!vipCustomerTasks.isEmpty()) {
            Future<?> task = vipCustomerTasks.removeLast();
            task.cancel(true);
            System.out.println("VIP customer removed. Remaining VIP customers: " + vipCustomerTasks.size());
            outputConsole.printSystem(
                    String.format("Last VIP Customer removed by Admin. Remaining VIP Customers: %d", vipCustomerTasks.size()));
        } else {
            System.out.println("No VIP customers to remove.");
        }
    }

    /**
     * Initializes and starts the simulation with user-specified numbers
     * of vendors, customers, and VIP customers. Resets all counters and
     * clears existing tasks before starting.
     */
    private void startSimulation() {
        if (!isRunning) {
            isRunning = true;
            vendorTasks.clear();
            customerTasks.clear();
            vipCustomerTasks.clear();
            vendorCounter = 0;
            customerCounter = 0;
            vipCustomerCounter = 0;

            int boxWidth = 50;
            String title = "SET UP USERS";

            printBorder(title, boxWidth);

            int vendorCount = getIntInput("Enter number of vendors: ");
            int customerCount = getIntInput("Enter number of customers: ");
            int vipCustomerCount = getIntInput("Enter number of VIP customers: ");
            System.out.println("\n------------------------- SIMULATION STARTED -------------------------\n");
            outputConsole.clear(); // clear the output console before the new session
            for (int i = 0; i < vendorCount; i++) {
                addVendor();
            }
            for (int i = 0; i < customerCount; i++) {
                addCustomer();
            }
            for (int i = 0; i < vipCustomerCount; i++) {
                addVIPCustomer();
            }
        } else {
            System.out.println("Simulation is already running.");
        }
    }

    /**
     * Stops the simulation by cancelling all running tasks and
     * shutting down the executor service. Clears all task lists
     * and resets the running state.
     */
    private void stopSimulation() {
        if (isRunning) {
            isRunning = false;
            for (Future<?> task : vendorTasks) {
                task.cancel(true);
            }
            for (Future<?> task : customerTasks) {
                task.cancel(true);
            }
            for (Future<?> task : vipCustomerTasks) {
                task.cancel(true);
            }
            vendorTasks.clear();
            customerTasks.clear();
            vipCustomerTasks.clear();
            executorService.shutdownNow();
            try {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Simulation stopped.");
        } else {
            System.out.println("Simulation is not running.");
        }
    }

    /**
     * Displays current system status including:
     * - Current ticket count
     * - Number of active vendors
     * - Number of active customers
     * - Number of active VIP customers
     * - System running state
     */
    private void printStatus() {
        int boxWidth = 80;
        String title = "SYSTEM STATUS";

        printBorder(title, boxWidth);
        System.out.println("Current ticket count: " + ticketPool.getTicketCount());
        System.out.println("Active vendors: " + vendorTasks.size());
        System.out.println("Active customers: " + customerTasks.size());
        System.out.println("Active VIP customers: " + vipCustomerTasks.size());
        System.out.println("System is " + (isRunning ? "RUNNING" : "STOPPED"));
        System.out.println("-".repeat(82));
    }

    /**
     * Safely exits the simulation by stopping all processes,
     * closing resources, and terminating the application.
     */
    private void exitSimulation() {
        if (isRunning) {
            stopSimulation();
        }
        System.out.println("\nExiting simulation....\n");
        scanner.close();
        outputConsole.clear();
        outputConsole.dispose();
        System.exit(0);
    }

    /**
     * Utility method to get validated integer input from user.
     * Ensures input is a positive number.
     * 
     * @param prompt The message to display when requesting input
     * @return Valid positive integer entered by user
     */
    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                }
                System.out.println("Please enter a number over 0.\n");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.\n");
            }
        }
    }

    /**
     * Application entry point. Initializes the GUI output console
     * and starts the ticketing system CLI.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize the GUI windows
        OutputConsole outputConsole = OutputConsole.getInstance();
        outputConsole.setVisible(true);

        TicketingSystemCLI cli = new TicketingSystemCLI();
        cli.start();
    }
}
