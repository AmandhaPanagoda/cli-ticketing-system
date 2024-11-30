/**
 * Represents a VIP customer with priority access to tickets.
 * Extends the base Customer class but uses VIP-specific ticket removal
 * method for preferential treatment in ticket purchasing.
 */
public class VIPCustomer extends Customer {
    /**
     * Creates a new VIP customer with a specific name and associated ticket pool
     */
    public VIPCustomer(String name, TicketPool ticketPool) {
        super(name, ticketPool);
    }

    /**
     * Main VIP customer operation loop that continuously attempts to get tickets.
     * Uses VIP-specific ticket removal method for priority access.
     * Runs until the thread is interrupted.
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                getTicketPool().removeVIPTicket(getName());
                Thread.sleep(getCustomerRetrievalRate());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}