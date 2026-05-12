package edu.university.sams.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AttendanceCalculator {
    private AttendanceCalculator() {}

    public static double percentage(int totalSessions, int present, int late) {
        if (totalSessions <= 0) return 0.0;
        int attended = Math.max(0, present) + Math.max(0, late);
        BigDecimal pct = BigDecimal.valueOf(attended)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalSessions), 4, RoundingMode.HALF_UP);
        return pct.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
