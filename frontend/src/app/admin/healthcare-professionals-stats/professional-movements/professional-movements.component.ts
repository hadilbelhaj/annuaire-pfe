import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HealthcareProfessionalService } from '../../../services/Stats/healthcare-professional.service';
import { forkJoin } from 'rxjs';
import { Chart } from 'chart.js';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-professional-movements',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './professional-movements.component.html',
  styleUrl: './professional-movements.component.css',
})
export class ProfessionalMovementsComponent implements OnInit {
  professionalId: number = 0;
  loading: boolean = true;
  movements: any[] | null = null;
  stats: any = null;
  monthlyStats: any[] = [];
  adherentStats: any[] = [];
  activeTab: string = 'overview';
  currentYear: number = new Date().getFullYear();

  monthlyChart: any;
  adherentChart: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private healthcareProfessionalService: HealthcareProfessionalService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.professionalId = +params['id'];
      this.fetchData();
    });
  }

  fetchData(): void {
    this.loading = true;
    forkJoin({
      movements: this.healthcareProfessionalService.getProfessionalMovements(
        this.professionalId
      ),
      stats: this.healthcareProfessionalService.getProfessionalMovementStats(
        this.professionalId
      ),
      monthlyStats:
        this.healthcareProfessionalService.getProfessionalMonthlyStats(
          this.professionalId,
          this.currentYear
        ),
      adherentStats:
        this.healthcareProfessionalService.getProfessionalAdherentStats(
          this.professionalId
        ),
    }).subscribe(
      (results) => {
        this.movements = results.movements;
        this.stats = results.stats;
        this.monthlyStats = results.monthlyStats;
        this.adherentStats = results.adherentStats;
        this.loading = false;
        setTimeout(() => {
          this.initializeMonthlyChart();
          this.initializeAdherentChart();
        }, 0);
      },
      (error) => {
        console.error('Error fetching professional data:', error);
        this.loading = false;
      }
    );
  }

  changeYear(change: number): void {
    this.currentYear += change;
    this.healthcareProfessionalService
      .getProfessionalMonthlyStats(this.professionalId, this.currentYear)
      .subscribe(
        (data) => {
          this.monthlyStats = data;
          this.updateMonthlyChart();
        },
        (error) => {
          console.error('Error fetching monthly stats:', error);
        }
      );
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;

    // Initialize charts when tabs are switched
    if (tab === 'overview' && this.monthlyStats.length > 0) {
      setTimeout(() => this.initializeMonthlyChart(), 0);
    }
    if (tab === 'adherents' && this.adherentStats.length > 0) {
      setTimeout(() => this.initializeAdherentChart(), 0);
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';

    const date = new Date(dateString);
    return new Intl.DateTimeFormat('fr-FR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    }).format(date);
  }

  backToList(): void {
    this.router.navigate(['/admin/healthcareprof']);
  }

  initializeMonthlyChart(): void {
    if (this.monthlyChart) {
      this.monthlyChart.destroy();
    }

    const ctx = document.getElementById('monthlyChart') as HTMLCanvasElement;
    if (!ctx) return;

    const labels = this.monthlyStats.map((stat) => stat.month);
    const amountData = this.monthlyStats.map((stat) => stat.totalAmount);
    const countData = this.monthlyStats.map((stat) => stat.transactionCount);

    this.monthlyChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Amount (TND)',
            data: amountData,
            borderColor: '#8884d8',
            backgroundColor: 'rgba(136, 132, 216, 0.2)',
            yAxisID: 'y',
          },
          {
            label: 'Transactions',
            data: countData,
            borderColor: '#82ca9d',
            backgroundColor: 'rgba(130, 202, 157, 0.2)',
            yAxisID: 'y1',
          },
        ],
      },
      options: {
        responsive: true,
        scales: {
          y: {
            type: 'linear',
            display: true,
            position: 'left',
            title: {
              display: true,
              text: 'Amount (TND)',
            },
          },
          y1: {
            type: 'linear',
            display: true,
            position: 'right',
            title: {
              display: true,
              text: 'Count',
            },
            grid: {
              drawOnChartArea: false,
            },
          },
        },
      },
    });
  }

  updateMonthlyChart(): void {
    if (!this.monthlyChart) return;

    const labels = this.monthlyStats.map((stat) => stat.month);
    const amountData = this.monthlyStats.map((stat) => stat.totalAmount);
    const countData = this.monthlyStats.map((stat) => stat.transactionCount);

    this.monthlyChart.data.labels = labels;
    this.monthlyChart.data.datasets[0].data = amountData;
    this.monthlyChart.data.datasets[1].data = countData;
    this.monthlyChart.update();
  }

  initializeAdherentChart(): void {
    if (this.adherentChart) {
      this.adherentChart.destroy();
    }

    const ctx = document.getElementById('adherentChart') as HTMLCanvasElement;
    if (!ctx) return;

    const topAdherents = [...this.adherentStats]
      .sort((a, b) => b.totalAmount - a.totalAmount)
      .slice(0, 10);

    const labels = topAdherents.map((stat) => stat.adherentName);
    const data = topAdherents.map((stat) => stat.totalAmount);

    const backgroundColors = [
      '#0088FE',
      '#00C49F',
      '#FFBB28',
      '#FF8042',
      '#8884d8',
      '#82ca9d',
      '#ffc658',
      '#8dd1e1',
      '#a4de6c',
      '#d0ed57',
    ];

    this.adherentChart = new Chart(ctx, {
      type: 'pie',
      data: {
        labels: labels,
        datasets: [
          {
            data: data,
            backgroundColor: backgroundColors,
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            position: 'right',
          },
          tooltip: {
            callbacks: {
              label: function (context) {
                const value = context.raw as number;
                return `${context.label}: ${value.toFixed(2)} TND`;
              },
            },
          },
        },
      },
    });
  }
  exportReport(): void {
    this.healthcareProfessionalService.downloadMovementsReport(this.professionalId, this.currentYear)
      .subscribe((response) => {
        const blob = new Blob([response], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const newWindow = window.open(url, '_blank');
        const a = document.createElement('a');
        a.href = url;
        a.download = `healthcare_professional_${this.professionalId}_report.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      }, (error) => {
        console.error('Error downloading the report:', error);
      });
  }
}
