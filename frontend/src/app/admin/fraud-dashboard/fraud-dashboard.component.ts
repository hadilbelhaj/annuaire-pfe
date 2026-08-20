import { Component, OnInit } from '@angular/core';
import { CollusionFlag, CollusionThresholds, PsfraudService } from '../../services/Stats/psfraud.service';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
import { Content, TDocumentDefinitions, TableCell } from 'pdfmake/interfaces';
import { Router } from '@angular/router';

(pdfMake as any).vfs = pdfFonts.vfs;

@Component({
  selector: 'app-fraud-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './fraud-dashboard.component.html',
  styleUrl: './fraud-dashboard.component.css'
})
export class FraudDashboardComponent implements OnInit {
    collusionFlags: CollusionFlag[] = [];
    loading = false;
    error: string | null = null;
    activeTab = 'all';
    
    dateRangeForm: FormGroup;
    professionalForm: FormGroup;
    adherentForm: FormGroup;
    thresholdsForm: FormGroup;
    
    defaultThresholds: CollusionThresholds = {
      minMovementsForSuspicion: 3,
      claimsPerMonthThreshold: 20,
      percentDaysWithMultipleClaimsThreshold: 30,
      mostFrequentActePSPercentThreshold: 70
    };

    getLowRiskCount(): number {
      return this.collusionFlags.filter(flag => flag.riskScore < 50).length;
    }
    
    getMediumRiskCount(): number {
      return this.collusionFlags.filter(flag => flag.riskScore >= 50 && flag.riskScore < 80).length;
    }
    
    getHighRiskCount(): number {
      return this.collusionFlags.filter(flag => flag.riskScore >= 80).length;
    }
    navigateBack(): void {
      this.router.navigate(['/admin/frauds']);
    }
  
    constructor(
      private psfraudService: PsfraudService,
      private fb: FormBuilder,private router:Router
    ) {
      this.dateRangeForm = this.fb.group({
        startDate: [''],
        endDate: ['']
      });
      
      this.professionalForm = this.fb.group({
        professionalName: ['']
      });
      
      this.adherentForm = this.fb.group({
        firstName: [''],
        lastName: ['']
      });
      
      this.thresholdsForm = this.fb.group({
        minMovementsForSuspicion: [this.defaultThresholds.minMovementsForSuspicion],
        claimsPerMonthThreshold: [this.defaultThresholds.claimsPerMonthThreshold],
        percentDaysWithMultipleClaimsThreshold: [this.defaultThresholds.percentDaysWithMultipleClaimsThreshold],
        mostFrequentActePSPercentThreshold: [this.defaultThresholds.mostFrequentActePSPercentThreshold]
      });
    }
  
    ngOnInit(): void {
      this.loadAllFraudData();
    }
  
    setActiveTab(tab: string): void {
      this.activeTab = tab;
      if (tab === 'all') {
        this.loadAllFraudData();
      }
    }
  
    loadAllFraudData(): void {
      this.loading = true;
      this.error = null;
      
      this.psfraudService.detectCollusion().subscribe({
        next: (data) => {
          this.collusionFlags = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load fraud data';
          this.loading = false;
          console.error(err);
        }
      });
    }
  
    searchByDateRange(): void {
      const { startDate, endDate } = this.dateRangeForm.value;
      if (!startDate || !endDate) {
        this.error = 'Please provide both start and end dates';
        return;
      }
  
      this.loading = true;
      this.error = null;
      
      this.psfraudService.detectCollusionByDateRange(startDate, endDate).subscribe({
        next: (data) => {
          this.collusionFlags = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load fraud data for the selected date range';
          this.loading = false;
          console.error(err);
        }
      });
    }
  
    searchByProfessional(): void {
      const { professionalName } = this.professionalForm.value;
      if (!professionalName) {
        this.error = 'Please provide a professional name';
        return;
      }
  
      this.loading = true;
      this.error = null;
      
      this.psfraudService.detectCollusionByProfessionalName(professionalName).subscribe({
        next: (data) => {
          this.collusionFlags = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load fraud data for the selected professional';
          this.loading = false;
          console.error(err);
        }
      });
    }
  
    searchByAdherent(): void {
      const { firstName, lastName } = this.adherentForm.value;
      if (!firstName || !lastName) {
        this.error = 'Please provide both first and last name of adherent';
        return;
      }
  
      this.loading = true;
      this.error = null;
      
      this.psfraudService.detectCollusionByAdherentName(firstName, lastName).subscribe({
        next: (data) => {
          this.collusionFlags = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load fraud data for the selected adherent';
          this.loading = false;
          console.error(err);
        }
      });
    }
  
    applyCustomThresholds(): void {
      const thresholds = this.thresholdsForm.value as CollusionThresholds;
      
      this.loading = true;
      this.error = null;
      
      this.psfraudService.detectCollusionWithCustomThresholds(thresholds).subscribe({
        next: (data) => {
          this.collusionFlags = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to apply custom thresholds';
          this.loading = false;
          console.error(err);
        }
      });
    }
  
    resetThresholds(): void {
      this.thresholdsForm.patchValue(this.defaultThresholds);
    }
  
    getRiskLevelClass(riskScore: number): string {
      if (riskScore >= 80) return 'bg-red-100 text-red-800';
      if (riskScore >= 50) return 'bg-yellow-100 text-yellow-800';
      return 'bg-green-100 text-green-800';
    }
    generateFraudReport(): void {
      // Define document style and content
      const docDefinition: TDocumentDefinitions = {
        pageSize: 'A4',
        pageOrientation: 'landscape', // Use landscape for more table space
        pageMargins: [40, 60, 40, 40],
        
        // Define header
        header: {
          columns: [
            {
              text: 'FRAUD DETECTION REPORT',
              margin: [40, 20, 0, 0],
              fontSize: 16,
              bold: true,
              color: '#333333'
            }
          ]
        },
        
        // Define footer with page numbers
        footer: (currentPage, pageCount) => {
          return {
            columns: [
              { text: 'Generated on: ' + new Date().toLocaleDateString(), alignment: 'left', margin: [40, 0, 0, 0], fontSize: 8, color: '#777777' },
              { text: `Page ${currentPage} of ${pageCount}`, alignment: 'right', margin: [0, 0, 40, 0], fontSize: 8, color: '#777777' }
            ]
          };
        },
        
        // Document content
        content: [
          // Summary section
          {
            columns: [
              {
                text: [
                  { text: 'Low Risk: ', bold: true },
                  { text: this.getLowRiskCount().toString() }
                ],
                width: 'auto',
                margin: [0, 0, 20, 0]
              },
              {
                text: [
                  { text: 'Medium Risk: ', bold: true },
                  { text: this.getMediumRiskCount().toString() }
                ],
                width: 'auto',
                margin: [0, 0, 20, 0]
              },
              {
                text: [
                  { text: 'High Risk: ', bold: true },
                  { text: this.getHighRiskCount().toString() }
                ],
                width: 'auto'
              }
            ],
            margin: [0, 0, 0, 20]
          },
          
          // Thresholds section
          {
            text: 'Detection Thresholds',
            style: 'sectionHeader',
            margin: [0, 0, 0, 5]
          },
          this.createThresholdsTable(),
          
          // Detailed flags section
          {
            text: 'Detailed Fraud Flags',
            style: 'sectionHeader',
            margin: [0, 20, 0, 5]
          },
          this.createDetailedFlagsTable()
        ],
        
        // Define document styles
        styles: {
          sectionHeader: {
            fontSize: 14,
            bold: true,
            color: '#333333',
            margin: [0, 5, 0, 5]
          },
          tableHeader: {
            bold: true,
            fontSize: 10,
            color: 'white',
            fillColor: '#336699'
          }
        }
      };
      
      // Generate and open the PDF
      pdfMake.createPdf(docDefinition).open();
    }
    
    // Helper method to create thresholds table
    private createThresholdsTable(): Content {
      // Get current thresholds (either default or custom)
      const thresholds = this.thresholdsForm.value as CollusionThresholds;
      
      return {
        table: {
          headerRows: 1,
          widths: ['*', 80],
          body: [
            [
              { text: 'Threshold Parameter', style: 'tableHeader' },
              { text: 'Value', style: 'tableHeader', alignment: 'center' }
            ],
            ['Minimum Movements For Suspicion', { text: thresholds.minMovementsForSuspicion.toString(), alignment: 'center' }],
            ['Claims Per Month Threshold', { text: thresholds.claimsPerMonthThreshold.toString(), alignment: 'center' }],
            ['Days With Multiple Claims (%)', { text: thresholds.percentDaysWithMultipleClaimsThreshold.toString() + '%', alignment: 'center' }],
            ['Most Frequent ActePS Percent Threshold', { text: thresholds.mostFrequentActePSPercentThreshold.toString() + '%', alignment: 'center' }]
          ]
        },
        layout: {
          fillColor: (rowIndex: number) => {
            return rowIndex === 0 ? '#336699' : null;
          },
          hLineWidth: (i, node) => 0.5,
          vLineWidth: (i, node) => 0.5,
          hLineColor: () => '#cccccc',
          vLineColor: () => '#cccccc'
        }
      };
    }
    
    // Helper method to create detailed flags table
    private createDetailedFlagsTable(): Content {
      // Create table header row
      const tableBody: TableCell[][] = [
        [
          { text: 'Doctor', style: 'tableHeader' },
          { text: 'Adherent', style: 'tableHeader' },
          { text: 'Risk Score', style: 'tableHeader', alignment: 'center' },
          { text: 'Claims/Month', style: 'tableHeader', alignment: 'center' },
          { text: 'Multiple Claims (%)', style: 'tableHeader', alignment: 'center' },
          { text: 'Most Common Act', style: 'tableHeader' },
          { text: 'Act Frequency (%)', style: 'tableHeader', alignment: 'center' },
          { text: 'Total Amount', style: 'tableHeader', alignment: 'right' },
          { text: 'Suspicious', style: 'tableHeader', alignment: 'center' }
        ]
      ];
      this.collusionFlags.forEach(flag => {
        tableBody.push([
          flag.doctorName,
          flag.adherentName,
          { 
            text: flag.riskScore.toString(), 
            alignment: 'center',
            bold: flag.riskScore >= 80 ? true : false
          },
          { text: flag.indicators.claimsPerMonth.toString(), alignment: 'center' },
          { text: flag.indicators.percentDaysWithMultipleClaims.toString() + '%', alignment: 'center' },
          flag.indicators.mostFrequentActePSName,
          { text: flag.indicators.mostFrequentActePSPercent.toString() + '%', alignment: 'center' },
          { text: flag.indicators.totalAmountClaimed.toFixed(2) + 'DT', alignment: 'right' },
          { 
            text: flag.indicators.suspicious ? 'Yes' : 'No', 
            alignment: 'center',
            bold: flag.indicators.suspicious ? true : false
          }
        ]);
      });
      
      return {
        table: {
          headerRows: 1,
          widths: ['auto', 'auto', 'auto', 'auto', 'auto', '*', 'auto', 'auto', 'auto'],
          body: tableBody
        },
        layout: {
          fillColor: (rowIndex: number) => {
            return rowIndex === 0 ? '#336699' : (rowIndex % 2 === 0 ? '#f2f2f2' : null);
          },
          hLineWidth: () => 0.5,
          vLineWidth: () => 0.5,
          hLineColor: () => '#cccccc',
          vLineColor: () => '#cccccc'
        }
      };
    }
}