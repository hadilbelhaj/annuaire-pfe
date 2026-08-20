import { Component, ElementRef, ViewChild } from '@angular/core';
import { Prestation } from '../../../services/Stats/prestation-stats.service';
import { PrestationStatsService } from '../../../services/Stats/prestation-stats.service';
import { CommonModule } from '@angular/common';
import { PrestationChartComponent } from '../prestation-chart/prestation-chart.component';
import { PrestationTableComponent } from '../prestation-table/prestation-table.component';
import { FormsModule } from '@angular/forms';
import { PdfExportService } from '../../../services/pdf-export.service';
import { ChartToImageService } from '../../../services/chart-to-image.service';

@Component({
  selector: 'app-prestation-statistics',
  standalone: true,
  imports: [CommonModule,PrestationChartComponent,PrestationTableComponent,FormsModule],
  templateUrl: './prestation-statistics.component.html',
  styleUrl: './prestation-statistics.component.css'
})
export class PrestationStatisticsComponent {
  prestations: any[] = [];
  loading = false;
  error = false;
  countOptions = [5, 10];
  selectedCount = 5;
  @ViewChild('chartContainer') chartContainer: ElementRef | undefined;
  

  constructor(
    private prestationService: PrestationStatsService,
    private pdfExportService: PdfExportService,
    private chartToImageService: ChartToImageService
  ) {}

  ngOnInit(): void {
    this.fetchPrestationData();
  }

  fetchPrestationData(): void {
    this.loading = true;
    this.error = false;
    
    this.prestationService.getMostFrequentPrestations(this.selectedCount)
      .subscribe({
        next: (data) => {
          this.prestations = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching prestation data:', err);
          this.loading = false;
          this.error = true;
        }
      });
  }

  onCountChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.selectedCount = parseInt(select.value, 10);
    this.fetchPrestationData();
  }

  /**
   * Export the current prestation data to PDF
   */
  exportToPdf(): void {
    if (this.prestations.length === 0) {
      console.warn('No prestation data to export');
      return;
    }

    // First check if chart element is available
    if (this.chartContainer && this.chartContainer.nativeElement) {
      // Get the chart element reference (this may need to be adjusted based on your actual chart structure)
      const chartElement = this.chartContainer.nativeElement.querySelector('app-prestation-chart');
      
      if (chartElement) {
        // Convert chart to image before generating PDF
        this.chartToImageService.convertChartToImage(chartElement)
          .then(chartBase64 => {
            this.pdfExportService.generatePrestationPdf(this.prestations, chartBase64);
          })
          .catch(error => {
            console.error('Error converting chart to image:', error);
            // Fall back to generating PDF without chart
            this.pdfExportService.generatePrestationPdf(this.prestations);
          });
      } else {
        // No chart element found, generate PDF without chart
        this.pdfExportService.generatePrestationPdf(this.prestations);
      }
    } else {
      // No chart container found, generate PDF without chart
      this.pdfExportService.generatePrestationPdf(this.prestations);
    }
  }
}
