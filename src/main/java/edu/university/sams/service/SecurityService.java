package edu.university.sams.service;

import edu.university.sams.dao.DatabaseManager;
import edu.university.sams.dao.UserDAO;
import edu.university.sams.dao.UserDAOImpl;
import edu.university.sams.model.User;
import edu.university.sams.model.enums.UserRole;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;

public class SecurityService {

    private static final Logger LOGGER = Logger.getLogger(SecurityService.class.getName());
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int ITERATIONS = 10000;

    private final UserDAO userDAO;

    public SecurityService() {
        this.userDAO = new UserDAOImpl(DatabaseManager.getInstance());
    }

    public User authenticateUser(String username, String password, UserRole role) {
        try {
            System.out.println("Authenticating user: " + username + " with role: " + role);
            User user = userDAO.findByUsername(username);

            if (user == null) {
                System.out.println("User not found in database: " + username);
                return null;
            }

            System.out.println("User found - Role: " + user.getRole() + ", Active: " + user.isActive());

            if (user.getRole() == role && user.isActive()) {
                // Simple password check for testing
                if (password.equals("password") && user.getPasswordHash().equals("password")) {
                    System.out.println("Authentication successful for: " + username);
                    return user;
                }

                // Try hashed password verification
                if (verifyPassword(password, user.getPasswordHash())) {
                    System.out.println("Authentication successful with hash for: " + username);
                    return user;
                }
            }

            System.out.println("Authentication failed for: " + username);
            return null;
        } catch (Exception e) {
            LOGGER.severe("Authentication error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_LENGTH * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(salt) + ":" +
                    Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            LOGGER.severe("Error hashing password: " + e.getMessage());
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            // Handle plain text passwords for testing
            if (hashedPassword.equals("password")) {
                return password.equals("password");
            }

            String[] parts = hashedPassword.split(":");
            if (parts.length != 2) return false;

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_LENGTH * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            if (storedHash.length != testHash.length) return false;

            int diff = 0;
            for (int i = 0; i < storedHash.length; i++) {
                diff |= storedHash[i] ^ testHash[i];
            }

            return diff == 0;
        } catch (Exception e) {
            LOGGER.severe("Error verifying password: " + e.getMessage());
            return false;
        }
    }
}