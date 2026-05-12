import edu.university.sams.service.SecurityService;

import java.util.Scanner;

public class PasswordHasher {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter password to hash: ");
        String password = scanner.nextLine().trim();

        String hashedPassword = SecurityService.hashPassword(password);
        System.out.println("Hashed password: " + hashedPassword);

        scanner.close();
    }
}
