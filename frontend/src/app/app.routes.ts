import { Routes } from '@angular/router';
import { ProfessionalListComponent } from './components/professional-list/professional-list.component';
import { HomeComponent } from './pages/home/home.component';
import { SearchResultsComponent } from './components/search-results/search-results.component';
import { AuthComponent } from './components/auth/auth.component';
import { AdminModule } from './admin/admin.module';
import { ProfileComponent } from './components/profile/profile.component';
import { HealthcareProfessionalsComponent } from './admin/healthcare-professionals-stats/healthcare-professionals/healthcare-professionals.component';
import { PasswordResetComponent } from './components/password-reset/password-reset.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { SuggestionFormComponent } from './components/suggestion-form/suggestion-form.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'professionals', component: ProfessionalListComponent },
  { path: 'search-results', component: SearchResultsComponent },
  { path: 'auth', component: AuthComponent },
  {
    path: 'admin',
    loadChildren: () =>
      import('./admin/admin.module').then((m) => m.AdminModule),
  },
  { path: 'profile', component: ProfileComponent },
  { path: 'stats', component: HealthcareProfessionalsComponent },
  { path: 'resetPasswordemail', component: PasswordResetComponent },
  { path: 'reset-password', component: ChangePasswordComponent },
  {path:'suggestion',component:SuggestionFormComponent}
];
