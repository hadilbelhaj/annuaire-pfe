package com.example.Annuaire.Service.Reports;

import net.sf.jasperreports.engine.*;

import java.io.InputStream;

public class JasperTest {
    public static void main(String[] args) {
        try {
            // Load the JRXML file
            InputStream reportStream = JasperTest.class
                    .getResourceAsStream("/reports/Ps/yearly_statistics.jrxml");

            if (reportStream == null) {
                throw new RuntimeException("Report file not found!");
            }

            // Compile the report
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            System.out.println("JasperReport compiled successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error compiling JasperReport: " + e.getMessage());
        }
    }
}
