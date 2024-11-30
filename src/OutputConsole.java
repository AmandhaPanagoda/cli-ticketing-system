import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Provides a graphical console window for displaying simulation output.
 * Uses different colors for different types of messages (vendor, customer, VIP,
 * system)
 * and implements the Singleton pattern for system-wide access.
 */
public class OutputConsole extends JFrame {
    private final JTextPane textPane;
    private static OutputConsole instance;

    private static final Color VENDOR_COLOR = new Color(46, 204, 113); // Green
    private static final Color CUSTOMER_COLOR = new Color(52, 152, 219); // Blue
    private static final Color VIP_COLOR = new Color(154, 85, 184); // Purple
    private static final Color SYSTEM_COLOR = new Color(149, 165, 166); // Gray

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    /**
     * Private constructor for Singleton pattern.
     * Sets up the GUI window with a styled text pane for output.
     */
    private OutputConsole() {
        setTitle("Simulation Output");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        textPane.setBackground(new Color(30, 30, 30)); // Dark background

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane);
        setLocationRelativeTo(null);
    }

    /**
     * Returns the single instance of the output console.
     * Creates the instance if it doesn't exist (Singleton pattern).
     */
    public static OutputConsole getInstance() {
        if (instance == null) {
            instance = new OutputConsole();
        }
        return instance;
    }

    /**
     * Prints a vendor message in specified color
     */
    public void printVendor(String message) {
        printColored(message, VENDOR_COLOR);
    }

    /**
     * Prints a customer message in specified color
     */
    public void printCustomer(String message) {
        printColored(message, CUSTOMER_COLOR);
    }

    /**
     * Prints a VIP customer message in specified color
     */
    public void printVIP(String message) {
        printColored(message, VIP_COLOR);
    }

    /**
     * Prints a system message in specified color
     */
    public void printSystem(String message) {
        printColored(message, SYSTEM_COLOR);
    }

    /**
     * Helper method to print colored messages with timestamps.
     */
    private void printColored(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = textPane.getStyledDocument();
            Style style = textPane.addStyle("Color Style", null);
            StyleConstants.setForeground(style, color);

            try {
                String timestamp = LocalDateTime.now().format(timeFormatter);
                String timestampedMessage = String.format("[%s] %s", timestamp, message);

                doc.insertString(doc.getLength(), timestampedMessage + "\n", style);
                textPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Clears all text from the console window
     */
    public void clear() {
        SwingUtilities.invokeLater(() -> textPane.setText(""));
    }
}