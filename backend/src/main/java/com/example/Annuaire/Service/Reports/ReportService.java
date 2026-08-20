package com.example.Annuaire.Service.Reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Repository.MovementRepository;
import com.example.DTOS.MovementsTable.GroupingByAdherantDto;
import com.example.DTOS.MovementsTable.GroupingByPsDto;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final DataSource dataSource;
    private final MovementRepository movementRepository;

    public ReportService(DataSource dataSource, MovementRepository movementRepository) {
        this.dataSource = dataSource;
        this.movementRepository = movementRepository;
    }

    public byte[] generateUserReport() throws Exception {
        InputStream reportStream = new ClassPathResource("reports/user_report.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "User Report");

        // Fill the report with data from the database
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

        // Export report to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public byte[] generateHealthcareProfessionalStatsReport() throws Exception {
        try {
            InputStream reportStream = new ClassPathResource("reports/PsReportMovement.jrxml").getInputStream();

            JasperReport jasperReport = JasperCompileManager.compileReport(
                    reportStream);

            List<Object[]> stats = movementRepository.findAllMovementsGroupByHealthCareProfessional();
            List<GroupingByPsDto> statsDto = stats.stream()
                    .map(o -> new GroupingByPsDto(
                            (String) o[0],
                            (String) o[1],
                            (String) o[2],
                            (Long) o[3],
                            ((BigDecimal) o[4]).doubleValue()))
                    .toList();

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(statsDto);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReportDate", new java.util.Date());
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parameters, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            e.printStackTrace();
            return null;
        }

    }

    public byte[] generateAdherantsReport() throws Exception {
        try {
            InputStream reportStream = new ClassPathResource("reports/AdherantReportMovement.jrxml").getInputStream();

            JasperReport jasperReport = JasperCompileManager.compileReport(
                    reportStream);

            List<Object[]> stats = movementRepository.findAllMovementsGroupByAdherant();
            List<GroupingByAdherantDto> statsDto = stats.stream()
                    .map(obj -> new GroupingByAdherantDto(
                            (String) obj[0],
                            (Long) obj[1],
                            (String) obj[2],
                            (Long) obj[3],
                            ((BigDecimal) obj[4]).doubleValue()))
                    .toList();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(statsDto);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReportDate", new java.util.Date());
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parameters, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            e.printStackTrace();
            return null;
        }
    }

}
