package edu.university.sams.report;

import java.nio.file.Path;

/**
 * Run this main to generate SAMS_Report.docx in the project root (or pass a custom path as arg0).
 */
public class GenerateSAMSReport {
    public static void main(String[] args) {
        try {
            Path out = args != null && args.length > 0
                    ? Path.of(args[0])
                    : Path.of("SAMS_Report.docx");
            DocxReportWriter.writeSAMSReport(out);
            System.out.println("Report generated: " + out.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to generate report: " + e.getMessage());
            System.exit(1);
        }
    }
}
