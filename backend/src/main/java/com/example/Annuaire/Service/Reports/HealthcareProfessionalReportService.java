package com.example.Annuaire.Service.Reports;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.awt.image.BufferedImage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Models.AdherantsPerPs;
import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.example.Annuaire.Repository.MovementRepository;
import com.example.Annuaire.Service.Statistiques.MouvementsParPsStats;
import com.example.DTOS.MouvementParPs.AdherentStatsDTO;
import com.example.DTOS.MouvementParPs.MonthlyStatsDTO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import java.awt.Font;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;

@Service
public class HealthcareProfessionalReportService {

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private HealthcareProfessionalRepository healthcareProfessionalRepository;

    @Autowired
    private MouvementsParPsStats movementStats;

    public byte[] generateMovementsReport(Long healthcareProfessionalId, Integer year) throws Exception {
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        HealthcareProfessional professional = healthcareProfessionalRepository.findById(healthcareProfessionalId)
                .orElseThrow(() -> new Exception("Healthcare professional not found"));
        List<Movement> movements = movementRepository.findByHealthcareProfessionalIdAndDateBetween(
                healthcareProfessionalId,
                LocalDateTime.of(targetYear, 1, 1, 0, 0),
                LocalDateTime.of(targetYear, 12, 31, 23, 59, 59));

        List<AdherantsPerPs> reportData = prepareReportData(movements);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);
        for (AdherantsPerPs item : reportData) {
            System.out.println("Item: " + item.getAdherentName() + ", " + item.getDescription());
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("healthcareProfessionalId", healthcareProfessionalId);
        parameters.put("healthcareProfessionalName", professional.getName());
        parameters.put("reportYear", targetYear);
        parameters.put("reportGenerationDate", new Date());
        parameters.put("MovementsDataSource", dataSource);

        addMonthlyTrendsChart(parameters, healthcareProfessionalId, targetYear);
        addAdherentDistributionChart(parameters, healthcareProfessionalId);

        InputStream reportStream = getClass()
                .getResourceAsStream("/reports/HealthcareProfessionalMovementsReport.jrxml");
        JasperDesign jasperDesign = JRXmlLoader.load(reportStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
        System.out.println("Report generated successfully for healthcare professional ID: " + healthcareProfessionalId);
        System.out.println(outStream.toByteArray().length + " bytes");

        return outStream.toByteArray();
    }

    private List<AdherantsPerPs> prepareReportData(List<Movement> movements) {
        return movements.stream().map(movement -> new AdherantsPerPs(
                movement.getAmount(),
                movement.getDate(),
                movement.getDescription(),
                movement.getAdherant().getFirstName() + " " + movement.getAdherant().getLastName()))
                .collect(Collectors.toList());
    }

    private void addMonthlyTrendsChart(Map<String, Object> parameters, Long healthcareProfessionalId, int year) {
        List<MonthlyStatsDTO> monthlyStats = movementStats
                .getMonthlyStatsForHealthcareProfessional(healthcareProfessionalId, year);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (MonthlyStatsDTO stat : monthlyStats) {
            dataset.addValue(stat.getTotalAmount(), "Amount", stat.getMonth());
            dataset.addValue(stat.getTransactionCount(), "Count", stat.getMonth());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Movement Trends - " + year,
                "Month",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 250));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setDomainGridlinesVisible(false);
        plot.setOutlineVisible(false);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        domainAxis.setLowerMargin(0.01);
        domainAxis.setUpperMargin(0.01);

        // Customize the range axis (y-axis)
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // Customize the renderer
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter()); // Solid bars without gradients
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(false);

        // Set attractive colors for the bars
        renderer.setSeriesPaint(0, new Color(70, 130, 180)); // Steel Blue for Amount
        renderer.setSeriesPaint(1, new Color(60, 179, 113)); // Medium Sea Green for Count

        // Customize item labels if needed
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 9));
        renderer.setDefaultItemLabelPaint(Color.DARK_GRAY);

        // Customize legend
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setBackgroundPaint(Color.WHITE);

        // Customize title
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 14));
        chart.getTitle().setPaint(new Color(50, 50, 100));

        // Convert to BufferedImage with higher resolution for better quality
        BufferedImage chartImage = chart.createBufferedImage(800, 500);
        parameters.put("MonthlyTrendsChart", chartImage);
    }

    @SuppressWarnings("rawtypes")
    private void addAdherentDistributionChart(Map<String, Object> parameters, Long healthcareProfessionalId) {
        List<AdherentStatsDTO> adherentStats = movementStats
                .getAdherentDistributionForHealthcareProfessional(healthcareProfessionalId);

        // Calculate total amount for percentage calculations
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (AdherentStatsDTO stat : adherentStats) {
            totalAmount = totalAmount.add(stat.getTotalAmount());
        }

        DefaultPieDataset dataset = new DefaultPieDataset();

        // Limit the number of displayed slices to improve readability
        List<AdherentStatsDTO> sortedStats = new ArrayList<>(adherentStats);
        sortedStats.sort((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()));

        // Display top N adherents individually and group the rest
        int maxSlices = 7;
        BigDecimal othersAmount = BigDecimal.ZERO;
        int othersCount = 0;

        for (int i = 0; i < sortedStats.size(); i++) {
            AdherentStatsDTO stat = sortedStats.get(i);
            if (i < maxSlices - 1) {
                // Format label with name and percentage
                double percentage = 0;
                if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    percentage = stat.getTotalAmount().divide(totalAmount, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).doubleValue();
                }
                String label = stat.getAdherentName() + " (" + new DecimalFormat("0.0").format(percentage) + "%)";
                dataset.setValue(label, stat.getTotalAmount().doubleValue());
            } else {
                // Group remaining into "Others"
                othersAmount = othersAmount.add(stat.getTotalAmount());
                othersCount++;
            }
        }

        // Add "Others" category if needed
        if (othersCount > 0) {
            double percentage = 0;
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                percentage = othersAmount.divide(totalAmount, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
            }
            dataset.setValue("Others (" + othersCount + " adherents, " +
                    new DecimalFormat("0.0").format(percentage) + "%)",
                    othersAmount.doubleValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Adherent Distribution by Amount",
                dataset,
                true,
                true,
                false);

        // Customize chart appearance
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);

        // Customize plot
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null); // Remove shadows
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200)); // Semi-transparent labels
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        plot.setCircular(true);

        // Set an attractive color scheme
        Color[] colors = {
                new Color(51, 102, 204), // Blue
                new Color(220, 57, 18), // Red
                new Color(255, 153, 0), // Orange
                new Color(16, 150, 24), // Green
                new Color(153, 0, 153), // Purple
                new Color(0, 153, 198), // Light Blue
                new Color(221, 68, 119), // Pink
                new Color(102, 170, 0), // Lime Green
        };

        // Apply colors to sections
        int i = 0;
        @SuppressWarnings("unchecked")
        List<Comparable<?>> keys = dataset.getKeys();
        for (Comparable<?> key : keys) {
            plot.setSectionPaint(key, colors[i % colors.length]);
            i++;
        }

        // Customize labels
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}",
                NumberFormat.getCurrencyInstance(),
                NumberFormat.getPercentInstance()));

        // Customize legend
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 11));
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setBackgroundPaint(Color.WHITE);

        // Customize title
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 14));
        chart.getTitle().setPaint(new Color(50, 50, 100));

        // Convert to BufferedImage with higher resolution for better quality
        BufferedImage chartImage = chart.createBufferedImage(800, 500);
        parameters.put("AdherentDistributionChart", chartImage);
    }
}