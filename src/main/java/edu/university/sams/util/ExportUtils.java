package edu.university.sams.util;

import javax.swing.table.DefaultTableModel;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Utility class for exporting data to various formats
 */
public class ExportUtils {

    private static final Logger LOGGER = Logger.getLogger(ExportUtils.class.getName());

    /**
     * Export table model to CSV file
     * @param model Table model to export
     * @param filePath Output file path
     * @return true if successful, false otherwise
     */
    public static boolean exportTableToCSV(DefaultTableModel model, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write(escapeCSVField(model.getColumnName(i)));
                if (i < model.getColumnCount() - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");

            // Write data
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    writer.write(escapeCSVField(value != null ? value.toString() : ""));
                    if (j < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }

            LOGGER.info("Data exported to CSV: " + filePath);
            return true;

        } catch (IOException e) {
            LOGGER.severe("Error exporting to CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Export list of maps to CSV file
     * @param data List of data maps
     * @param headers Column headers
     * @param filePath Output file path
     * @return true if successful, false otherwise
     */
    public static boolean exportDataToCSV(List<Map<String, Object>> data,
                                          String[] headers, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            for (int i = 0; i < headers.length; i++) {
                writer.write(escapeCSVField(headers[i]));
                if (i < headers.length - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");

            // Write data
            for (Map<String, Object> row : data) {
                for (int i = 0; i < headers.length; i++) {
                    Object value = row.get(headers[i].toLowerCase().replace(" ", ""));
                    writer.write(escapeCSVField(value != null ? value.toString() : ""));
                    if (i < headers.length - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }

            LOGGER.info("Data exported to CSV: " + filePath);
            return true;

        } catch (IOException e) {
            LOGGER.severe("Error exporting to CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Export attendance report to CSV
     * @param reportData Report data
     * @param filePath Output file path
     * @return true if successful, false otherwise
     */
    public static boolean exportAttendanceReportToCSV(List<Map<String, Object>> reportData,
                                                      String filePath) {
        String[] headers = {"Student ID", "Student Name", "Total Sessions",
                "Present", "Absent", "Late", "Excused", "Attendance Percentage"};

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            for (int i = 0; i < headers.length; i++) {
                writer.write(escapeCSVField(headers[i]));
                if (i < headers.length - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");

            // Write data
            for (Map<String, Object> row : reportData) {
                writer.write(escapeCSVField(getString(row, "studentId")));
                writer.write(",");
                writer.write(escapeCSVField(getString(row, "studentName")));
                writer.write(",");
                writer.write(escapeCSVField(getString(row, "totalSessions")));
                writer.write(",");
                writer.write(escapeCSVField(getString(row, "present")));
                writer.write(",");
                writer.write(escapeCSVField(getString(row, "absent")));
                writer.write(",");
                writer.write(escapeCSVField(getString(row, "late")));
                writer.write(",");
                writer.write(escapeCSVField(getString(row, "excused")));
                writer.write(",");
                writer.write(escapeCSVField(String.format("%.1f%%",
                        (Double) row.getOrDefault("attendancePercentage", 0.0))));
                writer.write("\n");
            }

            LOGGER.info("Attendance report exported to CSV: " + filePath);
            return true;

        } catch (IOException e) {
            LOGGER.severe("Error exporting attendance report to CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Escape CSV field (handle commas and quotes)
     * @param field Field value to escape
     * @return Escaped field value
     */
    private static String escapeCSVField(String field) {
        if (field == null) {
            return "";
        }

        // If field contains comma, quote, or newline, wrap in quotes and escape internal quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }

    /**
     * Get string value from map with null safety
     * @param map Map to get value from
     * @param key Key to look for
     * @return String value or empty string if null
     */
    private static String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    /**
     * Generate filename with timestamp
     * @param prefix Filename prefix
     * @param extension File extension (without dot)
     * @return Generated filename
     */
    public static String generateFilename(String prefix, String extension) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

        return prefix + "_" + now.format(formatter) + "." + extension;
    }

    /**
     * Validate file path for writing
     * @param filePath File path to validate
     * @return true if valid and writable, false otherwise
     */
    public static boolean validateFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }

        try {
            java.io.File file = new java.io.File(filePath);
            java.io.File parent = file.getParentFile();

            // Check if parent directory exists or can be created
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    LOGGER.warning("Cannot create directory: " + parent.getAbsolutePath());
                    return false;
                }
            }

            // Check if file can be created/written
            return parent == null || parent.canWrite();

        } catch (Exception e) {
            LOGGER.warning("Invalid file path: " + filePath + " - " + e.getMessage());
            return false;
        }
    }
}