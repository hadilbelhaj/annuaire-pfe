import { Component, Input, OnInit, ElementRef, ViewChild } from '@angular/core';
import { TopProfessional } from '../../../models/Stats/healthcare-professional-stats.model';
import { CommonModule } from '@angular/common';
import { ProfessionalsPdfExportService } from '../../../services/professionals-pdf-export.service';


@Component({
  selector: 'app-top-professionals',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './top-professionals.component.html',
  styleUrl: './top-professionals.component.css'
})
export class TopProfessionalsComponent implements OnInit {
  @Input() topByVisits: TopProfessional[] = [];
  @Input() topByTransactions: TopProfessional[] = [];
  @Input() topByAverage: TopProfessional[] = [];

  activeTab: 'visits' | 'transactions' | 'average' = 'visits';
  
  @ViewChild('professionalsTable') professionalsTable: ElementRef | undefined;
  
  constructor(private pdfExportService: ProfessionalsPdfExportService) {}

  ngOnInit(): void {
    // Par défaut, on affiche le top par visites
  }

  setActiveTab(tab: 'visits' | 'transactions' | 'average'): void {
    this.activeTab = tab;
  }

  // Méthode pour formater les montants en euros
  formatAmount(amount: number): string {
    return new Intl.NumberFormat('fr-FR', { 
      style: 'currency', 
      currency: 'TND',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(amount);
  }
  
  /**
   * Export professionals data to PDF
   */
  exportToPdf(): void {
    // Check if we have data to export
    if (
      this.topByVisits.length === 0 && 
      this.topByTransactions.length === 0 && 
      this.topByAverage.length === 0
    ) {
      console.warn('No professionals data to export');
      return;
    }
    
    // Generate the PDF using the service
    this.pdfExportService.generateTopProfessionalsPdf(
      this.topByVisits,
      this.topByTransactions,
      this.topByAverage,
      this.activeTab
    );
  }
  
  /**
   * Determine if export button should be disabled
   */
  isExportDisabled(): boolean {
    switch (this.activeTab) {
      case 'visits':
        return this.topByVisits.length === 0;
      case 'transactions':
        return this.topByTransactions.length === 0;
      case 'average':
        return this.topByAverage.length === 0;
      default:
        return true;
    }
  }
}