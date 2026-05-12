package edu.university.sams.security;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {

    public static final int DEFAULT_TIMEOUT_MINUTES = 30;

    public static final class Session {
        public final String userId;
        public final LocalDateTime createdAt;
        public volatile LocalDateTime lastActivity;
        public final int timeoutMinutes;

        Session(String userId, int timeoutMinutes) {
            this.userId = userId;
            this.timeoutMinutes = timeoutMinutes;
            this.createdAt = LocalDateTime.now();
            this.lastActivity = this.createdAt;
        }
    }

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static Session startSession(String userId) {
        Session s = new Session(userId, DEFAULT_TIMEOUT_MINUTES);
        SESSIONS.put(userId, s);
        return s;
    }

    public static void touch(String userId) {
        Session s = SESSIONS.get(userId);
        if (s != null) s.lastActivity = LocalDateTime.now();
    }

    public static boolean isExpired(String userId) {
        Session s = SESSIONS.get(userId);
        if (s == null) return true;
        Duration idle = Duration.between(s.lastActivity, LocalDateTime.now());
        return idle.toMinutes() >= s.timeoutMinutes;
    }

    public static void endSession(String userId) {
        SESSIONS.remove(userId);
    }

    private SessionManager() {}
}
