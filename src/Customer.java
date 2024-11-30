/**
 * Represents a regular customer that attempts to purchase tickets from the
 * pool.
 * Each customer runs in its own thread and tries to get tickets at a specified
 * rate until interrupted.
 */
public class Customer implements Runnable {
    private static int customerRetrievalRate;
    private final String name;
    private final TicketPool ticketPool;

    /**
     * Creates a new customer with a specific name and associated ticket pool
     */
    public Customer(String name, TicketPool ticketPool) {
        this.name = name;
        this.ticketPool = ticketPool;
    }

    /**
     * Sets how frequently customers attempt to get tickets (in milliseconds)
     */
    public static void setCustomerRetrievalRate(int rate) {
        customerRetrievalRate = rate;
    }

    /**
     * Gets the customer's name
     */
    protected String getName() {
        return name;
    }

    /**
     * Gets the ticket pool this customer is associated with
     */
    protected TicketPool getTicketPool() {
        return ticketPool;
    }

    /**
     * Gets the current rate at which customers attempt to retrieve tickets
     */
    protected static int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    /**
     * Main customer operation loop that continuously attempts to get tickets.
     * Runs until the thread is interrupted, with delays between attempts
     * to purchase tickets.
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ticketPool.removeTicket(name);
                Thread.sleep(customerRetrievalRate);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}