package edu.university.sams.main;

import edu.university.sams.dao.UserDAOImpl;
import edu.university.sams.dao.DatabaseManager;
import edu.university.sams.model.User;
import edu.university.sams.model.enums.UserRole;
import edu.university.sams.service.SecurityService;

import java.util.Scanner;
import java.util.UUID;

public class SAMSApplication {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            edu.university.sams.ui.DarkTheme.apply();
            new edu.university.sams.gui.LoginWindow().setVisible(true);
        });
    }

    private static void createUser(Scanner scanner, UserDAOImpl userDAO, SecurityService securityService) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        // Hash password before storing
        String hashedPassword = securityService.hashPassword(password);

        User user = new User(
                java.util.UUID.randomUUID().toString(), // userId
                username,
                "", // email
                hashedPassword,
                UserRole.STUDENT, // default role
                "", // firstName
                "", // lastName
                true, // isActive
                "", // phone
                null // updatedAt
        );

        if (userDAO.save(user)) {
            System.out.println("User created successfully!");
        } else {
            System.out.println("Error creating user. Try again.");
        }
    }

    private static void login(Scanner scanner, SecurityService securityService) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        // Select role for authentication
        System.out.print("Select role (1=STUDENT, 2=INSTRUCTOR, 3=ADMINISTRATOR): ");
        int roleOption;
        try {
            roleOption = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException ex) {
            roleOption = 1;
        }
        UserRole role = switch (roleOption) {
            case 2 -> UserRole.INSTRUCTOR;
            case 3 -> UserRole.ADMINISTRATOR;
            default -> UserRole.STUDENT;
        };

        User user = securityService.authenticateUser(username, password, role);

        if (user != null) {
            System.out.println("Login successful! Welcome " + user.getUsername());
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }
}
