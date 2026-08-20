import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Professional } from './models/professional.modal';
import { ProfessionalService } from './services/professional.service';
import { FooterComponent } from './components/footer/footer.component';
import { HeaderComponent } from './components/header/header.component';
import { ProfessionalListComponent } from './components/professional-list/professional-list.component';
import { SearchBarComponent } from './components/search-bar/search-bar.component';
import { CommonModule, NgClass } from '@angular/common';
import { HomeComponent } from './pages/home/home.component';
import { MapComponent } from './components/map/map.component';

import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AdminModule } from './admin/admin.module';
import { DashboardComponent } from './admin/dashboard/dashboard.component';
import { ProximitySearchComponent } from './components/proximity-search/proximity-search.component';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    FooterComponent,
    HeaderComponent,
    ProfessionalListComponent,
    SearchBarComponent,
    CommonModule,
    HomeComponent,MapComponent,ReactiveFormsModule,NgClass,HttpClientModule,DashboardComponent,ProximitySearchComponent,
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'annuaire';
 
}
