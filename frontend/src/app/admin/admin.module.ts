import { Component, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardComponent } from './dashboard/dashboard.component';
import { RouterModule, Routes } from '@angular/router';
import { NgChartsModule } from 'ng2-charts';
import { HealthcareProfessionalsComponent } from './healthcare-professionals-stats/healthcare-professionals/healthcare-professionals.component';
import { UserListComponent } from './user-list/user-list.component';
import { ProfessionalMovementsComponent } from './healthcare-professionals-stats/professional-movements/professional-movements.component';
import { ReportsDashboardComponent } from './reports-dashboard/reports-dashboard.component';

import { MovementStatsComponent } from './movement-stats/main-stats/main-stats.component';
import { FraudDetectionComponent } from './fraud-detection/fraud-detection.component';
import { DeductibleCheckComponent } from './deductible-check/deductible-check.component';
import { ExceededDeductiblesComponent } from './exceeded-deductibles/exceeded-deductibles.component';
import { PrestationFraudComponent } from './prestation-fraud/prestation-fraud.component';
import { FraudDashboardComponent } from './fraud-dashboard/fraud-dashboard.component';
import { Role } from '../models/user';
import { AuthGuardService } from '../guards/auth-guard.service';
import { SuperAdminGuardService } from '../guards/super-admin-guard.service';
import { MovementTableComponent } from '../components/movement-table/movement-table.component';
import { ReimbursementFraudComponent } from './reimbursement-fraud/reimbursement-fraud.component';
import { SuggestionDashboardComponent } from './suggestion-dashboard/suggestion-dashboard.component';


const adminRoutes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    children: [
      { path: '', redirectTo: 'movement', pathMatch: 'full' },
      { path: 'movement', component: MovementTableComponent },
      { path: 'healthcareprof', component: HealthcareProfessionalsComponent },
      { path: 'users', component: UserListComponent,canActivate: [AuthGuardService,SuperAdminGuardService],
        data: { roles: [Role['super-admin']] } },
      {
        path: 'healthcareprof/:id/movements',
        component: ProfessionalMovementsComponent,
      },

      { path: 'reports', component: ReportsDashboardComponent },

      { path: 'movementstats', component: MovementStatsComponent },
      {path:'frauds',component:FraudDetectionComponent},
      {path:"deductible",component:DeductibleCheckComponent},
      {path:"Exceded",component:ExceededDeductiblesComponent},{
        path:"prestation-fraud-detection",component:PrestationFraudComponent
      },
      {path:'Collusion',component:FraudDashboardComponent},
      {path:'reimbursement',component:ReimbursementFraudComponent},
      {path:'userSuggestions',component:SuggestionDashboardComponent}

    ],
  },
];
@NgModule({
  declarations: [],
  imports: [CommonModule, RouterModule.forChild(adminRoutes), NgChartsModule],
  exports: [RouterModule],
})
export class AdminModule {}
