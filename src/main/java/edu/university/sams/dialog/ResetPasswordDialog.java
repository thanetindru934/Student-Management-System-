package edu.university.sams.dialog;

import edu.university.sams.dao.UserDAOImpl;
import edu.university.sams.model.User;
import edu.university.sams.service.SecurityService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ResetPasswordDialog extends JDialog {
    private final UserDAOImpl userDAO;
    private final User user;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private final SecurityService securityService = new SecurityService();

    public ResetPasswordDialog(JDialog parent, UserDAOImpl userDAO, User user) {
        super(parent, "Reset Password", true);
        this.userDAO = userDAO;
        this.user = user;
        initializeGUI();
    }

    private void initializeGUI() {
        setSize(350, 200);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Info Panel
        JPanel infoPanel = new JPanel(new FlowLayout());
        JLabel infoLabel = new JLabel("Reset password for: " + user.getUsername());
        infoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(infoLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        // New Password
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("New Password:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        newPasswordField = new JPasswordField();
        formPanel.add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        confirmPasswordField = new JPasswordField();
        formPanel.add(confirmPasswordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton resetButton = new JButton("Reset Password");
        JButton cancelButton = new JButton("Cancel");

        resetButton.addActionListener(this::resetPassword);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(resetButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        getRootPane().setDefaultButton(resetButton);
    }

    private void resetPassword(ActionEvent e) {
        if (newPasswordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Please enter a new password.");
            newPasswordField.requestFocus();
            return;
        }

        if (!java.util.Arrays.equals(newPasswordField.getPassword(), confirmPasswordField.getPassword())) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            confirmPasswordField.requestFocus();
            return;
        }

        // Validate user and userId before proceeding
        if (user == null || user.getUserId() == null || user.getUserId().isBlank()) {
            JOptionPane.showMessageDialog(this, "Invalid user. Cannot reset password.");
            return;
        }

        // Capture password chars and run hashing + DB update off the EDT
        char[] pw1 = newPasswordField.getPassword();
        char[] pw2 = confirmPasswordField.getPassword();

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        newPasswordField.setEnabled(false);
        confirmPasswordField.setEnabled(false);

        new javax.swing.SwingWorker<Boolean, Void>() {
            private String userMessage = null;

            @Override
            protected Boolean doInBackground() {
                String newPasswordStr = null;
                try {
                    newPasswordStr = new String(pw1);
                    String hashed = securityService.hashPassword(newPasswordStr);
                    return userDAO.resetPassword(user.getUserId(), hashed);
                } catch (Exception ex) {
                    userMessage = "An unexpected error occurred while resetting the password.";
                    return false;
                } finally {
                    // Zero out sensitive data
                    java.util.Arrays.fill(pw1, '\0');
                    java.util.Arrays.fill(pw2, '\0');
                    newPasswordStr = null;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(ResetPasswordDialog.this, "Password reset successfully!");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ResetPasswordDialog.this,
                                userMessage != null ? userMessage : "Password reset failed.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ResetPasswordDialog.this,
                            "An unexpected error occurred while resetting the password.");
                } finally {
                    // Clear fields and restore UI state
                    newPasswordField.setText("");
                    confirmPasswordField.setText("");
                    newPasswordField.setEnabled(true);
                    confirmPasswordField.setEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }
}
