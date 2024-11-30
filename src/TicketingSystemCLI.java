import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;

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

    public void start() {
        configureSystem();
        runSimulation();
    }

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
            System.out.println("║  add     - Add vendor/customer or VIP customer         ║");
            System.out.println("║  remove  - Remove vendor/customer or VIP customer      ║");
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

    private void handleAddCommand() {
        int boxWidth = 60;
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

    private void handleRemoveCommand() {
        int boxWidth = 60;
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

    private void addVendor() {
        if (isRunning) {
            Future<?> task = executorService.submit(new Vendor("Vendor-" + vendorCounter++, ticketPool));
            vendorTasks.add(task);
            System.out.println("New vendor added. Total vendors: " + vendorTasks.size());
        } else {
            System.out.println("Please start the simulation first.");
        }
    }

    private void addCustomer() {
        if (isRunning) {
            Future<?> task = executorService.submit(new Customer("Customer-" + customerCounter++, ticketPool));
            customerTasks.add(task);
            System.out.println("New customer added. Total customers: " + customerTasks.size());
        } else {
            System.out.println("Please start the simulation first.");
        }
    }

    private void addVIPCustomer() {
        if (isRunning) {
            Future<?> task = executorService.submit(new VIPCustomer("VIP-" + vipCustomerCounter++, ticketPool));
            vipCustomerTasks.add(task);
            System.out.println("New VIP customer added. Total VIP customers: " + vipCustomerTasks.size());
        } else {
            System.out.println("Please start the simulation first.");
        }
    }

    private void removeVendor() {
        if (!vendorTasks.isEmpty()) {
            Future<?> task = vendorTasks.remove(vendorTasks.size() - 1);
            task.cancel(true);
            System.out.println("Vendor removed. Remaining vendors: " + vendorTasks.size());
        } else {
            System.out.println("No vendors to remove.");
        }
    }

    private void removeCustomer() {
        if (!customerTasks.isEmpty()) {
            Future<?> task = customerTasks.remove(customerTasks.size() - 1);
            task.cancel(true);
            System.out.println("Customer removed. Remaining customers: " + customerTasks.size());
        } else {
            System.out.println("No customers to remove.");
        }
    }

    private void removeVIPCustomer() {
        if (!vipCustomerTasks.isEmpty()) {
            Future<?> task = vipCustomerTasks.remove(vipCustomerTasks.size() - 1);
            task.cancel(true);
            System.out.println("VIP customer removed. Remaining VIP customers: " + vipCustomerTasks.size());
        } else {
            System.out.println("No VIP customers to remove.");
        }
    }

    private void startSimulation() {
        if (!isRunning) {
            isRunning = true;
            vendorTasks.clear();
            customerTasks.clear();
            vipCustomerTasks.clear();
            vendorCounter = 0;
            customerCounter = 0;
            vipCustomerCounter = 0;

            int boxWidth = 80;
            String title = "SET UP USERS";

            printBorder(title, boxWidth);

            int vendorCount = getIntInput("Enter number of vendors: ");
            int customerCount = getIntInput("Enter number of customers: ");
            int vipCustomerCount = getIntInput("Enter number of VIP customers: ");
            System.out.println("\n------------------------- SIMULATION STARTED -------------------------\n");

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

    private void printStatus() {
        int boxWidth = 80;
        String title = "SYSTEM STATUS";

        printBorder(title, boxWidth);
        System.out.println("Current ticket count: " + ticketPool.getTicketCount());
        System.out.println("Active vendors: " + vendorTasks.size());
        System.out.println("Active customers: " + customerTasks.size());
        System.out.println("Active VIP customers: " + vipCustomerTasks.size());
        System.out.println("System is " + (isRunning ? "running" : "stopped"));
        System.out.println("-".repeat(82));
    }

    private void exitSimulation() {
        if (isRunning) {
            stopSimulation();
        }
        System.out.println("\nExiting simulation....\n");
        scanner.close();
        outputConsole.dispose();
        System.exit(0);
    }

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                }
                System.out.println("Please enter a positive integer.\n");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.\n");
            }
        }
    }

    public static void main(String[] args) {
        // Initialize the GUI windows
        OutputConsole outputConsole = OutputConsole.getInstance();
        outputConsole.setVisible(true);

        TicketingSystemCLI cli = new TicketingSystemCLI();
        cli.start();
    }
}
