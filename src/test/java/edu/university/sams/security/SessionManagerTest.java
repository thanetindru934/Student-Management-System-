package edu.university.sams.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    @Test
    void UT_SEC_002_sessionTimeoutValidation() {
        var s = SessionManager.startSession("U1");
        // Simulate 35 minutes of inactivity
        s.lastActivity = s.createdAt.minusMinutes(35);
        assertTrue(SessionManager.isExpired("U1"));
        SessionManager.endSession("U1");
    }
}
