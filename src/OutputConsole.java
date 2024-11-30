import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OutputConsole extends JFrame {
    private final JTextPane textPane;
    private static OutputConsole instance;

    private static final Color VENDOR_COLOR = new Color(46, 204, 113); // Green
    private static final Color CUSTOMER_COLOR = new Color(52, 152, 219); // Blue
    private static final Color VIP_COLOR = new Color(155, 89, 182); // Purple
    private static final Color SYSTEM_COLOR = new Color(149, 165, 166); // Gray

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

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

    public static OutputConsole getInstance() {
        if (instance == null) {
            instance = new OutputConsole();
        }
        return instance;
    }

    public void printVendor(String message) {
        printColored(message, VENDOR_COLOR);
    }

    public void printCustomer(String message) {
        printColored(message, CUSTOMER_COLOR);
    }

    public void printVIP(String message) {
        printColored(message, VIP_COLOR);
    }

    public void printSystem(String message) {
        printColored(message, SYSTEM_COLOR);
    }

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

    public void clear() {
        SwingUtilities.invokeLater(() -> textPane.setText(""));
    }
}