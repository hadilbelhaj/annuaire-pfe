package com.example.Annuaire.Controllers.Reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Repository.MovementRepository;
import com.example.Annuaire.Service.Reports.ReportService;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    private final MovementRepository movementRepository;

    public ReportController(ReportService reportService, MovementRepository movementRepository) {
        this.reportService = reportService;
        this.movementRepository = movementRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<byte[]> downloadUserReport() {
        try {
            byte[] pdfReport = reportService.generateUserReport();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=user_report.pdf");
            return new ResponseEntity<>(pdfReport, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ps")
    public ResponseEntity<?> generateHealthcareProfessionalStatsReport() throws Exception {
        byte[] reportBytes = reportService.generateHealthcareProfessionalStatsReport();
        ByteArrayResource resource = new ByteArrayResource(reportBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=healthcare_professional_stats_report.pdf");

        // Return the response
        return ResponseEntity.ok().headers(headers).contentLength(reportBytes.length)
                .contentType(MediaType.APPLICATION_PDF).body(resource);
    }

    @GetMapping("/adherants")
    public ResponseEntity<?> generateAdherantsReport() throws Exception {
        byte[] reportBytes = reportService.generateAdherantsReport();
        ByteArrayResource resource = new ByteArrayResource(reportBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=adherants_report.pdf");

        // Return the response
        return ResponseEntity.ok().headers(headers).contentLength(reportBytes.length)
                .contentType(MediaType.APPLICATION_PDF).body(resource);
    }

    @GetMapping("/movements")
    public ResponseEntity<?> generateMovementsReport() {
        try {
            JasperReport jasperReport = JasperCompileManager
                    .compileReport("backend/src/main/resources/reports/movementReport.jrxml");

            // 2. Fetch data (implement fetchMovements to retrieve your list)
            List<Movement> movements = movementRepository.findAll();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(movements);

            Map<String, Object> parameters = new HashMap<>();
            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            // Set headers for download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "TreatmentMovementsReport.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);


        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.ok("not ok");

        }

    }

}
