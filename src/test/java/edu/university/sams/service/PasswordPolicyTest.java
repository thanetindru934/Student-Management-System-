package src.test.java.edu.university.sams.service;

import edu.university.sams.service.security.PasswordPolicy;
import edu.university.sams.service.security.exceptions.PasswordPolicyException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyTest {

    @Test
    void UT_SEC_001_validPasswordAccepted() {
        assertDoesNotThrow(() -> PasswordPolicy.validate("SecurePass123!"));
    }

    @Test
    void UT_SEC_001_invalidTooShort() {
        PasswordPolicyException ex = assertThrows(PasswordPolicyException.class,
                () -> PasswordPolicy.validate("weak"));
        assertTrue(ex.getMessage().toLowerCase().contains("least 8"));
    }

    @Test
    void UT_SEC_001_invalidNoDigits() {
        PasswordPolicyException ex = assertThrows(PasswordPolicyException.class,
                () -> PasswordPolicy.validate("NoDigits!AA"));
        assertTrue(ex.getMessage().toLowerCase().contains("digit"));
    }

    @Test
    void UT_SEC_001_invalidNoLower() {
        PasswordPolicyException ex = assertThrows(PasswordPolicyException.class,
                () -> PasswordPolicy.validate("NOCAPS123!"));
        assertTrue(ex.getMessage().toLowerCase().contains("lowercase"));
    }
}
