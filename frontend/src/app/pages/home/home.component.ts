import { Component } from '@angular/core';
import { HeaderComponent } from '../../components/header/header.component';
import { FooterComponent } from '../../components/footer/footer.component';
import { SearchBarComponent } from '../../components/search-bar/search-bar.component';
import { ProfessionalListComponent } from '../../components/professional-list/professional-list.component';
import { RouterModule } from '@angular/router';
import { ProximitySearchComponent } from '../../components/proximity-search/proximity-search.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [HeaderComponent,FooterComponent,SearchBarComponent,ProfessionalListComponent,RouterModule,ProximitySearchComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

}
