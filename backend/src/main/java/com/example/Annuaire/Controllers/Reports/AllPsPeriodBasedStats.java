package com.example.Annuaire.Controllers.Reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.enums.PeriodType;
import com.example.Annuaire.Models.TransactionStatistics;
import com.example.Annuaire.Service.Reports.JasperReportService;
import com.example.Annuaire.Service.Reports.PeriodBasedPsReports;

@RestController
@RequestMapping("/api/statistics")
public class AllPsPeriodBasedStats {
    private final PeriodBasedPsReports statisticsService;
    private final JasperReportService reportService;

    @Autowired
    public AllPsPeriodBasedStats(PeriodBasedPsReports statisticsService,
            JasperReportService reportService) {
        this.statisticsService = statisticsService;
        this.reportService = reportService;
    }

    @GetMapping("/generate/month/{year}/{month}")
    public ResponseEntity<TransactionStatistics> generateMonthlyStatistics(
            @PathVariable int year, @PathVariable int month) {
        TransactionStatistics statistics = statisticsService.generateMonthlyStatistics(year, month);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/generate/quarter/{year}/{quarter}")
    public ResponseEntity<TransactionStatistics> generateQuarterlyStatistics(
            @PathVariable int year, @PathVariable int quarter) {
        TransactionStatistics statistics = statisticsService.generateQuarterlyStatistics(year, quarter);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/generate/year/{year}")
    public ResponseEntity<TransactionStatistics> generateYearlyStatistics(@PathVariable int year) {
        TransactionStatistics statistics = statisticsService.generateYearlyStatistics(year);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/report/month/{year}/{month}")
    public ResponseEntity<byte[]> getMonthlyReport(@PathVariable int year, @PathVariable int month) {
        try {
            String period = String.format("%d-%02d", year, month);
            byte[] report = reportService.generateReport(period, PeriodType.MONTH);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=monthly-report-" + period + ".pdf")
                    .body(report);
        } catch (Exception e) {
            e.printStackTrace(); // Logs to console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/report/quarter/{year}/{quarter}")
    public ResponseEntity<byte[]> getQuarterlyReport(
            @PathVariable int year, @PathVariable int quarter) {
        try {
            String period = String.format("%d-Q%d", year, quarter);
            byte[] report = reportService.generateReport(period, PeriodType.QUARTER);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=quarterly-report-" + period + ".pdf")
                    .body(report);
        } catch (Exception e) {
            e.printStackTrace(); // Logs to console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/report/year/{year}")
    public ResponseEntity<byte[]> getYearlyReport(@PathVariable int year) {
        try {
            String period = String.valueOf(year);
            byte[] report = reportService.generateReport(period, PeriodType.YEAR);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=yearly-report-" + period + ".pdf")
                    .body(report);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/exists/{periodType}/{period}")
    public ResponseEntity<Boolean> checkReportExists(
            @PathVariable String periodType,
            @PathVariable String period) {
        boolean exists = statisticsService.checkReportExists(period, PeriodType.valueOf(periodType.toUpperCase()));
        return ResponseEntity.ok(exists);
    }
}
