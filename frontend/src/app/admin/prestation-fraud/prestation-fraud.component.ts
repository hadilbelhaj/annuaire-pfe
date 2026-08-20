import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FraudCheckResponse, FraudDetectionService, MovementDTO } from '../../services/Stats/fraud-detection.service';
import { finalize } from 'rxjs';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
import { Content, TDocumentDefinitions, TableCell } from 'pdfmake/interfaces';

(pdfMake as any).vfs = pdfFonts.vfs;
interface DoctorSummary {
  id: number;
  name: string;
  specialty: string;
  fraudCount: number;
  totalAmount: number;
}

@Component({
  selector: 'app-prestation-fraud',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './prestation-fraud.component.html',
  styleUrl: './prestation-fraud.component.css'
})
export class PrestationFraudComponent implements OnInit {
  searchForm: FormGroup;
  doctorMovements: MovementDTO[] = [];
  loadingMovements = false;
  fraudResult: FraudCheckResponse | null = null;
  searchPerformed = false;
  checkingAll = false;
  fraudulentMovements: MovementDTO[] = [];
  showingAllMovements = false;
  allMovements: MovementDTO[] = [];
  activeTab: 'allMovements' | 'byDoctor' = 'byDoctor';
  selectedDoctorName: string = '';
  selectedDoctorSpecialty: string = '';
  doctorSummary: DoctorSummary[] = [];
  showDoctorDetailsModal: boolean = false;
  selectedDoctorFrauds: MovementDTO[] = [];
  pageSize: number = 10;
  currentPage: number = 1;
  totalPages: number = 0;
  Math = Math;
  paginatedDoctorSummary: DoctorSummary[] = [];
  
  constructor(
    private fb: FormBuilder,
    private prestationFraudService: FraudDetectionService,
    private router: Router
  ) {
    this.searchForm = this.fb.group({
      doctorName: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.activeTab = 'byDoctor';
  }
 

  navigateBack(): void {
    this.router.navigate(['/admin/frauds']);
  }
getPageNumbers(): number[] {
  const pages: number[] = [];
  const maxVisiblePages = 5; 
  
  if (this.totalPages <= maxVisiblePages) {
    for (let i = 1; i <= this.totalPages; i++) {
      pages.push(i);
    }
  } else {
    // Show first page, current page with neighbors, and last page
    const startPage = Math.max(2, this.currentPage - 1);
    const endPage = Math.min(this.totalPages - 1, this.currentPage + 1);
    
    pages.push(1);
    
    if (startPage > 2) {
      pages.push(-1); // Use -1 to represent ellipsis
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    if (endPage < this.totalPages - 1) {
      pages.push(-1); // Use -1 to represent ellipsis
    }
    
    pages.push(this.totalPages);
  }
  
  return pages;
}
  searchDoctorMovements(): void {
    if (this.searchForm.invalid) {
      this.markFormGroupTouched(this.searchForm);
      return;
    }
    
    const doctorName = this.searchForm.get('doctorName')?.value;
    this.loadingMovements = true;
    this.fraudulentMovements = [];
    
    this.prestationFraudService.getFraudulentMovementsByDoctor(doctorName)
      .pipe(
        finalize(() => this.loadingMovements = false)
      )
      .subscribe({
        next: (movements) => {
          this.fraudulentMovements = movements;
          if (movements.length > 0) {
            this.selectedDoctorName = movements[0].healthcareProfessional?.name || doctorName;
            this.selectedDoctorSpecialty = movements[0].healthcareProfessional?.medicalSpecialty || 'Unknown';
          } else {
            this.selectedDoctorName = doctorName;
            this.selectedDoctorSpecialty = 'N/A';
          }
        },
        error: (error) => {
          console.error('Error fetching fraudulent doctor movements:', error);
        }
      });
  }
  checkAllMovements(): void {
    this.fraudResult = null;
    this.fraudulentMovements = [];
    this.checkingAll = true;
    
    this.prestationFraudService.checkAllMovements()
      .pipe(
        finalize(() => this.checkingAll = false)
      )
      .subscribe({
        next: (fraudulentMovements) => {
          this.fraudulentMovements = fraudulentMovements;
          this.generateDoctorSummary();
          this.updatePagination();
          setTimeout(() => {
            window.scrollTo({
              top: document.documentElement.scrollHeight,
              behavior: 'smooth'
            });
          }, 100);
        },
        error: (error) => {
          console.error('Error checking all movements for fraud:', error);
        }
      });
  }
  
  checkPrestationFraud(movement: MovementDTO): void {
    this.fraudResult = null;
    
    this.prestationFraudService.checkPrestationFraud(movement)
      .subscribe({
        next: (result) => {
          this.fraudResult = result;
          
          setTimeout(() => {
            window.scrollTo({
              top: document.documentElement.scrollHeight,
              behavior: 'smooth'
            });
          }, 100);
        },
        error: (error) => {
          console.error('Error checking prestation fraud:', error);
        }
      });
  }
  
  markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if ((control as any).controls) {
        this.markFormGroupTouched(control as FormGroup);
      }
    });
  }
setTabAndClearResults() {
  this.activeTab ='allMovements';
  this.fraudResult = null;
}
openDoctorDetailsModal(doctorId: number): void {
  this.selectedDoctorFrauds = this.fraudulentMovements.filter(
    movement => movement.healthcareProfessional?.id === doctorId
  );
  console.log(this.selectedDoctorFrauds);
  if (this.selectedDoctorFrauds.length > 0) {
    const doctor = this.selectedDoctorFrauds[0].healthcareProfessional;
    this.selectedDoctorName = doctor?.name || 'Unknown';
    this.selectedDoctorSpecialty = doctor?.medicalSpecialty || 'Unknown';
  }
  
  this.showDoctorDetailsModal = true;
}

closeDoctorDetailsModal(): void {
  this.showDoctorDetailsModal = false;
}
generateDoctorSummary(): void {
  const doctorMap = new Map<number, DoctorSummary>();
  
  this.fraudulentMovements.forEach(movement => {
    const doctorId = movement.healthcareProfessional?.id || 0;
    const doctorName = movement.healthcareProfessional?.name || 'Unknown';
    const specialty = movement.healthcareProfessional?.medicalSpecialty || 'Unknown';
    const amount = movement.amount || 0;
    
    if (doctorMap.has(doctorId)) {
      const doctor = doctorMap.get(doctorId)!;
      doctor.fraudCount += 1;
      doctor.totalAmount += amount;
    } else {
      doctorMap.set(doctorId, {
        id: doctorId,
        name: doctorName,
        specialty: specialty,
        fraudCount: 1,
        totalAmount: amount
      });
    }
  });
  
  this.doctorSummary = Array.from(doctorMap.values());
  this.updatePagination(); // Call this to initialize pagination
}

updatePagination(): void {
  this.totalPages = Math.ceil(this.doctorSummary.length / this.pageSize);
  this.goToPage(1); // Always go to first page when updating pagination
}

goToPage(page: number): void {
  if (page < 1 || page > this.totalPages) {
    return;
  }
  
  this.currentPage = page;
  const startIndex = (page - 1) * this.pageSize;
  const endIndex = Math.min(startIndex + this.pageSize, this.doctorSummary.length);
  this.paginatedDoctorSummary = this.doctorSummary.slice(startIndex, endIndex);
}

previousPage(): void {
  this.goToPage(this.currentPage - 1);
}

nextPage(): void {
  this.goToPage(this.currentPage + 1);
}

changePageSize(event: Event): void {
  const select = event.target as HTMLSelectElement;
  this.pageSize = parseInt(select.value, 10);
  this.updatePagination();
}
exportDoctorFraudPDF(){
  if (this.fraudulentMovements.length === 0) {
    alert('No fraudulent movements to export.');
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
          text: 'Doctor Fraud Report',
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
      // Doctor info section
      {
        columns: [
          {
            stack: [
              { text: 'Doctor Information', style: 'sectionHeader', margin: [0, 0, 0, 10] },
              {
                columns: [
                  { text: 'Name: ', bold: true, width: 'auto' },
                  { text: 'Dr. ' + this.selectedDoctorName, width: 'auto' }
                ]
              },
              {
                columns: [
                  { text: 'Specialty: ', bold: true, width: 'auto' },
                  { text: this.selectedDoctorSpecialty, width: 'auto' }
                ]
              }
            ],
            width: '*'
          },
          {
            stack: [
              { text: 'Summary', style: 'sectionHeader', margin: [0, 0, 0, 10] },
              {
                columns: [
                  { text: 'Total Frauds: ', bold: true, width: 'auto' },
                  { text: this.fraudulentMovements.length.toString(), width: 'auto' }
                ]
              },
              {
                columns: [
                  { text: 'Total Amount: ', bold: true, width: 'auto' },
                  { 
                    text: this.fraudulentMovements.reduce((total, movement) => total + movement.amount, 0).toFixed(2) + ' DT', 
                    width: 'auto' 
                  }
                ]
              }
            ],
            width: '*'
          }
        ],
        margin: [0, 0, 0, 20]
      },
      
      // Fraudulent Movements section
      { text: 'Fraudulent Movements', style: 'sectionHeader', margin: [0, 10, 0, 10] },
      {
        table: {
          headerRows: 1,
          widths: ['auto', '*', '*', '*', 'auto'],
          body: [
            [
              { text: 'Date', style: 'tableHeader' },
              { text: 'Adherent', style: 'tableHeader' },
              { text: 'Prestation', style: 'tableHeader' },
              { text: 'Reason for Fraud', style: 'tableHeader' },
              { text: 'Amount', style: 'tableHeader', alignment: 'right' }
            ],
            ...this.fraudulentMovements.map(movement => [
              new Date(movement.date).toLocaleDateString(),
              movement.adherentName || 'N/A',
              movement.actePS?.prestation?.prestation_libelle || movement.actePSName || 'N/A',
              movement.description,
              { text: movement.amount.toFixed(2) + ' DT', alignment: 'right' }
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

exportToPDF(){
  // This function is used from the doctor details modal
  if (this.selectedDoctorFrauds.length === 0) {
    alert('No fraudulent movements to export.');
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
          text: 'Doctor Fraud Details Report',
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
      // Doctor info section
      {
        columns: [
          {
            stack: [
              { text: 'Doctor Information', style: 'sectionHeader', margin: [0, 0, 0, 10] },
              {
                columns: [
                  { text: 'Name: ', bold: true, width: 'auto' },
                  { text: 'Dr. ' + this.selectedDoctorName, width: 'auto' }
                ]
              },
              {
                columns: [
                  { text: 'Specialty: ', bold: true, width: 'auto' },
                  { text: this.selectedDoctorSpecialty, width: 'auto' }
                ]
              }
            ],
            width: '*'
          },
          {
            stack: [
              { text: 'Summary', style: 'sectionHeader', margin: [0, 0, 0, 10] },
              {
                columns: [
                  { text: 'Total Frauds: ', bold: true, width: 'auto' },
                  { text: this.selectedDoctorFrauds.length.toString(), width: 'auto' }
                ]
              },
              {
                columns: [
                  { text: 'Total Amount: ', bold: true, width: 'auto' },
                  { 
                    text: this.selectedDoctorFrauds.reduce((total, movement) => total + movement.amount, 0).toFixed(2) + ' DT', 
                    width: 'auto' 
                  }
                ]
              }
            ],
            width: '*'
          }
        ],
        margin: [0, 0, 0, 20]
      },
      
      // Fraudulent Movements section
      { text: 'Fraudulent Movements', style: 'sectionHeader', margin: [0, 10, 0, 10] },
      {
        table: {
          headerRows: 1,
          widths: ['auto', '*', '*', '*', 'auto'],
          body: [
            [
              { text: 'Date', style: 'tableHeader' },
              { text: 'Adherent', style: 'tableHeader' },
              { text: 'Prestation', style: 'tableHeader' },
              { text: 'Reason for Fraud', style: 'tableHeader' },
              { text: 'Amount', style: 'tableHeader', alignment: 'right' }
            ],
            ...this.selectedDoctorFrauds.map(movement => [
              new Date(movement.date).toLocaleDateString(),
              movement.adherentName || 'N/A',
              movement.actePS?.prestation?.prestation_libelle || movement.actePSName || 'N/A',
              movement.description,
              { text: movement.amount.toFixed(2) + ' DT', alignment: 'right' }
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

exportAllFraudsPDF(){
  if (this.doctorSummary.length === 0) {
    alert('No fraud summary data to export.');
    return;
  }

  // Define document style and content
  const docDefinition: TDocumentDefinitions = {
    pageSize: 'A4',
    pageOrientation: 'portrait',
    pageMargins: [40, 60, 40, 40],
    
    // Define header
    header: {
      columns: [
        {
          text: 'System-wide Fraud Summary Report',
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
            stack: [
              { text: 'System-wide Fraud Analysis', style: 'sectionHeader', margin: [0, 0, 0, 10] as [number, number, number, number] },
              {
                columns: [
                  { text: 'Total Healthcare Professionals: ', bold: true, width: 'auto' },
                  { text: this.doctorSummary.length.toString(), width: 'auto' }
                ]
              },
              {
                columns: [
                  { text: 'Total Fraudulent Movements: ', bold: true, width: 'auto' },
                  { text: this.fraudulentMovements.length.toString(), width: 'auto' }
                ]
              },
              {
                columns: [
                  { text: 'Total Fraudulent Amount: ', bold: true, width: 'auto' },
                  { 
                    text: this.fraudulentMovements.reduce((total, movement) => total + movement.amount, 0).toFixed(2) + ' DT', 
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
      
      // Fraudulent Healthcare Professionals section
      { text: 'Fraudulent Healthcare Professionals', style: 'sectionHeader', margin: [0, 10, 0, 10] as [number, number, number, number] },
      {
        table: {
          headerRows: 1,
          widths: ['*', '*', 'auto', 'auto'],
          body: [
            [
              { text: 'Healthcare Professional', style: 'tableHeader' },
              { text: 'Specialty', style: 'tableHeader' },
              { text: 'Total Frauds', style: 'tableHeader', alignment: 'center' },
              { text: 'Total Amount', style: 'tableHeader', alignment: 'right' }
            ],
            ...this.doctorSummary.map(doctor => [
              'Dr. ' + doctor.name,
              doctor.specialty,
              { text: doctor.fraudCount.toString(), alignment: 'center' },
              { text: doctor.totalAmount.toFixed(2) + ' DT', alignment: 'right' }
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
      
      // Add detailed list if not too large
      ...(this.fraudulentMovements.length <= 100 ? [
        { 
          text: 'All Fraudulent Movements', 
          style: 'sectionHeader',
          margin: [0, 20, 0, 10] as [number, number, number, number], 
          pageBreak: 'before' 
        } as any,
        {
          table: {
            headerRows: 1,
            widths: ['auto', 'auto', '*', '*', 'auto'],
            body: [
              [
                { text: 'Date', style: 'tableHeader' },
                { text: 'Doctor', style: 'tableHeader' },
                { text: 'Prestation', style: 'tableHeader' },
                { text: 'Reason', style: 'tableHeader' },
                { text: 'Amount', style: 'tableHeader', alignment: 'right' }
              ],
              ...this.fraudulentMovements.map(movement => [
                new Date(movement.date).toLocaleDateString(),
                movement.healthcareProfessional?.name || 'Unknown',
                movement.actePS?.prestation?.prestation_libelle || movement.actePSName || 'N/A',
                movement.description,
                { text: movement.amount.toFixed(2) + ' DT', alignment: 'right' }
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
      ] : [
        { text: '* Detailed movements are not included due to the large number of records.', italics: true, margin: [0, 10, 0, 0] as [number, number, number, number] }
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