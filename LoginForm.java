import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;

public class LoginForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private HashMap<String, String> users;

    public LoginForm() {
        setTitle("Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel backgroundPanel = new GradientPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        users = loadUsers();

        // Title
        JLabel titleLabel = new JLabel("Your Innovative Transport System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        backgroundPanel.add(titleLabel, gbc);

        // Email Field
        JLabel emailLabel = new JLabel("Enter your email:");
        emailLabel.setForeground(Color.WHITE);
        emailField = new JTextField(15);
        emailField.setBackground(new Color(200, 200, 200));
        emailField.setForeground(Color.BLACK);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        backgroundPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        backgroundPanel.add(emailField, gbc);

        // Password Field
        JLabel passwordLabel = new JLabel("Enter your password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordField = new JPasswordField(15);
        passwordField.setBackground(new Color(200, 200, 200));
        passwordField.setForeground(Color.BLACK);

        gbc.gridx = 0;
        gbc.gridy = 2;
        backgroundPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        backgroundPanel.add(passwordField, gbc);

        // Remember Me Checkbox
        JCheckBox rememberMe = new JCheckBox("Remember me");
        rememberMe.setForeground(Color.WHITE);
        rememberMe.setOpaque(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        backgroundPanel.add(rememberMe, gbc);

        // Login Button
        RoundedButton loginButton = new RoundedButton("Login Now");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setToolTipText("Click to log in");

        gbc.gridx = 1;
        gbc.gridy = 4;
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });
        backgroundPanel.add(loginButton, gbc);

        // Sign Up Button
        RoundedButton signUpButton = new RoundedButton("Sign Up");
        signUpButton.setBackground(new Color(60, 179, 113));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setToolTipText("Click to create a new account");

        gbc.gridy = 5;
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegistrationForm();
            }
        });
        backgroundPanel.add(signUpButton, gbc);

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void loginUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (users.containsKey(email) && users.get(email).equals(password)) {
            showWelcomeSplash();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials.");
        }
    }

    // Method to open the welcome splash screen
    private void showWelcomeSplash() {
        JFrame splashFrame = new JFrame();
        splashFrame.setSize(400, 200);
        splashFrame.setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome to Innovative Transport System!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        splashFrame.add(welcomeLabel);

        splashFrame.setVisible(true);

        // Close splash screen after 3 seconds and open the main menu
        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                splashFrame.dispose();
                new MainMenu(0.0);  // Open main menu with initial wallet balance
                dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Method to open registration form
    private void openRegistrationForm() {
        new RegistrationForm();
        this.dispose();
    }

    private HashMap<String, String> loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.jfile"))) {
            return (HashMap<String, String>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // Add the main method here
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm());
    }
}
