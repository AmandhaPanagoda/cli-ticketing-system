import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages a thread-safe pool of tickets that can be added by vendors
 * and purchased by customers. Controls ticket distribution and maintains
 * capacity limits.
 */
public class TicketPool {
    private int ticketId = 1;
    private final List<Integer> tickets;
    private int maxTicketCapacity;
    private final Object lockObject = new Object();
    private final OutputConsole console = OutputConsole.getInstance();

    /**
     * Creates a new empty ticket pool with synchronized access
     */
    public TicketPool() {
        this.tickets = Collections.synchronizedList(new LinkedList<>());
    }

    /**
     * Updates the maximum number of tickets the pool can hold
     */
    public synchronized void setMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }

    /**
     * Adds tickets to the pool through administrative action
     * Returns false if adding would exceed capacity
     */
    public synchronized void addTickets(int count) {
        if (tickets.size() + count <= maxTicketCapacity) {
            for (int i = 0; i < count; i++) {
                tickets.add(ticketId++);
                console.printSystem(
                        String.format("Ticket #%d added by Admin. Current total: %d", (ticketId - 1), tickets.size()));
            }
        }
    }

    /**
     * Adds tickets to the pool through a specific vendor
     * Returns false if adding would exceed capacity
     */
    public synchronized void addTickets(int count, String vendorName) {
        if (tickets.size() + count <= maxTicketCapacity) {
            for (int i = 0; i < count; i++) {
                tickets.add(ticketId++);
                console.printVendor(String.format("%s : Added ticket #%d. Current total: %d",
                        vendorName, (ticketId - 1), tickets.size()));
            }
        }
    }

    /**
     * Removes a ticket for a VIP customer with priority access
     * Returns null if no tickets are available
     */
    public void removeVIPTicket(String customerName) {
        synchronized (lockObject) {
            if (!tickets.isEmpty()) {
                Integer ticket = tickets.removeFirst();
                console.printVIP(String.format("%s (VIP) : Purchased ticket #%d. Remaining tickets: %d",
                        customerName, ticket, tickets.size()));
            }
        }
    }

    /**
     * Removes a ticket for a regular customer
     * Includes a small delay and returns null if no tickets are available
     */
    public void removeTicket(String customerName) {
        synchronized (lockObject) {
            if (!tickets.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (!tickets.isEmpty()) {
                    Integer ticket = tickets.removeFirst();
                    console.printCustomer(String.format("%s : Purchased ticket #%d. Remaining tickets: %d",
                            customerName, ticket, tickets.size()));
                }
            }
        }
    }

    /**
     * Returns the current number of tickets in the pool
     */
    public synchronized int getTicketCount() {
        return tickets.size();
    }
}