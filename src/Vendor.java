/**
 * Represents a ticket vendor that continuously adds tickets to the ticket pool.
 * Each vendor runs in its own thread and adds tickets at a specified rate
 * until interrupted.
 */
public class Vendor implements Runnable {
    private static int ticketReleaseRate;
    private final String name;
    private final TicketPool ticketPool;

    /**
     * Creates a new vendor with a specific name and associated ticket pool
     */
    public Vendor(String name, TicketPool ticketPool) {
        this.name = name;
        this.ticketPool = ticketPool;
    }

    /**
     * Sets how frequently vendors add tickets to the pool (in milliseconds)
     */
    public static void setTicketReleaseRate(int rate) {
        ticketReleaseRate = rate;
    }

    /**
     * Main vendor operation loop that continuously adds tickets to the pool.
     * Runs until the thread is interrupted, adding one ticket at a time
     * with delays between additions.
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ticketPool.addTickets(1, name);
                Thread.sleep(ticketReleaseRate);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}