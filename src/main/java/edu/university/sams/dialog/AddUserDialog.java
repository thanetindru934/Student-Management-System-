package edu.university.sams.dialog;

import edu.university.sams.dao.UserDAOImpl;
import edu.university.sams.model.User;
import edu.university.sams.model.enums.UserRole;
import edu.university.sams.service.SecurityService;
import edu.university.sams.dao.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.UUID;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Dialog for adding a new user.
 */
public class AddUserDialog extends JDialog {
    private final UserDAOImpl userDAO;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<UserRole> roleCombo;
    private boolean userAdded = false;

    public AddUserDialog(JFrame parent, UserDAOImpl userDAO) {
        super(parent, "Add New User", true);
        this.userDAO = userDAO;
        initializeGUI();
    }

    private void initializeGUI() {
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField();
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField();
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        confirmPasswordField = new JPasswordField();
        formPanel.add(confirmPasswordField, gbc);

        // First Name
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        firstNameField = new JTextField();
        formPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        lastNameField = new JTextField();
        formPanel.add(lastNameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        emailField = new JTextField();
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        phoneField = new JTextField();
        formPanel.add(phoneField, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        roleCombo = new JComboBox<>(UserRole.values());
        formPanel.add(roleCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("\u2795  Add User");
        JButton cancelButton = new JButton("\u2716  Cancel");

        // Dark theme button styling
        addButton.setBackground(new Color(52, 152, 219));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(new Dimension(130, 36));

        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(130, 36));

        addButton.addActionListener(this::addUser);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        getRootPane().setDefaultButton(addButton);
    }

    private void addUser(ActionEvent e) {
        if (!validateInput()) return;

        try {
            String username = usernameField.getText().trim();

            // Prevent duplicate usernames
            User existing = userDAO.findByUsername(username);
            if (existing != null) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose another.");
                usernameField.requestFocus();
                return;
            }

            // Build user with all required fields
            User user = new User();
            user.setUserId(UUID.randomUUID().toString());
            user.setUsername(username);

            // Password hashing with safe clearing
            char[] pwdChars = passwordField.getPassword();
            String plain = new String(pwdChars);
            // Enforce password policy
            try {
                edu.university.sams.service.security.PasswordPolicy.validate(plain);
            } catch (edu.university.sams.service.security.exceptions.PasswordPolicyException ppe) {
                JOptionPane.showMessageDialog(this, ppe.getMessage());
                return;
            }
            String hashed = SecurityService.hashPassword(plain);
            java.util.Arrays.fill(pwdChars, '\0');
            user.setPasswordHash(hashed);

            // Ensure names are not empty (DB may require non-null)
            String firstName = firstNameField.getText().trim();
            if (firstName.isEmpty()) firstName = "N/A";
            String lastName = lastNameField.getText().trim();
            if (lastName.isEmpty()) lastName = "N/A";
            user.setFirstName(firstName);
            user.setLastName(lastName);

            // Ensure unique, non-empty email
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                String suffix = UUID.randomUUID().toString().substring(0, 8);
                email = username + "+" + suffix + "@example.com";
            }
            user.setEmail(email);

            // Optional fields
            user.setPhone(phoneField.getText().trim());

            // Role default
            UserRole role = (UserRole) roleCombo.getSelectedItem();
            if (role == null) role = UserRole.STUDENT;
            user.setRole(role);

            user.setActive(true);

            boolean saved = userDAO.save(user);
            if (!saved) {
                // Fallback: direct SQL insert (try with updated_at, then without)
                try (Connection conn = DatabaseManager.getInstance().getConnection()) {
                    String sql1 = "INSERT INTO users (user_id, username, email, password_hash, role, first_name, last_name, is_active, phone, updated_at) " +
                                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
                    saved = tryInsertUser(conn, sql1, user);
                    if (!saved) {
                        String sql2 = "INSERT INTO users (user_id, username, email, password_hash, role, first_name, last_name, is_active, phone) " +
                                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        saved = tryInsertUser(conn, sql2, user);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                    saved = false;
                }
            }

            if (saved) {
                userAdded = true;
                JOptionPane.showMessageDialog(this, "User added successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user. Please verify required fields and try again.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage());
        }
    }

    // Helper to bind parameters and execute insert
    private boolean tryInsertUser(Connection conn, String sql, User user) {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, user.getUserId());
            ps.setString(i++, user.getUsername());
            ps.setString(i++, user.getEmail());
            ps.setString(i++, user.getPasswordHash());
            ps.setString(i++, user.getRole() != null ? user.getRole().name() : "STUDENT");
            ps.setString(i++, user.getFirstName());
            ps.setString(i++, user.getLastName());
            ps.setBoolean(i++, user.isActive());
            ps.setString(i++, user.getPhone());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean validateInput() {
        if (usernameField.getText().trim().isEmpty()) { usernameField.requestFocus(); return false; }
        if (passwordField.getPassword().length == 0) { passwordField.requestFocus(); return false; }
        if (!java.util.Arrays.equals(passwordField.getPassword(), confirmPasswordField.getPassword())) { confirmPasswordField.requestFocus(); return false; }
        if (firstNameField.getText().trim().isEmpty()) { firstNameField.requestFocus(); return false; }
        if (lastNameField.getText().trim().isEmpty()) { lastNameField.requestFocus(); return false; }
        if (!emailField.getText().contains("@") || !emailField.getText().contains(".")) { emailField.requestFocus(); return false; }
        return true;
    }

    public boolean isUserAdded() { return userAdded; }
}
