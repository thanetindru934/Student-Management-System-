package edu.university.sams.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Writes a minimal .docx (Office Open XML) file using only the JDK.
 * It assembles required parts as a ZIP:
 *  - [Content_Types].xml
 *  - _rels/.rels
 *  - word/document.xml
 *
 * The content includes all sections from the SAMS case study and marking criteria.
 */
public final class DocxReportWriter {

    public static void writeSAMSReport(Path output) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {

            // [Content_Types].xml
            putText(zos, "[Content_Types].xml", contentTypes());

            // _rels/.rels
            putText(zos, "_rels/.rels", relsRoot());

            // word/document.xml
            putText(zos, "word/document.xml", documentXml());

            zos.finish();
            Files.write(output, baos.toByteArray());
        }
    }

    private static void putText(ZipOutputStream zos, String path, String xml) throws Exception {
        ZipEntry e = new ZipEntry(path);
        zos.putNextEntry(e);
        try (Writer w = new OutputStreamWriter(zos, StandardCharsets.UTF_8)) {
            w.write(xml);
            w.flush();
        }
        zos.closeEntry();
    }

    private static String contentTypes() {
        return """
               <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
               <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                 <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                 <Default Extension="xml" ContentType="application/xml"/>
                 <Override PartName="/word/document.xml"
                   ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
               </Types>
               """;
    }

    private static String relsRoot() {
        return """
               <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
               <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                 <Relationship Id="rId1"
                   Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument"
                   Target="word/document.xml"/>
               </Relationships>
               """;
    }

    // Minimal WordprocessingML. We format with simple paragraphs.
    private static String documentXml() {
        String title = "Student Attendance Management System (SAMS)";
        String subTitle = "Software Quality Assurance & Testing Report";

        // Helper to make paragraphs
        StringBuilder body = new StringBuilder();

        body.append(h1("1. Introduction"));
        body.append(p("""
                This report presents the design, implementation, and quality assurance approach for the \
                Student Attendance Management System (SAMS). The system enables instructors to mark \
                attendance (Present/Absent/Late), students to view their attendance, and administrators to \
                generate reports by course or semester. This document also details the testing strategy, \
                requirement coverage analysis, methodology for change management, and references.
                """));

        body.append(h1("2. Quality Assurance Method"));
        body.append(p("""
                We adopted a V-Model aligned approach with Agile execution. Each requirement maps to a \
                corresponding verification activity (unit, integration, system, UAT). Peer reviews and CI \
                ensure early defect discovery. Automated checks cover authentication, attendance operations, \
                and reporting, maintaining confidence during iterative changes.
                """));

        body.append(h1("3. Requirements"));
        body.append(h2("3.1 Functional Requirements"));
        body.append(bullets(new String[]{
            "User Authentication by role (Student, Instructor, Administrator).",
            "Instructor: Create lecture sessions and mark Present/Absent/Late.",
            "Student: View personal attendance summary and per-course details.",
            "Administrator: Manage users; generate and export reports.",
            "Reporting: Export user/course lists and summary snapshots."
        }));
        body.append(h2("3.2 Non-Functional Requirements"));
        body.append(bullets(new String[]{
            "Security: Password hashing (PBKDF2) and role-based access.",
            "Usability: Clear, role-focused dashboards; responsive UI.",
            "Reliability: DB-backed persistence with graceful error handling.",
            "Maintainability: Layered services/DAOs; modular UI.",
            "Performance: Long-running tasks off the UI thread."
        }));

        body.append(h1("4. Java Implementation Overview"));
        body.append(p("""
                The implementation provides a login window, role-specific dashboards, user management, \
                attendance workflows, and report exports. Passwords are hashed using PBKDF2 with per-user salts. \
                The Administrator dashboard presents live statistics, CSV exports, and utilities. UI uses Swing, \
                and DAOs handle persistence interactions.
                """));

        body.append(h1("5. Testing Strategy and Test Cases"));
        body.append(h2("5.1 Test Types and Objectives"));
        body.append(bullets(new String[]{
            "Unit Tests: Hash/verify passwords; DAO methods.",
            "Integration Tests: DB reads/writes (user creation, attendance updates).",
            "System Tests: End-to-end login and role navigation.",
            "UI Tests: Form validation and responsiveness.",
            "Non-Functional: Performance sanity and security checks."
        }));
        body.append(h2("5.2 Example Test Cases"));
        body.append(bullets(new String[]{
            "Login with valid admin credentials → Admin dashboard opens.",
            "Create a user → Appears in User Management and CSV export.",
            "Instructor marks attendance → Student summary reflects updates.",
            "Export users → File exists with correct header and rows.",
            "Password verification negative → Wrong password rejected."
        }));

        body.append(h1("6. Requirement Coverage"));
        body.append(p("""
                Implemented: authentication, role-based UI, user management, CSV exports, and attendance flows. \
                Out-of-scope (future): timetable integration, biometric login, automated alerts.
                """));

        body.append(h1("7. Methodology for Changes/Extensions"));
        body.append(p("""
                Continue with Agile (Scrum). For larger extensions (e.g., biometric login), begin with a \
                spike/PoC, refine requirements, update design and tests, then deliver iteratively with \
                regression automation to preserve quality.
                """));

        body.append(h1("8. Marking Criteria Mapping"));
        body.append(bullets(new String[]{
            "Introduction (10): Section 1.",
            "Quality attributes & requirements (10): Section 3.",
            "Java implementation (20): Section 4.",
            "Testing strategy & cases (30): Section 5.",
            "Requirement coverage (10): Section 6.",
            "Methodology & extensions (10): Section 7.",
            "Writing quality (5): Structure and coherence.",
            "References (5): Section 9 (IEEE style)."
        }));

        body.append(h1("9. References (IEEE Style)"));
        body.append(bullets(new String[]{
            "[1] ISO/IEC/IEEE 29119: Software and systems engineering — Software testing.",
            "[2] OWASP, “Authentication Cheat Sheet,” 2024.",
            "[3] Oracle, “Java Cryptography Architecture (JCA) Reference Guide,” 2024.",
            "[4] ThoughtWorks, “Continuous Integration,” M. Fowler.",
            "[5] IEEE, “Recommended Practice for Software Requirements Specifications,” 2024."
        }));

        body.append(h1("Appendix: Individual Contribution Table"));
        body.append(p("Member 1 — ID001 — 25%"));
        body.append(p("Member 2 — ID002 — 25%"));
        body.append(p("Member 3 — ID003 — 50%"));

        return """
               <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
               <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                 <w:body>
                   %s
                   <w:sectPr/>
                 </w:body>
               </w:document>
               """.formatted(
                // Title + subtitle + body
                hCenteredBold(title, 28) +
                hCentered(subTitle, 18) +
                p("") +
                body
        );
    }

    // WordprocessingML helpers:

    private static String p(String text) {
        return """
               <w:p><w:r><w:t>%s</w:t></w:r></w:p>
               """.formatted(escape(text));
    }

    private static String hCentered(String text, int size) {
        return """
               <w:p>
                 <w:pPr><w:jc w:val="center"/></w:pPr>
                 <w:r><w:rPr><w:sz w:val="%d"/></w:rPr><w:t>%s</w:t></w:r>
               </w:p>
               """.formatted(size * 2, escape(text));
    }

    private static String hCenteredBold(String text, int size) {
        return """
               <w:p>
                 <w:pPr><w:jc w:val="center"/></w:pPr>
                 <w:r><w:rPr><w:b/><w:sz w:val="%d"/></w:rPr><w:t>%s</w:t></w:r>
               </w:p>
               """.formatted(size * 2, escape(text));
    }

    private static String h1(String text) { return bold(text, 24); }
    private static String h2(String text) { return bold(text, 18); }

    private static String bold(String text, int size) {
        return """
               <w:p>
                 <w:r><w:rPr><w:b/><w:sz w:val="%d"/></w:rPr><w:t>%s</w:t></w:r>
               </w:p>
               """.formatted(size * 2, escape(text));
    }

    private static String bullets(String[] items) {
        StringBuilder sb = new StringBuilder();
        for (String it : items) {
            sb.append(p("• " + it));
        }
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;");
    }

    private DocxReportWriter() {}
}
