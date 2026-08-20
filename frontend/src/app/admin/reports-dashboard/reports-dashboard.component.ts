import { Component, OnInit } from '@angular/core';
import { ReportServiceService } from '../../services/Stats/report-service.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { Observable } from 'rxjs';

interface ReportInfo {
  id: string;
  name: string;
}

interface ReportTypeMap {
  adherents: ReportInfo;
  professionals: ReportInfo;
  movements: ReportInfo;
  [key: string]: ReportInfo; 
}

interface CategoryReportMap {
  'Monthly Statistics': ReportTypeMap;
  'Quarterly Statistics': ReportTypeMap;
  'Yearly Statistics': ReportTypeMap;
  [key: string]: ReportTypeMap; 
}

@Component({
  selector: 'app-reports-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reports-dashboard.component.html',
  styleUrls: ['./reports-dashboard.component.css']
})

export class ReportsDashboardComponent implements OnInit {
  reportCategories = [
    { name: 'Monthly Statistics' },
    { name: 'Quarterly Statistics' },
    { name: 'Yearly Statistics' }
  ];

  reportMap: CategoryReportMap = {
    'Monthly Statistics': {
      'adherents': { id: 'monthly-top-adherents', name: 'Most Active Adherents' },
      'professionals': { id: 'monthly-top-professionals', name: 'Most Frequent Healthcare Professionals' },
      'movements': { id: 'monthly-movements', name: 'Number of Movements per Month' }
    },
    'Quarterly Statistics': {
      'adherents': { id: 'quarterly-adherent-activity', name: 'Adherent Activity' },
      'professionals': { id: 'quarterly-specialty', name: 'Highest-Billed Medical Specialty' },
      'movements': { id: 'quarterly-acts', name: 'Total Acts per Quarter' }
    },
    'Yearly Statistics': {
      'adherents': { id: 'yearly-adherent-spending', name: 'Adherent Spending Trends' },
      'professionals': { id: 'yearly-professional-revenue', name: 'Professional Revenue' },
      'movements': { id: 'yearly-movements', name: 'Total Movements per Year' }
    }
  };
  selectedCategory: any = null;
  selectedReportType: string = 'adherents';
  selectedReport: any = null;
  availableYears: number[] = [];
  availableMonths: { value: number; label: string }[] = [];
  availableQuarters: { value: number; label: string }[] = [];
  filters = {
    year: new Date().getFullYear(),
    month: new Date().getMonth() + 1, // Default to current month (1-12)
    quarter: Math.ceil((new Date().getMonth() + 1) / 3), // Calculate current quarter (1-4)
    limit: 5
  };
  isGenerating = false;
  reportData: any = null;
  isLoading = false;
  reportExists = false;

  constructor(private reportService: ReportServiceService) {}

  ngOnInit() {
    const currentYear = new Date().getFullYear();
    this.availableYears = Array.from({ length: 6 }, (_, i) => currentYear - i);
    this.availableMonths = [
      { value: 1, label: "January" },
      { value: 2, label: "February" },
      { value: 3, label: "March" },
      { value: 4, label: "April" },
      { value: 5, label: "May" },
      { value: 6, label: "June" },
      { value: 7, label: "July" },
      { value: 8, label: "August" },
      { value: 9, label: "September" },
      { value: 10, label: "October" },
      { value: 11, label: "November" },
      { value: 12, label: "December" }
    ];
  
    this.availableQuarters = [
      { value: 1, label: "Q1 (Jan - Mar)" },
      { value: 2, label: "Q2 (Apr - Jun)" },
      { value: 3, label: "Q3 (Jul - Sep)" },
      { value: 4, label: "Q4 (Oct - Dec)" }
    ];
  }

  selectCategory(category: any) {
    this.selectedCategory = category;
    this.updateSelectedReport();
  }

  updateSelectedReport() {
    if (this.selectedCategory && this.selectedReportType) {
      const categoryName = this.selectedCategory.name as keyof CategoryReportMap;
      const reportType = this.selectedReportType as keyof ReportTypeMap;
      
      if (this.reportMap[categoryName] && this.reportMap[categoryName][reportType]) {
        this.selectedReport = this.reportMap[categoryName][reportType];
        this.reportData = null;
        this.reportExists = false;
        this.checkIfReportExists();
      }
    }
  }
  onFilterChange() {
    this.reportExists = false;
    this.checkIfReportExists();
  }
  checkIfReportExists() {
    if (!this.selectedCategory || !this.selectedReport) {
      this.reportExists = false;
      return;
    }
    let periodType = '';
    let period = '';

    switch (this.selectedCategory.name) {
      case 'Monthly Statistics':
        periodType = 'MONTH';
        // Format: YYYY-MM
        period = `${this.filters.year}-${this.filters.month.toString().padStart(2, '0')}`;
        break;
      case 'Quarterly Statistics':
        periodType = 'QUARTER';
        // Format: YYYY-Q#
        period = `${this.filters.year}-Q${this.filters.quarter}`;
        break;
      case 'Yearly Statistics':
        periodType = 'YEAR';
        // Format: YYYY
        period = `${this.filters.year}`;
        break;
      default:
        console.error('Unknown category type');
        this.reportExists = false;
        return;
    }

    this.reportService.checkReportExists(periodType, period)
      .pipe(finalize(() => {
      
      }))
      .subscribe({
        next: (exists) => {
          this.reportExists = exists && this.selectedReportType=="professionals";
          console.log(`Report exists: ${exists}`);
         
        },
        error: (error) => {
          console.error('Error checking if report exists:', error);
          this.reportExists = false;
        }
      });
  }

  generateReport() {
    if (!this.selectedCategory || !this.selectedReport) {
      console.error('No category or report selected');
      return;
    }
  
    this.isGenerating = true;
  
    // Prepare parameters based on selected category
    const params: any = {};
    params.year = this.filters.year;
    
    if (this.selectedCategory.name === 'Monthly Statistics') {
      params.month = this.filters.month;
    } else if (this.selectedCategory.name === 'Quarterly Statistics') {
      params.quarter = this.filters.quarter;
    }
  
    // First generate statistics
    this.reportService.generateStatistics(
      this.selectedCategory.name,
      params
    ).subscribe({
      next: (data) => {
        this.isGenerating = false;
        this.reportData = data;
        
        // Then open the report in a new tab
        this.reportService.getReportAndOpenInNewTab(
          this.selectedCategory.name,
          params,
          this.selectedReport.id
        );
      },
      error: (error) => {
        this.isGenerating = false;
        console.error('Error generating report:', error);
      }
    });
  }

  shouldShowYearFilter(): boolean {
    return this.selectedCategory ;
  }
  getExistingReport() {
    if (!this.selectedCategory || !this.selectedReport || !this.reportExists) {
      console.error('Report does not exist or is not selected');
      return;
    }
    this.isLoading = true;
    const params: any = {};
    params.year = this.filters.year;
    
    if (this.selectedCategory.name === 'Monthly Statistics') {
      params.month = this.filters.month;
    } else if (this.selectedCategory.name === 'Quarterly Statistics') {
      params.quarter = this.filters.quarter;
    }

    this.reportService.getReportAndOpenInNewTab(
      this.selectedCategory.name,
      params,
      this.selectedReport.id
    ).finally(() => {
      this.isLoading = false;
    });
    
  }

}