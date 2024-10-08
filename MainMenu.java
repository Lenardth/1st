import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainMenu extends JFrame {
    private double walletBalance;
    private JLabel clockLabel;
    private ArrayList<Taxi> taxis;
    private DefaultListModel<String> notificationsModel;

    public MainMenu(double initialBalance) {
        this.walletBalance = initialBalance;
        taxis = new ArrayList<>();
        initializeTaxis();

        setTitle("Taxi Service Dashboard");
        setSize(1080, 1040);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(new Color(52, 73, 94));

        Color buttonColor = new Color(70, 130, 180);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Book Taxi Button
        RoundedButton bookTaxiButton = createMenuButton("Book a Taxi", buttonColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        menuPanel.add(bookTaxiButton, gbc);

        // Nearby Taxi Button
        RoundedButton nearbyTaxiButton = createMenuButton("See Nearby Taxis", buttonColor);
        gbc.gridx = 1;
        menuPanel.add(nearbyTaxiButton, gbc);

        // Taxi Routes Button
        RoundedButton taxiRoutesButton = createMenuButton("View Taxi Routes", buttonColor);
        gbc.gridx = 2;
        menuPanel.add(taxiRoutesButton, gbc);

        // Taxi Schedule Button
        RoundedButton taxiScheduleButton = createMenuButton("Taxi Schedule Overview", buttonColor);
        gbc.gridx = 3;
        menuPanel.add(taxiScheduleButton, gbc);

        // Clock
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.BOLD, 24));
        clockLabel.setForeground(Color.WHITE);
        gbc.gridx = 4;
        menuPanel.add(clockLabel, gbc);

        initClock();

        JPanel walletPanel = new JPanel(new GridBagLayout());
        walletPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel walletLabel = new JLabel("Wallet Balance: R" + walletBalance, SwingConstants.CENTER);
        walletLabel.setFont(new Font("Arial", Font.BOLD, 36));

        GridBagConstraints walletGbc = new GridBagConstraints();
        walletGbc.fill = GridBagConstraints.HORIZONTAL;
        walletGbc.insets = new Insets(10, 10, 10, 10);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        JLabel initialPointLabel = new JLabel("Initial Point:");
        initialPointLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        JTextField initialPointField = new JTextField();
        JLabel finalDestinationLabel = new JLabel("Final Destination:");
        finalDestinationLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        JTextField finalDestinationField = new JTextField();

        inputPanel.add(initialPointLabel);
        inputPanel.add(initialPointField);
        inputPanel.add(finalDestinationLabel);
        inputPanel.add(finalDestinationField);

        walletGbc.gridx = 0;
        walletGbc.gridy = 0;
        walletPanel.add(walletLabel, walletGbc);

        walletGbc.gridy = 1;
        walletPanel.add(inputPanel, walletGbc);

        // Add money button
        RoundedButton addMoneyButton = createActionButton("Add Money", buttonColor);
        addMoneyButton.addActionListener(e -> {
            String amountStr = JOptionPane.showInputDialog(this, "Enter amount to add:");
            if (amountStr == null || amountStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr.trim());
                walletBalance += amount;
                walletLabel.setText("Wallet Balance: R" + walletBalance);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.");
            }
        });

        // Withdraw money button
        RoundedButton withdrawMoneyButton = createActionButton("Withdraw Money", buttonColor);
        withdrawMoneyButton.addActionListener(e -> {
            String amountStr = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
            if (amountStr == null || amountStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr.trim());
                if (amount > walletBalance) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.");
                } else {
                    walletBalance -= amount;
                    walletLabel.setText("Wallet Balance: R" + walletBalance);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.");
            }
        });

        JPanel walletButtonPanel = new JPanel(new FlowLayout());
        walletButtonPanel.add(addMoneyButton);
        walletButtonPanel.add(withdrawMoneyButton);
        
        walletGbc.gridy = 2;
        walletPanel.add(walletButtonPanel, walletGbc);

        notificationsModel = new DefaultListModel<>();
        addSampleNewsNotifications();
        JPanel notificationPanel = createModernNotificationPanel();

        RoundedButton themeSwitcher = createActionButton("Switch Theme", new Color(155, 89, 182));
        themeSwitcher.addActionListener(e -> switchTheme());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(themeSwitcher);

        add(menuPanel, BorderLayout.NORTH);
        add(walletPanel, BorderLayout.CENTER);
        add(notificationPanel, BorderLayout.EAST);  // Modern notification panel
        add(bottomPanel, BorderLayout.SOUTH);

        // Book Taxi button action
        bookTaxiButton.addActionListener(e -> {
            String initialPoint = initialPointField.getText();
            String finalDestination = finalDestinationField.getText();
            double taxiFare = 50.0;

            if (initialPoint.isEmpty() || finalDestination.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both the initial point and the final destination.");
            } else if (walletBalance < taxiFare) {
                JOptionPane.showMessageDialog(this, "Insufficient balance for taxi fare.");
            } else {
                Taxi availableTaxi = findAvailableTaxi();
                if (availableTaxi != null && availableTaxi.isAvailableAtCurrentTime()) {
                    walletBalance -= taxiFare;
                    walletLabel.setText("Wallet Balance: R" + walletBalance);
                    String notification = "Taxi Booked: " + availableTaxi.getTaxiNumber() + " from " +
                            initialPoint + " to " + finalDestination;
                    notificationsModel.addElement(notification);
                    JOptionPane.showMessageDialog(this, "Taxi booked from " + initialPoint + " to " + finalDestination +
                            ". Taxi Number: " + availableTaxi.getTaxiNumber());
                } else {
                    JOptionPane.showMessageDialog(this, "No taxis are available at the current time. " +
                            "Taxis operate between 5:00 AM and 11:00 PM. Please try again later.");
                }
            }
        });

        nearbyTaxiButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Showing nearby taxis..."));
        taxiRoutesButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Displaying taxi routes..."));

        setVisible(true);
    }

    private JPanel createModernNotificationPanel() {
        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setBorder(BorderFactory.createTitledBorder("Notifications"));
        notificationPanel.setPreferredSize(new Dimension(250, 150)); // Adjusted size

        // List for notifications
        JList<String> notificationsList = new JList<>(notificationsModel);
        notificationsList.setFont(new Font("Arial", Font.PLAIN, 14));
        notificationsList.setVisibleRowCount(5);
        notificationsList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Scroll pane for notifications with modern styling
        JScrollPane notificationsScrollPane = new JScrollPane(notificationsList);
        notificationsScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        notificationPanel.add(notificationsScrollPane, BorderLayout.CENTER);

        return notificationPanel;
    }

    private void initClock() {
        javax.swing.Timer clockTimer = new javax.swing.Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            clockLabel.setText("Current Time: " + sdf.format(new Date()));
        });
        clockTimer.start();
    }

    private void initializeTaxis() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("taxis.ser"))) {
            taxis = (ArrayList<Taxi>) ois.readObject();
            System.out.println("Successfully loaded " + taxis.size() + " taxis from taxis.ser");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load taxi data. Please ensure the data is available.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Taxi findAvailableTaxi() {
        for (Taxi taxi : taxis) {
            if (taxi.isAvailableAtCurrentTime()) {
                return taxi;
            }
        }
        return null;
    }

    private void addSampleNewsNotifications() {
        notificationsModel.addElement("Welcome to the Taxi Service App!");
        notificationsModel.addElement("Service will be unavailable on Sunday for maintenance.");
        notificationsModel.addElement("New routes added between Johannesburg and Pretoria.");
    }

    private RoundedButton createMenuButton(String text, Color color) {
        RoundedButton button = new RoundedButton(text);
        button.setBackground(color);
        button.setPreferredSize(new Dimension(200, 40));  // Adjusted button size
        return button;
    }

    private RoundedButton createActionButton(String text, Color color) {
        RoundedButton button = new RoundedButton(text);
        button.setBackground(color);
        button.setPreferredSize(new Dimension(200, 40));  // Adjusted button size
        return button;
    }

    private void switchTheme() {
        UIManager.put("Panel.background", Color.DARK_GRAY);
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("Button.background", Color.LIGHT_GRAY);
        UIManager.put("Button.foreground", Color.BLACK);
        SwingUtilities.updateComponentTreeUI(this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu(100.0));
    }
}
