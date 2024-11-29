import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TicketPool {
    private int ticketId = 1;
    private final List<Integer> tickets;
    private int maxTicketCapacity;
    private final Object lockObject = new Object();
    private final OutputConsole console = OutputConsole.getInstance();

    public TicketPool() {
        this.tickets = Collections.synchronizedList(new LinkedList<>());
    }

    public synchronized void setMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public synchronized boolean addTickets(int count) {
        if (tickets.size() + count <= maxTicketCapacity) {
            for (int i = 0; i < count; i++) {
                tickets.add(ticketId++);
                console.printSystem(
                        String.format("Ticket #%d added by Admin. Current total: %d", (ticketId - 1), tickets.size()));
            }
            return true;
        }
        return false;
    }

    public synchronized boolean addTickets(int count, String vendorName) {
        if (tickets.size() + count <= maxTicketCapacity) {
            for (int i = 0; i < count; i++) {
                tickets.add(ticketId++);
                console.printVendor(String.format("Ticket #%d added by %s. Current total: %d",
                        (ticketId - 1), vendorName, tickets.size()));
            }
            return true;
        }
        return false;
    }

    public Integer removeVIPTicket(String customerName) {
        synchronized (lockObject) {
            if (!tickets.isEmpty()) {
                Integer ticket = tickets.remove(0);
                console.printVIP(String.format("%s (VIP) : Purchased ticket #%d. Remaining tickets: %d",
                        customerName, ticket, tickets.size()));
                return ticket;
            }
            return null;
        }
    }

    public Integer removeTicket(String customerName) {
        synchronized (lockObject) {
            if (!tickets.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (!tickets.isEmpty()) {
                    Integer ticket = tickets.remove(0);
                    console.printCustomer(String.format("%s : Purchased ticket #%d. Remaining tickets: %d",
                            customerName, ticket, tickets.size()));
                    return ticket;
                }
            }
            return null;
        }
    }

    public synchronized int getTicketCount() {
        return tickets.size();
    }
}