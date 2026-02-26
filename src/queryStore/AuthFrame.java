package querystore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserManager userManager;

    public AuthFrame() {
        setTitle("Login / Sign Up");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Initialize UserManager with a stable storage path (User Home)
        userManager = new UserManager(QueryStore.getBaseStoragePath()); 

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        Font uiFont = new Font("SansSerif", Font.PLAIN, 16);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(uiFont);
        panel.add(usernameLabel);
        usernameField = new JTextField();
        usernameField.setFont(uiFont);
        usernameField.putClientProperty("JTextField.placeholderText", "Enter your username");
        usernameField.addActionListener(e -> attemptLogin()); // Add Enter key support
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(uiFont);
        panel.add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setFont(uiFont);
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password");
        passwordField.addActionListener(e -> attemptLogin()); // Add Enter key support
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(uiFont);
        loginButton.putClientProperty("JButton.buttonType", "roundRect");
        loginButton.setBackground(new Color(0, 120, 215)); // Modern Blue
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> attemptLogin());
        panel.add(loginButton);
        
        // Set login button as default button for Enter key
        getRootPane().setDefaultButton(loginButton);

        JButton signupButton = new JButton("Sign Up");
        signupButton.setFont(uiFont);
        signupButton.putClientProperty("JButton.buttonType", "roundRect");
        signupButton.addActionListener(e -> attemptSignUp());
        panel.add(signupButton);

        add(panel);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User authenticatedUser = userManager.authenticateUser(username, password);
        if (authenticatedUser != null) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close login frame
            // Launch main application frame
            SwingUtilities.invokeLater(() -> new QueryStoreFrame(authenticatedUser.getUsername()));
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attemptSignUp() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Sign Up Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userManager.userExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already taken. Please choose another.", "Sign Up Failed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean registered = userManager.registerUser(username, password);
        if (registered) {
            JOptionPane.showMessageDialog(this, "Sign Up successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Optionally, log in the user immediately
            usernameField.setText("");
            passwordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Error during sign up. Please try again.", "Sign Up Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
