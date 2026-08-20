import { Component } from '@angular/core';
import { EnhancedFraudCheckResult, FraudStats, ReimbursementFraudService } from '../../services/Stats/reimbursement-fraud.service';
import { DateRangePickerComponent } from '../date-range-picker/date-range-picker.component';
import { AllReimbursementsComponent } from '../all-reimbursements/all-reimbursements.component';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
import { Content, TDocumentDefinitions, TableCell } from 'pdfmake/interfaces';
@Component({
  selector: 'app-reimbursement-fraud',
  standalone: true,
  imports: [DateRangePickerComponent,AllReimbursementsComponent,CommonModule],
  templateUrl: './reimbursement-fraud.component.html',
  styleUrl: './reimbursement-fraud.component.css'
})
export class ReimbursementFraudComponent {
  results: EnhancedFraudCheckResult[] = [];
  selectedResult: EnhancedFraudCheckResult | null = null;
  loading = false;
  fraudStats: FraudStats | null = null;
  displayedResults: any[] = []; 
  currentPage: number = 1;
  pageSize: number = 10;
  totalPages: number = 1;
  Math = Math;
 
  
  constructor(private fraudService: ReimbursementFraudService,private router: Router) {}
  
  ngOnInit() {
    this.refreshStats();
    this.loadAllReimbursements(false);
  }
  
  loadAllReimbursements(onlyFraudulent: boolean) {
    this.loading = true;
    this.fraudService.checkAll().subscribe({
      next: (data) => {
        this.results = data;
        this.totalPages = Math.ceil(this.results.length / this.pageSize);
        this.updateDisplayedResults();
        this.loading = false;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading reimbursements:', error);
        this.loading = false;
      }
    });
  }
  
  onDateRangeSelected(event: {startDate: Date, endDate: Date}) {
    this.loading = true;
    this.fraudService.checkDateRange(event.startDate, event.endDate).subscribe({
      next: (data) => {
        this.results = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading date range results:', error);
        this.loading = false;
      }
    });
  }
  navigateBack(): void {
    this.router.navigate(['/admin/frauds']);
  }
  
  refreshStats() {
    this.fraudService.getFraudStats().subscribe({
      next: (data) => {
        this.fraudStats = data;
      },
      error: (error) => {
        console.error('Error loading fraud stats:', error);
      }
    });
  }
  
  openDetailsDialog(result: EnhancedFraudCheckResult) {
    this.selectedResult = result;
  }
  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updateDisplayedResults();
    }
  }
  
  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updateDisplayedResults();
    }
  }
  getStartIndex(): number {
    return (this.currentPage - 1) * this.pageSize + 1;
  }
  
  // Helper method for template to calculate ending index
  getEndIndex(): number {
    return Math.min(this.currentPage * this.pageSize, this.results.length);
  }
  updateDisplayedResults() {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = Math.min(startIndex + this.pageSize, this.results.length);
    this.displayedResults = this.results.slice(startIndex, endIndex);
  }
  // Add this method to your ReimbursementFraudComponent class

exportReimbursementFraudPDF() {
  if (this.results.length === 0) {
    alert('No fraud results to export.');
    return;
  }

  // Define document style and content
  const docDefinition: TDocumentDefinitions = {
    pageSize: 'A4',
    pageOrientation: 'landscape',
    pageMargins: [40, 60, 40, 40],
    
    // Define header
    header: {
      columns: [
        {
          text: 'Reimbursement Fraud Report',
          margin: [40, 20, 0, 0] as [number, number, number, number],
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
          { text: 'Generated on: ' + new Date().toLocaleDateString(), alignment: 'left', margin: [40, 0, 0, 0] as [number, number, number, number], fontSize: 8, color: '#777777' },
          { text: `Page ${currentPage} of ${pageCount}`, alignment: 'right', margin: [0, 0, 40, 0] as [number, number, number, number], fontSize: 8, color: '#777777' }
        ]
      };
    },
    
    // Document content
    content: [
      // Summary section
      {
        columns: [
          {
            stack: [
              { text: 'Fraud Statistics', style: 'sectionHeader', margin: [0, 0, 0, 10] as [number, number, number, number] },
              {
                columns: [
                  { text: 'Total Reimbursements: ', bold: true, width: 'auto' },
                  { text: this.fraudStats ? this.fraudStats.totalReimbursements.toString() : '0', width: 'auto' }
                ]
              },
              {
                columns: [
                  { text: 'Fraudulent Reimbursements: ', bold: true, width: 'auto' },
                  { text: this.fraudStats ? this.fraudStats.totalFraudulentReimbursements.toString() : '0', width: 'auto' }
                ]
              },
              {
                columns: [
                  { text: 'Fraud Percentage: ', bold: true, width: 'auto' },
                  { 
                    text: this.fraudStats ? this.fraudStats.fraudPercentage.toFixed(2) + '%' : '0%', 
                    width: 'auto' 
                  }
                ]
              }
            ],
            width: '*'
          }
        ],
        margin: [0, 0, 0, 20] as [number, number, number, number]
      },
      
      // Reimbursement Fraud Table
      { text: 'Reimbursement Fraud Results', style: 'sectionHeader', margin: [0, 10, 0, 10] as [number, number, number, number] },
      {
        table: {
          headerRows: 1,
          widths: ['auto', '*', '*', '*', 'auto', 'auto'],
          body: [
            [
              { text: 'Date', style: 'tableHeader' },
              { text: 'Adherent', style: 'tableHeader' },
              { text: 'Doctor', style: 'tableHeader' },
              { text: 'Description', style: 'tableHeader' },
              { text: 'Alerts', style: 'tableHeader', alignment: 'center' },
              { text: 'Amount', style: 'tableHeader', alignment: 'right' }
            ],
            ...this.results.map(result => [
              new Date(result.movementDate).toLocaleDateString(),
              result.adherantName || 'N/A',
              result.doctorName || 'N/A',
              result.movementDescription || 'N/A',
              { text: result.alerts.length.toString(), alignment: 'center' },
              { text: result.amount.toFixed(2) + ' DT', alignment: 'right' }
            ])
          ]
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
      },
      
      // Add detailed list of alerts if not too large
      ...(this.results.length <= 100 ? [
        { 
          text: 'Fraud Alert Details', 
          style: 'sectionHeader',
          margin: [0, 20, 0, 10] as [number, number, number, number], 
          pageBreak: 'before' 
        } as any,
        ...this.generateAlertDetailsContent()
      ] : [
        { text: '* Detailed alerts are not included due to the large number of records.', italics: true, margin: [0, 10, 0, 0] as [number, number, number, number] }
      ])
    ],
    
    // Define document styles
    styles: {
      sectionHeader: {
        fontSize: 14,
        bold: true,
        color: '#333333',
        margin: [0, 5, 0, 5] as [number, number, number, number]
      },
      tableHeader: {
        bold: true,
        fontSize: 10,
        color: 'white',
        fillColor: '#336699'
      },
      alertHeader: {
        fontSize: 12,
        bold: true, 
        color: '#333333',
        margin: [0, 10, 0, 5] as [number, number, number, number]
      }
    }
  };
  
  // Generate and open the PDF
  pdfMake.createPdf(docDefinition).open();
}

// Helper method to generate alert details content
private generateAlertDetailsContent() {
  const alertsContent = [];
  
  for (const result of this.results) {
    if (result.alerts.length > 0) {
      // Add reimbursement header
      alertsContent.push({
        text: `Reimbursement: ${result.adherantName} - ${new Date(result.movementDate).toLocaleDateString()}`,
        style: 'alertHeader',
        margin: [0, 10, 0, 5] as [number, number, number, number]
      });
      
      // Add alert table for this reimbursement
      alertsContent.push({
        table: {
          headerRows: 1,
          widths: ['*', '*', 'auto'],
          body: [
            [
              { text: 'Alert Type', style: 'tableHeader' },
              { text: 'Description', style: 'tableHeader' },
              { text: 'Status', style: 'tableHeader', alignment: 'center' }
            ],
            ...result.alerts.map(alert => [
              alert.alertType,
              alert.description,
              { text: alert.status, alignment: 'center' }
            ])
          ]
        },
        layout: {
          fillColor: (rowIndex: number) => {
            return rowIndex === 0 ? '#336699' : (rowIndex % 2 === 0 ? '#f2f2f2' : null);
          },
          hLineWidth: () => 0.5,
          vLineWidth: () => 0.5,
          hLineColor: () => '#cccccc',
          vLineColor: () => '#cccccc'
        },
        margin: [0, 5, 0, 15] as [number, number, number, number]
      });
    }
  }
  
  return alertsContent;
}

// Add this method to export a single reimbursement detail
exportSelectedReimbursementPDF() {
  if (!this.selectedResult) {
    alert('No reimbursement selected.');
    return;
  }

  // Define document style and content for a single reimbursement
  const docDefinition: TDocumentDefinitions = {
    pageSize: 'A4',
    pageOrientation: 'portrait',
    pageMargins: [40, 60, 40, 40],
    
    // Define header
    header: {
      columns: [
        {
          text: 'Reimbursement Fraud Detail Report',
          margin: [40, 20, 0, 0] as [number, number, number, number],
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
          { text: 'Generated on: ' + new Date().toLocaleDateString(), alignment: 'left', margin: [40, 0, 0, 0] as [number, number, number, number], fontSize: 8, color: '#777777' },
          { text: `Page ${currentPage} of ${pageCount}`, alignment: 'right', margin: [0, 0, 40, 0] as [number, number, number, number], fontSize: 8, color: '#777777' }
        ]
      };
    },
    
    // Document content
    content: [
      // Reimbursement details section
      {
        stack: [
          { text: 'Reimbursement Information', style: 'sectionHeader', margin: [0, 0, 0, 10] as [number, number, number, number] },
          {
            columns: [
              { text: 'Adherent: ', bold: true, width: 'auto' },
              { text: this.selectedResult.adherantName || 'N/A', width: 'auto' }
            ]
          },
          {
            columns: [
              { text: 'Doctor: ', bold: true, width: 'auto' },
              { text: this.selectedResult.doctorName || 'N/A', width: 'auto' }
            ]
          },
          {
            columns: [
              { text: 'Date: ', bold: true, width: 'auto' },
              { text: new Date(this.selectedResult.movementDate).toLocaleDateString(), width: 'auto' }
            ]
          },
          {
            columns: [
              { text: 'Description: ', bold: true, width: 'auto' },
              { text: this.selectedResult.movementDescription || 'N/A', width: 'auto' }
            ]
          },
          {
            columns: [
              { text: 'Amount: ', bold: true, width: 'auto' },
              { text: this.selectedResult.amount.toFixed(2) + ' DT', width: 'auto' }
            ]
          }
        ],
        margin: [0, 0, 0, 20] as [number, number, number, number]
      },
      
      // Fraud alerts section
      { text: 'Fraud Alerts', style: 'sectionHeader', margin: [0, 10, 0, 10] as [number, number, number, number] },
      
      // Show message if no alerts
      ...(this.selectedResult.alerts.length === 0 ? [
        { text: 'No fraud alerts detected for this reimbursement.', italics: true, margin: [0, 5, 0, 5] as [number, number, number, number] }
      ] : [
        // Show alert table
        {
          table: {
            headerRows: 1,
            widths: ['*', '*', 'auto', 'auto'],
            body: [
              [
                { text: 'Alert Type', style: 'tableHeader' },
                { text: 'Description', style: 'tableHeader' },
                { text: 'Detection Date', style: 'tableHeader' },
                { text: 'Status', style: 'tableHeader', alignment: 'center' }
              ],
              ...this.selectedResult.alerts.map(alert => [
                alert.alertType,
                alert.description,
                new Date(alert.detectedAt).toLocaleDateString(),
                { text: alert.status, alignment: 'center' }
              ])
            ]
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
        }
      ])
    ],
    
    // Define document styles
    styles: {
      sectionHeader: {
        fontSize: 14,
        bold: true,
        color: '#333333',
        margin: [0, 5, 0, 5] as [number, number, number, number]
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

}
