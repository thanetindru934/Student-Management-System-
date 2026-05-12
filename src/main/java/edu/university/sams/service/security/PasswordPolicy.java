package edu.university.sams.service.security;

import edu.university.sams.service.security.exceptions.PasswordPolicyException;

public final class PasswordPolicy {

    // Example policy: min 8 chars, at least one digit, one lowercase, one uppercase, one special
    public static void validate(String password) {
        if (password == null || password.length() < 8)
            throw new PasswordPolicyException("Password must be at least 8 characters long.");
        if (!password.matches(".*[0-9].*"))
            throw new PasswordPolicyException("Password must contain at least one digit.");
        if (!password.matches(".*[a-z].*"))
            throw new PasswordPolicyException("Password must contain at least one lowercase letter.");
        if (!password.matches(".*[A-Z].*"))
            throw new PasswordPolicyException("Password must contain at least one uppercase letter.");
        if (!password.matches(".*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?].*"))
            throw new PasswordPolicyException("Password must contain at least one special character.");
    }

    private PasswordPolicy() {}
}
