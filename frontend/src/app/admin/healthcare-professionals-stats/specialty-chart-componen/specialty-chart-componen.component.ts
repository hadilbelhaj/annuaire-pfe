import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { Chart, ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { SpecialtyDistribution } from '../../../models/Stats/healthcare-professional-stats.model';
import { NgChartsModule } from 'ng2-charts';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-specialty-chart-componen',
  standalone: true,
  imports: [NgChartsModule, CommonModule],
  templateUrl: './specialty-chart-componen.component.html',
  styleUrl: './specialty-chart-componen.component.css'
})
export class SpecialtyChartComponenComponent implements OnChanges {
  @Input() data: SpecialtyDistribution[] = [];
  
  // Configuration du graphique en donut
  public doughnutChartType: ChartType = 'doughnut';
  public doughnutChartData: ChartData<'doughnut'> = {
    labels: [],
    datasets: [
      {
        data: [],
        backgroundColor: []
      }
    ]
  };
  
  // Define the options with a type assertion or interface extension
  public doughnutChartOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'right',
        labels: {
          boxWidth: 15,
          padding: 15,
          font: {
            size: 12
          },
          // Enable text wrapping
          generateLabels: function(chart: any) {
            const original = Chart.overrides.doughnut.plugins.legend.labels.generateLabels;
            const labels = original.call(this, chart);
            
            // Limit label length to prevent cutoff
            labels.forEach(label => {
              if (label.text && label.text.length > 20) {
                label.text = label.text.substring(0, 20) + '...';
              }
            });
            return labels;
          }
        },
        // Adjust legend layout for better fit
        align: 'start',
        maxWidth: 200,
        maxHeight: 350
      },
      tooltip: {
        callbacks: {
          label: function(context: any) {
            const label = context.label || '';
            const value = context.raw as number;
            return `${label}: ${value.toFixed(1)}%`;
          }
        }
      }
    },
    cutout: '65%',
    layout: {
      padding: {
        right: 50 // Add more padding on the right for the legend
      }
    }
  };
  
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data'] && this.data) {
      this.updateChartData();
    }
  }
  
  private updateChartData(): void {
    // On limite à 7 spécialités maximum, le reste est regroupé dans "Autres"
    const topSpecialties = [...this.data]
      .sort((a, b) => b.percentage - a.percentage)
      .slice(0, 7);
    
    let otherPercentage = 0;
    if (this.data.length > 7) {
      otherPercentage = this.data
        .slice(7)
        .reduce((sum, current) => sum + current.percentage, 0);
    }
    
    // Préparation des données pour le graphique
    const labels = topSpecialties.map(item => item.specialtyName);
    const percentages = topSpecialties.map(item => item.percentage);
    
    // Ajout de la catégorie "Autres" si nécessaire
    if (otherPercentage > 0) {
      labels.push('Autres');
      percentages.push(otherPercentage);
    }
    
    // Générer des couleurs
    const colors = this.generateColors(labels.length);
    
    // Mise à jour des données du graphique
    this.doughnutChartData = {
      labels: labels,
      datasets: [
        {
          data: percentages,
          backgroundColor: colors
        }
      ]
    };
  }
  
  private generateColors(count: number): string[] {
    const colorPalette = [
      '#3498db', '#2980b9', '#1abc9c', '#16a085', '#2ecc71', 
      '#27ae60', '#f1c40f', '#f39c12', '#e67e22', '#d35400'
    ];
    
    const colors: string[] = [];
    for (let i = 0; i < count; i++) {
      colors.push(colorPalette[i % colorPalette.length]);
    }
    
    return colors;
  }
}