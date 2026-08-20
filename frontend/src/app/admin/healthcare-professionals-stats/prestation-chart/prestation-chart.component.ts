import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { Prestation } from '../../../services/Stats/prestation-stats.service';


Chart.register(...registerables);

@Component({
  selector: 'app-prestation-chart',
  standalone: true,
  imports: [],
  templateUrl: './prestation-chart.component.html',
  styleUrl: './prestation-chart.component.css'
})
export class PrestationChartComponent {

  @Input() data: Prestation[] = [];
  chart: any;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data'] && this.data && this.data.length > 0) {
      this.renderChart();
    }
  }

  renderChart(): void {
    // Destroy previous chart instance if it exists
    if (this.chart) {
      this.chart.destroy();
    }

    const ctx = document.getElementById('prestationChart') as HTMLCanvasElement;
    if (!ctx) return;

    this.chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: this.data.map(p => p.prestationName),
        datasets: [
          {
            label: 'Frequency',
            data: this.data.map(p => p.frequency),
            backgroundColor: 'rgba(59, 130, 246, 0.6)',
            borderColor: 'rgba(59, 130, 246, 1)',
            borderWidth: 1,
            barPercentage: 0.6,
            categoryPercentage: 0.7
          },
          {
            label: 'Total Revenue (TND)',
            data: this.data.map(p => p.totalRevenue),
            backgroundColor: 'rgba(16, 185, 129, 0.6)',
            borderColor: 'rgba(16, 185, 129, 1)',
            borderWidth: 1,
            yAxisID: 'y1',
            barPercentage: 0.6,
            categoryPercentage: 0.7
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top',
            labels: {
              usePointStyle: true,
              padding: 20,
              font: {
                size: 12
              }
            }
          },
          tooltip: {
            backgroundColor: 'rgba(255, 255, 255, 0.9)',
            titleColor: '#1f2937',
            bodyColor: '#4b5563',
            borderColor: '#e5e7eb',
            borderWidth: 1,
            padding: 12,
            boxPadding: 6,
            usePointStyle: true,
            callbacks: {
              label: function(context) {
                const label = context.dataset.label || '';
                const value = context.raw;
                return `${label}: ${context.datasetIndex === 1 ? 'TND' : ''}${value}`;
              }
            }
          }
        },
        scales: {
          x: {
            grid: {
              display: false
            },
            ticks: {
              font: {
                size: 12
              }
            }
          },
          y: {
            beginAtZero: true,
            title: {
              display: true,
              text: 'Frequency',
              font: {
                size: 12,
                weight: 'bold'
              }
            },
            grid: {
              color: 'rgba(243, 244, 246, 1)'
            },
            ticks: {
              font: {
                size: 11
              }
            }
          },
          y1: {
            position: 'right',
            beginAtZero: true,
            title: {
              display: true,
              text: 'Revenue (TND)',
              font: {
                size: 12,
                weight: 'bold'
              }
            },
            grid: {
              drawOnChartArea: false
            },
            ticks: {
              font: {
                size: 11
              },
              callback: function(value) {
                return 'TND' + value;
              }
            }
          }
        }
      }
    });
  }
}
