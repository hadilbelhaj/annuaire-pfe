package com.example.Annuaire.Service.Reports;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.util.Rotation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Component;
import java.awt.Font;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.TextAnchor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ChartGenerator {

        public BufferedImage createSpecialtyPieChart(Map<String, Long> specialtyVolumes, int width, int height) {

                long totalVolume = specialtyVolumes.values().stream().mapToLong(Long::longValue).sum();

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                specialtyVolumes.entrySet().stream()
                                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                                .limit(15)
                                .forEach(entry -> {

                                        double percentage = 100.0 * entry.getValue() / totalVolume;

                                        String formattedKey = String.format("%s (%.1f%%)",
                                                        entry.getKey(), percentage);

                                        dataset.addValue(entry.getValue(), formattedKey, "");
                                });

                JFreeChart chart = ChartFactory.createBarChart(
                                "Specialty Transaction Volumes", // chart title
                                "", // category axis label (x-axis for vertical, y-axis for horizontal)
                                "Number of Transactions", // value axis label (y-axis for vertical, x-axis for
                                                          // horizontal)
                                dataset, // dataset
                                PlotOrientation.HORIZONTAL, // orientation
                                true, // include legend
                                true, // tooltips
                                false // URLs
                );

                // Get and customize the plot
                CategoryPlot plot = chart.getCategoryPlot();
                plot.setBackgroundPaint(Color.WHITE);
                plot.setOutlinePaint(null);
                plot.setRangeGridlinesVisible(true);
                plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
                plot.setDomainGridlinesVisible(false);

                // Customize the bars
                BarRenderer renderer = (BarRenderer) plot.getRenderer();
                renderer.setDrawBarOutline(false);
                renderer.setShadowVisible(false);
                renderer.setBarPainter(new StandardBarPainter()); // Simpler look without gradients
                renderer.setItemMargin(0.02); // Space between bars

                // Use a nice color
                renderer.setSeriesPaint(0, new Color(65, 105, 225)); // Royal blue

                // Customize the domain axis (categories axis)
                CategoryAxis domainAxis = plot.getDomainAxis();
                domainAxis.setCategoryMargin(0.25); // Space between category groups
                domainAxis.setLowerMargin(0.05); // Space at the left (for horizontal) of the chart
                domainAxis.setUpperMargin(0.05); // Space at the right (for horizontal) of the chart
                domainAxis.setMaximumCategoryLabelLines(2); // Allow labels to wrap to 2 lines if needed

                // Customize the range axis (values axis)
                NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
                rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                rangeAxis.setAutoRangeIncludesZero(true);

                // Customize the labels
                domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
                rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

                // Set the other fonts
                Font titleFont = new Font("SansSerif", Font.BOLD, 20);
                Font labelFont = new Font("SansSerif", Font.PLAIN, 18);
                chart.getTitle().setFont(titleFont);
                domainAxis.setLabelFont(labelFont);
                rangeAxis.setLabelFont(labelFont);

                // Add data labels to the bars
                renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                                "{2}", new DecimalFormat("#,###")));
                renderer.setDefaultItemLabelsVisible(true);
                renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 20));
                renderer.setDefaultPositiveItemLabelPosition(
                                new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));

                // Create and return the image
                return chart.createBufferedImage(width, height);
        }

        // In ChartGenerator.java

        public BufferedImage createPrestationTypePieChart(Map<String, Long> prestationTypeDistribution, int width,
                        int height) {
                DefaultPieDataset dataset = new DefaultPieDataset();

                // Sort entries by count in descending order for better visualization
                List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(prestationTypeDistribution.entrySet());
                sortedEntries.sort(Map.Entry.<String, Long>comparingByValue().reversed());

                // Take top 10 entries for better readability
                long othersTotal = 0;
                int count = 0;
                for (Map.Entry<String, Long> entry : sortedEntries) {
                        if (count++ < 10) { // Limit to top 10 for clarity
                                // Add count to label: "Type (123)"
                                dataset.setValue(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue());
                        } else {
                                othersTotal += entry.getValue();
                        }
                }

                // Add Others category if needed
                if (othersTotal > 0) {
                        dataset.setValue("Others (" + othersTotal + ")", othersTotal);
                }

                JFreeChart chart = ChartFactory.createPieChart(
                                "Distribution by Prestation Type",
                                dataset,
                                true, // include legend
                                true, // tooltips
                                false // URLs
                );

                // Customize pie chart
                PiePlot plot = (PiePlot) chart.getPlot();
                plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
                plot.setNoDataMessage("No data available");
                plot.setCircular(true);
                plot.setLabelGap(0.02);

                // Add percentage labels to the slices
                plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                                "{0}: {2}", // Format: "Label: Percentage%"
                                NumberFormat.getNumberInstance(),
                                new DecimalFormat("0.0%")));

                // Make the chart a bit larger to accommodate the added text
                return chart.createBufferedImage(width, height);
        }

        public BufferedImage createActePSBarChart(Map<String, Long> actePSDistribution, int width, int height) {
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                // Sort entries by count in descending order
                List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(actePSDistribution.entrySet());
                sortedEntries.sort(Map.Entry.<String, Long>comparingByValue().reversed());

                // Take top 15 entries for better readability
                int count = 0;
                for (Map.Entry<String, Long> entry : sortedEntries) {
                        if (count++ < 15) { // Limit to top 15
                                // Add count to label for better visibility
                                dataset.addValue(entry.getValue(), "Transactions",
                                                entry.getKey() + " (" + entry.getValue() + ")");
                        }
                }

                JFreeChart chart = ChartFactory.createBarChart(
                                "ActePS Distribution",
                                "Acte PS",
                                "Number of Transactions",
                                dataset,
                                PlotOrientation.VERTICAL,
                                false, // include legend
                                true, // tooltips
                                false // URLs
                );

                // Customize bar chart
                CategoryPlot plot = chart.getCategoryPlot();
                BarRenderer renderer = (BarRenderer) plot.getRenderer();
                renderer.setDrawBarOutline(false);
                renderer.setItemMargin(0.1);
                renderer.setSeriesPaint(0, new Color(30, 144, 255)); // Dodger Blue

                // Add value labels on top of bars
                renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
                renderer.setDefaultItemLabelsVisible(true);
                renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 12));
                renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
                                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));

                // Rotate category labels for better readability if many categories
                CategoryAxis domainAxis = plot.getDomainAxis();
                domainAxis.setCategoryLabelPositions(
                                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
                domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

                // Set range axis font
                NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
                rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

                // Improve chart title
                chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 18));

                // Enhance visual appearance
                chart.setBackgroundPaint(Color.WHITE);
                plot.setBackgroundPaint(Color.WHITE);
                plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
                plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

                // Enhance rendering quality
                chart.setAntiAlias(true);
                chart.setTextAntiAlias(true);

                return chart.createBufferedImage(width, height);
        }

        public BufferedImage createFinancialAnalysisBarChart(Map<String, BigDecimal> financialAnalysis, int width,
                        int height) {
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                List<Map.Entry<String, BigDecimal>> sortedEntries = new ArrayList<>(financialAnalysis.entrySet());
                sortedEntries.sort(Map.Entry.<String, BigDecimal>comparingByValue().reversed());

                int count = 0;
                for (Map.Entry<String, BigDecimal> entry : sortedEntries) {
                        if (count++ < 15) {
                                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("fr", "TN"));
                                String formattedValue = formatter.format(entry.getValue());
                                dataset.addValue(entry.getValue(), "Revenue",
                                                entry.getKey() + " (" + formattedValue + ")");
                        }
                }

                JFreeChart chart = ChartFactory.createBarChart(
                                "Revenue by Prestation Type",
                                "Prestation Type",
                                "Revenue (TND)",
                                dataset,
                                PlotOrientation.VERTICAL,
                                false, // include legend
                                true, // tooltips
                                false // URLs
                );

                CategoryPlot plot = chart.getCategoryPlot();
                BarRenderer renderer = (BarRenderer) plot.getRenderer();
                renderer.setDrawBarOutline(false);
                renderer.setItemMargin(0.1);
                renderer.setSeriesPaint(0, new Color(46, 139, 87)); // Sea Green

                // Use currency format for the y-axis
                NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
                rangeAxis.setNumberFormatOverride(NumberFormat.getCurrencyInstance(new Locale("fr", "TN")));
                rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

                // Rotate category labels for better readability
                CategoryAxis domainAxis = plot.getDomainAxis();
                domainAxis.setCategoryLabelPositions(
                                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
                domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

                // Improve chart title
                chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 18));

                // Enhance visual appearance
                chart.setBackgroundPaint(Color.WHITE);
                plot.setBackgroundPaint(Color.WHITE);
                plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
                plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

                // Enhance rendering quality
                chart.setAntiAlias(true);
                chart.setTextAntiAlias(true);

                return chart.createBufferedImage(width, height);
        }
}