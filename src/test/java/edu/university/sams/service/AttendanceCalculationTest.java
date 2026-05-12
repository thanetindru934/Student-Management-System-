package edu.university.sams.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AttendanceCalculationTest {

    @Test
    void UT_CALC_001_percentageCalculation() {
        double pct = AttendanceCalculator.percentage(10, 7, 1);
        assertEquals(80.00, pct, 0.001);
    }

    @Test
    void UT_CALC_002_emptyHistory() {
        double pct = AttendanceCalculator.percentage(0, 0, 0);
        assertEquals(0.0, pct, 0.0);
    }
}
