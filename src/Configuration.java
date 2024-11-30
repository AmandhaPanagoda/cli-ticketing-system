/**
 * Manages the configuration settings for the ticketing system.
 * Stores and applies parameters such as ticket capacity, release rates,
 * and customer retrieval rates to the system components.
 */
public class Configuration {
    private int totalTickets;
    private int maxTicketCapacity;
    private int ticketReleaseRate;
    private int customerRetrievalRate;

    /**
     * Sets up the configuration parameters for the ticketing system.
     * 
     * @param totalTickets          Initial number of tickets in the system
     * @param maxTicketCapacity     Maximum allowed tickets in the pool
     * @param ticketReleaseRate     How often vendors release tickets (in ms)
     * @param customerRetrievalRate How often customers attempt to get tickets (in
     *                              ms)
     */
    public void configure(int totalTickets, int maxTicketCapacity, int ticketReleaseRate, int customerRetrievalRate) {
        this.totalTickets = totalTickets;
        this.maxTicketCapacity = maxTicketCapacity;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
    }

    /**
     * Applies the stored configuration to the ticket pool and actors.
     * Sets up the ticket pool capacity and initial tickets,
     * and configures the timing for vendors and customers.
     * 
     * @param ticketPool The ticket pool to configure
     */
    public void applyConfiguration(TicketPool ticketPool) {
        ticketPool.setMaxTicketCapacity(maxTicketCapacity);
        ticketPool.addTickets(totalTickets);
        Vendor.setTicketReleaseRate(ticketReleaseRate);
        Customer.setCustomerRetrievalRate(customerRetrievalRate);
    }
}
