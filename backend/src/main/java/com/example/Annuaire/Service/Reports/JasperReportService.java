package com.example.Annuaire.Service.Reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.Annuaire.enums.PeriodType;
import com.example.Annuaire.Models.ActePSDistribution;
import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Models.Prestation;
import com.example.Annuaire.Models.PrestationFinancial;
import com.example.Annuaire.Models.PrestationType;
import com.example.Annuaire.Models.SpecialtyVolume;
import com.example.Annuaire.Models.TopProfessional;
import com.example.Annuaire.Models.TransactionStatistics;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.example.Annuaire.Repository.MovementRepository;
import com.example.Annuaire.Repository.TransactionStatisticsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class JasperReportService {

        private final TransactionStatisticsRepository statisticsRepository;
        private final MovementRepository movementRepository;
        private final HealthcareProfessionalRepository professionalRepository;
        @Autowired
        private ChartGenerator chartGenerator;

        @Autowired
        public JasperReportService(TransactionStatisticsRepository statisticsRepository,
                        MovementRepository movementRepository,
                        HealthcareProfessionalRepository professionalRepository) {
                this.statisticsRepository = statisticsRepository;
                this.movementRepository = movementRepository;
                this.professionalRepository = professionalRepository;
        }

        public byte[] generateReport(String period, PeriodType periodType) throws Exception {
                TransactionStatistics statistics = statisticsRepository.findByPeriodAndPeriodType(period, periodType);
                if (statistics == null) {
                        throw new RuntimeException(
                                        "Statistics not found for period: " + period + " and type: " + periodType);
                }
                LocalDateTime startOfPeriod, endOfPeriod;
                String formattedPeriod;

                switch (periodType) {
                        case MONTH:
                                String[] parts = period.split("-");
                                int year = Integer.parseInt(parts[0]);
                                int month = Integer.parseInt(parts[1]);

                                startOfPeriod = LocalDateTime.of(year, month, 1, 0, 0);
                                endOfPeriod = startOfPeriod.plusMonths(1).minusNanos(1);
                                formattedPeriod = java.time.Month.of(month).toString() + " " + year;
                                break;

                        case QUARTER:
                                parts = period.split("-Q");
                                year = Integer.parseInt(parts[0]);
                                int quarter = Integer.parseInt(parts[1]);
                                int startMonth = (quarter - 1) * 3 + 1;

                                startOfPeriod = LocalDateTime.of(year, startMonth, 1, 0, 0);
                                endOfPeriod = startOfPeriod.plusMonths(3).minusNanos(1);
                                formattedPeriod = "Q" + parts[1] + " " + parts[0];
                                break;

                        case YEAR:
                                year = Integer.parseInt(period);

                                startOfPeriod = LocalDateTime.of(year, 1, 1, 0, 0);
                                endOfPeriod = startOfPeriod.plusYears(1).minusNanos(1);
                                formattedPeriod = period;
                                break;

                        default:
                                throw new IllegalArgumentException("Unknown period type: " + periodType);
                }

                String periodTypeStr = periodType == PeriodType.MONTH ? "Monthly"
                                : periodType == PeriodType.QUARTER ? "Quarterly" : "Yearly";

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("reportTitle", periodTypeStr + " Healthcare Statistics Report - " + formattedPeriod);
                parameters.put("period", period);
                parameters.put("periodType", periodTypeStr);
                parameters.put("totalMonetaryValue", statistics.getTotalMonetaryValue());
                parameters.put("averageTransactionAmount", statistics.getAverageTransactionAmount());
                parameters.put("highestActivityRegion", statistics.getHighestActivityRegion());
                parameters.put("lowestActivityRegion", statistics.getLowestActivityRegion());
                parameters.put("growthPercentage", statistics.getMonthOverMonthGrowthPercentage());
                parameters.put("generatedAt", statistics.getGeneratedAt().toString());
                parameters.put("topProfessionalByPatientCountName", statistics.getTopProfessionalByPatientCountName());
                parameters.put("topProfessionalByTransactionAmountName",
                                statistics.getTopProfessionalByTransactionAmountName());
                parameters.put("topProfessionalByAverageValueName", statistics.getTopProfessionalByAverageValueName());

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Long> specialtyVolumes = mapper.readValue(
                                statistics.getSpecialtyTransactionVolumes(),
                                new TypeReference<Map<String, Long>>() {
                                });
                Map<String, Long> prestationTypeDistribution = mapper.readValue(
                                statistics.getPrestationTypeDistributionJson(),
                                new TypeReference<Map<String, Long>>() {
                                });
                Map<String, Long> actePSDistribution = mapper.readValue(
                                statistics.getActePSDistributionJson(),
                                new TypeReference<Map<String, Long>>() {
                                });
                Map<String, BigDecimal> prestationFinancialAnalysis = mapper.readValue(
                                statistics.getPrestationFinancialAnalysisJson(),
                                new TypeReference<Map<String, BigDecimal>>() {
                                });

                BufferedImage chartImage = chartGenerator.createSpecialtyPieChart(specialtyVolumes, 800, 600);

                // BufferedImage to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(chartImage, "png", baos);
                byte[] chartBytes = baos.toByteArray();
                parameters.put("specialtyChartImage", new ByteArrayInputStream(chartBytes));
                // prestattion
                BufferedImage prestationChartImage = chartGenerator.createPrestationTypePieChart(
                                prestationTypeDistribution, 800, 600);
                ByteArrayOutputStream prestationBaos = new ByteArrayOutputStream();
                ImageIO.write(prestationChartImage, "png", prestationBaos);
                byte[] prestationChartBytes = prestationBaos.toByteArray();
                parameters.put("prestationTypeChartImage", new ByteArrayInputStream(prestationChartBytes));
                // acteps
                BufferedImage actePSChartImage = chartGenerator.createActePSBarChart(
                                actePSDistribution, 800, 600);
                ByteArrayOutputStream actePSBaos = new ByteArrayOutputStream();
                ImageIO.write(actePSChartImage, "png", actePSBaos);
                byte[] actePSChartBytes = actePSBaos.toByteArray();
                parameters.put("actePSChartImage", new ByteArrayInputStream(actePSChartBytes));
                // financial
                BufferedImage financialChartImage = chartGenerator.createFinancialAnalysisBarChart(
                                prestationFinancialAnalysis, 800, 600);
                ByteArrayOutputStream financialBaos = new ByteArrayOutputStream();
                ImageIO.write(financialChartImage, "png", financialBaos);
                byte[] financialChartBytes = financialBaos.toByteArray();
                parameters.put("financialAnalysisChartImage", new ByteArrayInputStream(financialChartBytes));

                JRBeanCollectionDataSource specialtyDataSource = new JRBeanCollectionDataSource(
                                specialtyVolumes.entrySet().stream()
                                                .map(entry -> new SpecialtyVolume(entry.getKey(), entry.getValue()))
                                                .collect(Collectors.toList()));
                parameters.put("specialtyDataSource", specialtyDataSource);

                JRBeanCollectionDataSource prestationTypeDataSource = new JRBeanCollectionDataSource(
                                prestationTypeDistribution.entrySet().stream()
                                                .map(entry -> new PrestationType(entry.getKey(), entry.getValue()))
                                                .collect(Collectors.toList()));
                parameters.put("prestationTypeDataSource", prestationTypeDataSource);

                JRBeanCollectionDataSource actePSDataSource = new JRBeanCollectionDataSource(
                                actePSDistribution.entrySet().stream()
                                                .map(entry -> new ActePSDistribution(entry.getKey(), entry.getValue()))
                                                .collect(Collectors.toList()));
                parameters.put("actePSDataSource", actePSDataSource);

                JRBeanCollectionDataSource financialDataSource = new JRBeanCollectionDataSource(
                                prestationFinancialAnalysis.entrySet().stream()
                                                .map(entry -> new PrestationFinancial(entry.getKey(), entry.getValue()))
                                                .collect(Collectors.toList()));
                parameters.put("financialAnalysisDataSource", financialDataSource);
                try {
                        BufferedImage qrCodeImage = generateStatisticsQrCode(statistics, period, periodTypeStr);
                        parameters.put("statisticsQrCode", new ByteArrayInputStream(imageToByteArray(qrCodeImage)));
                } catch (Exception e) {
                        Log.error("Failed to generate QR code for statistics", e);
                        // Handle error appropriately - perhaps use a placeholder image
                }

                List<Movement> movements = movementRepository.findByDateBetween(startOfPeriod, endOfPeriod);

                List<TopProfessional> topProfessionals = getTopProfessionals(movements);
                JRBeanCollectionDataSource professionalsDataSource = new JRBeanCollectionDataSource(topProfessionals);
                parameters.put("topProfessionalsDataSource", professionalsDataSource);

                String templateName;
                switch (periodType) {
                        case MONTH:
                                templateName = "reports/Ps/monthly_statistics.jrxml";
                                break;
                        case QUARTER:
                                templateName = "reports/Ps/quarterly_statistics.jrxml";
                                break;
                        case YEAR:
                                templateName = "reports/Ps/yearly_statistics.jrxml";
                                break;
                        default:
                                templateName = "reports/statistics.jrxml";
                }

                ClassPathResource reportResource = new ClassPathResource(templateName);
                if (!reportResource.exists()) {
                        throw new RuntimeException("Report template not found: reports/ps/monthly_statistics.jrxml");
                }
                JasperReport jasperReport = JasperCompileManager.compileReport(reportResource.getInputStream());

                JasperPrint jasperPrint = JasperFillManager.fillReport(
                                jasperReport,
                                parameters,
                                new JREmptyDataSource());

                return JasperExportManager.exportReportToPdf(jasperPrint);
        }

        private List<TopProfessional> getTopProfessionals(List<Movement> movements) {

                Map<HealthcareProfessional, Set<Long>> professionalPatients = new HashMap<>();
                Map<HealthcareProfessional, BigDecimal> professionalAmounts = new HashMap<>();

                for (Movement movement : movements) {
                        HealthcareProfessional professional = movement.getHealthcareProfessional();

                        professionalPatients.computeIfAbsent(professional, k -> new HashSet<>())
                                        .add(movement.getAdherant().getId());

                        professionalAmounts.compute(professional,
                                        (k, v) -> (v == null) ? movement.getAmount() : v.add(movement.getAmount()));
                }

                return professionalAmounts.entrySet().stream()
                                .map(entry -> {
                                        HealthcareProfessional professional = entry.getKey();
                                        BigDecimal totalAmount = entry.getValue();
                                        int patientCount = professionalPatients
                                                        .getOrDefault(professional, Collections.emptySet()).size();
                                        BigDecimal avgAmount = totalAmount.divide(
                                                        BigDecimal.valueOf(Math.max(1, patientCount)),
                                                        RoundingMode.HALF_UP);

                                        return new TopProfessional(
                                                        professional.getId(),
                                                        professional.getName(),
                                                        professional.getmedicalSpecialty(),
                                                        professional.getRegion(),
                                                        patientCount,
                                                        totalAmount,
                                                        avgAmount);
                                })
                                .sorted(Comparator.comparing(TopProfessional::getTotalAmount).reversed())
                                .limit(10)
                                .collect(Collectors.toList());
        }

        private BufferedImage generateStatisticsQrCode(TransactionStatistics statistics, String period,
                        String periodTypeStr) throws Exception {
                // Build JSON payload with all statistics data
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode payload = mapper.createObjectNode();
                ObjectNode dataNode = mapper.createObjectNode();

                // Add all the statistics parameters to the data node
                dataNode.put("period", period);
                dataNode.put("periodType", periodTypeStr);
                dataNode.put("totalMonetaryValue", statistics.getTotalMonetaryValue().toString());
                dataNode.put("averageTransactionAmount", statistics.getAverageTransactionAmount().toString());
                dataNode.put("highestActivityRegion", statistics.getHighestActivityRegion());
                dataNode.put("lowestActivityRegion", statistics.getLowestActivityRegion());
                dataNode.put("growthPercentage", statistics.getMonthOverMonthGrowthPercentage().toString());
                dataNode.put("generatedAt", statistics.getGeneratedAt().toString());
                dataNode.put("topProfessionalByPatientCountName", statistics.getTopProfessionalByPatientCountName());
                dataNode.put("topProfessionalByTransactionAmountName",
                                statistics.getTopProfessionalByTransactionAmountName());
                dataNode.put("topProfessionalByAverageValueName", statistics.getTopProfessionalByAverageValueName());

                // Set the data in the main payload
                payload.set("data", dataNode);
                payload.put("societe", "i-way"); // Replace with your company name

                // Call the QR code generation service
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                ResponseEntity<byte[]> response = restTemplate.postForEntity(
                                "http://localhost:5000/generate-qr",
                                new HttpEntity<>(payload, headers),
                                byte[].class);

                // Convert the response to a BufferedImage
                return ImageIO.read(new ByteArrayInputStream(response.getBody()));
        }

        private byte[] imageToByteArray(BufferedImage image) throws IOException {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                return baos.toByteArray();
        }
}
