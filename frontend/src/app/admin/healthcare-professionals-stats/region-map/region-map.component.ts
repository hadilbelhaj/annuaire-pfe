import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { RegionDistribution } from '../../../models/Stats/healthcare-professional-stats.model';
import { CommonModule } from '@angular/common';
import { NgChartsModule } from 'ng2-charts';
import { TopProfessionalsComponent } from '../top-professionals/top-professionals.component';

@Component({
  selector: 'app-region-map',
  standalone: true,
  imports: [CommonModule,NgChartsModule],
  templateUrl: './region-map.component.html'
})
export class RegionMapComponent implements OnChanges {
  @Input() data: RegionDistribution[] = [];
  
  // Utilisation d'un graphique à barres horizontales pour représenter les régions
  public barChartType: ChartType = 'bar';
  public barChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      {
        data: [],
        backgroundColor: '#3498db',
        label: 'Pourcentage'
      }
    ]
  };

  public barChartOptions: ChartConfiguration['options'] = {
    indexAxis: 'y',  // Pour avoir des barres horizontales
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {
        min: 0,
        max: 100,
        ticks: {
          callback: function(value) {
            return value + '%';
          }
        }
      }
    },
    plugins: {
      legend: {
        display: false,
      },
      tooltip: {
        callbacks: {
          label: function(context) {
            const label = context.dataset.label || '';
            const value = context.raw as number;
            return `${label}: ${value.toFixed(1)}%`;
          }
        }
      }
    }
  };

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data'] && this.data) {
      this.updateChartData();
    }
  }

  private updateChartData(): void {
    // On trie les régions par pourcentage décroissant et on limite à 10 régions
    const topRegions = [...this.data]
      .sort((a, b) => b.percentage - a.percentage)
      .slice(0, 10);
    
    const labels = topRegions.map(item => item.regionName);
    const percentages = topRegions.map(item => item.percentage);
    
    this.barChartData = {
      labels: labels,
      datasets: [
        {
          data: percentages,
          backgroundColor: '#3498db',
          label: 'Pourcentage'
        }
      ]
    };
  }
}