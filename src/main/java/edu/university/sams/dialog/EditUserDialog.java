package edu.university.sams.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import edu.university.sams.dao.UserDAOImpl;
import edu.university.sams.model.User;
import edu.university.sams.model.enums.UserRole;

public class EditUserDialog extends JDialog {
    private final UserDAOImpl userDAO;
    private final User user;
    private JTextField usernameField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<UserRole> roleCombo;
    private JCheckBox activeCheckBox;
    private boolean userUpdated = false;

    public EditUserDialog(JFrame parent, UserDAOImpl userDAO, User user) {
        super(parent, "Edit User", true);
        this.userDAO = userDAO;
        this.user = user;
        initializeGUI();
        populateFields();
    }

    private void initializeGUI() {
        setSize(450, 350);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 10, 10, 0);
        usernameField = new JTextField();
        usernameField.setEditable(false); // Username cannot be changed
        usernameField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(usernameField, gbc);

        // First Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(new JLabel("First Name:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 10, 10, 0);
        firstNameField = new JTextField();
        formPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(new JLabel("Last Name:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 10, 10, 0);
        lastNameField = new JTextField();
        formPanel.add(lastNameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 10, 10, 0);
        emailField = new JTextField();
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 10, 10, 0);
        phoneField = new JTextField();
        formPanel.add(phoneField, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 10, 10, 0);
        roleCombo = new JComboBox<>(UserRole.values());
        formPanel.add(roleCombo, gbc);

        // Active Status
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(0, 0, 10, 0);
        activeCheckBox = new JCheckBox("Active User");
        formPanel.add(activeCheckBox, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton updateButton = new JButton("Update User");
        JButton resetPasswordButton = new JButton("Reset Password");
        JButton cancelButton = new JButton("Cancel");

        updateButton.addActionListener(this::updateUser);
        resetPasswordButton.addActionListener(this::resetPassword);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(resetPasswordButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Set default button
        getRootPane().setDefaultButton(updateButton);
    }

    private void populateFields() {
        usernameField.setText(user.getUsername());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        roleCombo.setSelectedItem(user.getRole());
        activeCheckBox.setSelected(user.isActive());
    }

    private void updateUser(ActionEvent e) {
        if (!validateInput()) return;

        try {
            user.setFirstName(firstNameField.getText().trim());
            user.setLastName(lastNameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setPhone(phoneField.getText().trim());
            user.setRole((UserRole) roleCombo.getSelectedItem());
            user.setActive(activeCheckBox.isSelected());
            user.setUpdatedAt(new java.util.Date());

            // Correct method call
            userDAO.update(user);

            userUpdated = true;
            JOptionPane.showMessageDialog(this, "User updated successfully!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + ex.getMessage());
        }
    }

    private void resetPassword(ActionEvent e) {
        ResetPasswordDialog dialog = new ResetPasswordDialog(this, userDAO, user);
        dialog.setVisible(true);
    }

    private boolean validateInput() {
        if (firstNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a first name.");
            firstNameField.requestFocus();
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a last name.");
            lastNameField.requestFocus();
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an email address.");
            emailField.requestFocus();
            return false;
        }
        String email = emailField.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }
        return true;
    }

    public boolean isUserUpdated() {
        return userUpdated;
    }
}
