import { Component, OnInit  } from '@angular/core';
import { HeaderComponent } from '../header/header.component';
import { SearchComponent } from '../search/search.component';
import { ProfessionalListComponent } from '../professional-list/professional-list.component';
import { ActivatedRoute, Router } from '@angular/router';
import { MapComponent } from '../map/map.component';
@Component({
  selector: 'app-search-results',
  standalone: true,
  imports: [HeaderComponent,SearchComponent,ProfessionalListComponent,MapComponent],
  templateUrl: './search-results.component.html',
  styleUrl: './search-results.component.css'
})
export class SearchResultsComponent implements OnInit {
  selectedName: string = '';
  selectedSpecialty: string = '';
  selectedRegion: string = '';

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.selectedName = params['name'] || '';
      this.selectedSpecialty = params['specialty'] || '';
      this.selectedRegion = params['region'] || '';
    });
  }
  goBack() {
    this.router.navigate(['/']);
  }
}
