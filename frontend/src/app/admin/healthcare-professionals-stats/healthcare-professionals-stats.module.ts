import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NgChartsModule } from 'ng2-charts';
import { SpecialtyChartComponenComponent } from './specialty-chart-componen/specialty-chart-componen.component';
import { RegionMapComponent } from './region-map/region-map.component';
import { TopProfessionalsComponent } from './top-professionals/top-professionals.component';
import { NgModel } from '@angular/forms';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,SpecialtyChartComponenComponent,RegionMapComponent,TopProfessionalsComponent,
  ],
})
export class HealthcareProfessionalsStatsModule {}
