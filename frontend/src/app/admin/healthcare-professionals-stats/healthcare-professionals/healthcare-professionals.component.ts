import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { HealthcareprofessionalstatsService } from '../../../services/Stats/healthcareprofessionalstats.service';
import {
  HealthcareProfessionalStats,
  SpecialtyDistribution,
} from '../../../models/Stats/healthcare-professional-stats.model';
import { CommonModule } from '@angular/common';
import { SpecialtyChartComponenComponent } from '../specialty-chart-componen/specialty-chart-componen.component';
import { RegionMapComponent } from '../region-map/region-map.component';
import { TopProfessionalsComponent } from '../top-professionals/top-professionals.component';
import { PslistComponent } from '../../pslist/pslist.component';
import { PrestationStatisticsComponent } from '../prestation-statistics/prestation-statistics.component';

@Component({
  selector: 'app-healthcare-professionals',
  standalone: true,
  imports: [
    CommonModule,
    SpecialtyChartComponenComponent,
    RegionMapComponent,
    TopProfessionalsComponent,
    PslistComponent,
    PrestationStatisticsComponent
  ],
  templateUrl: './healthcare-professionals.component.html',
  styleUrl: './healthcare-professionals.component.css',
})
export class HealthcareProfessionalsComponent implements OnInit {
  loading = true;
  error = false;
  stats: HealthcareProfessionalStats | null = null;
  totalSpecialties = 0;
  mostCommonSpecialty = '';
  activeTab = 'professionals';

  totalItemsInParent: number = 0;

  constructor(private statsService: HealthcareprofessionalstatsService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.error = false;

    this.statsService.getProfessionalStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des statistiques', err);
        this.error = true;
        this.loading = false;
      },
    });
    this.statsService.getSpecialtyDistribution().subscribe({
      next: (specialties) => {
        this.totalSpecialties = specialties.length; 
        this.mostCommonSpecialty = this.getMostCommonSpecialty(specialties);
      },
      error: (err) => {
        console.error(
          'Erreur lors du chargement de la distribution des spécialités',
          err
        );
      },
    });
  }

  private getMostCommonSpecialty(specialties: SpecialtyDistribution[]): string {
    if (specialties.length === 0) return 'Aucune donnée';
    const mostCommon = specialties.reduce((prev, current) =>
      prev.count > current.count ? prev : current
    );

    return mostCommon.specialtyName;
  }

  refreshData(): void {
    this.loadData();
  }
}
