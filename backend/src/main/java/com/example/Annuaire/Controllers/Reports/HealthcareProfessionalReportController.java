package com.example.Annuaire.Controllers.Reports;

import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Service.Reports.HealthcareProfessionalReportService;

import net.sf.jasperreports.engine.JasperExportManager;

@RestController
@RequestMapping("/api/reports/healthcare-professionals")
public class HealthcareProfessionalReportController {

    @Autowired
    private HealthcareProfessionalReportService reportService;

    @GetMapping("/{id}/movements-report")
    public ResponseEntity<byte[]> generateMovementsReport(
            @PathVariable Long id,
            @RequestParam(required = false) Integer year) {
            

        try {
            byte[] reportBytes = reportService.generateMovementsReport(id, year);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "healthcare_professional_" + id + "_report.pdf");

            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}