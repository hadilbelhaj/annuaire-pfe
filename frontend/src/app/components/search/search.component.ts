import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { ProfessionalService } from '../../services/professional.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Professional } from '../../models/professional.modal';
import { BehaviorSubject, Subscription } from 'rxjs';
import { Page } from '../../models/page.modal';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css',
})
export class SearchComponent implements OnInit, OnDestroy {
  specialties: string[] = [];
  regions: string[] = [];
  selectedSpecialty: string = '';
  selectedRegion: string = '';
  selectedName: string = '';

  currentPage: number = 0;
  pageSize: number = 10;
  isHorizontal = false;

  private subscriptions: Subscription[] = [];

  private professionalsSubject = new BehaviorSubject<Page<Professional>>({
    content: [],
    pageable: {
      pageNumber: 0,
      pageSize: 10,
      offset: 0,
      paged: true,
      unpaged: false,
      sort: {
        empty: true,
        sorted: false,
        unsorted: true,
      },
    },
    totalElements: 0,
    totalPages: 0,
    size: 0,
    number: 0,
    first: true,
    last: true,
    empty: true,
    numberOfElements: 0,
    sort: {
      empty: true,
      sorted: false,
      unsorted: true,
    },
  });
  professionals$ = this.professionalsSubject.asObservable();

  constructor(
    private professionalService: ProfessionalService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Subscribe to specialties and regions
    this.subscriptions.push(
      this.professionalService
        .getUniqueSpecialties()
        .subscribe((specialties) => {
          this.specialties = specialties;
          this.cdr.detectChanges();
        })
    );

    this.subscriptions.push(
      this.professionalService.getUniqueRegions().subscribe((regions) => {
        this.regions = regions.filter(region => region !== null && region.trim() !== ''); 
        this.cdr.detectChanges();
      })
    );
    
    // Listen to route changes for horizontal layout
    this.subscriptions.push(
      this.router.events.subscribe(() => {
        this.isHorizontal = this.router.url.includes('/search-results');
        this.cdr.detectChanges();
      })
    );

    // Check URL parameters on init
    this.route.queryParams.subscribe((params) => {
      if (params) {
        this.selectedName = params['name'] || '';
        this.selectedSpecialty = params['specialty'] || '';
        this.selectedRegion = params['region'] || '';
        if (this.router.url.includes('/search-results')) {
          this.onSearch();
        }
      }
    });
  }

  onSearch() {
    const searchCriteria = {
      name: this.selectedName || undefined,
      specialty: this.selectedSpecialty || undefined,
      region: this.selectedRegion || undefined,
    };

    // Update last search criteria in the service
    this.professionalService.updateLastSearchCriteria(searchCriteria);

    // Check different combinations of search criteria
    if (this.selectedName && this.selectedRegion && this.selectedSpecialty) {
      this.searchWithAllCriteria();
    } else if (this.selectedName && this.selectedRegion) {
      this.searchByNameAndRegion();
    } else if (this.selectedName && this.selectedSpecialty) {
      this.searchByNameAndSpecialty();
    } else if (this.selectedSpecialty && this.selectedRegion) {
      this.searchBySpecialtyAndRegion();
    } else if (this.selectedName) {
      this.searchByName();
    } else if (this.selectedSpecialty) {
      this.searchBySpecialty();
    } else if (this.selectedRegion) {
      this.searchByRegion();
    } else {
      this.searchAll();
    }

    // Navigate to search results
    this.router.navigate(['/search-results'], {
      queryParams: {
        name: this.selectedName || null,
        specialty: this.selectedSpecialty || null,
        region: this.selectedRegion || null,
      },
      queryParamsHandling: 'merge',
    });
  }

  private searchWithAllCriteria() {
    this.professionalService
      .getPaginatedProfessionals(
        this.currentPage,
        this.pageSize,
        this.selectedSpecialty,
        this.selectedRegion,
        this.selectedName
      )
      .subscribe((result) => {
        this.professionalsSubject.next(result);
      });
  }

  private searchByNameAndRegion() {
    this.professionalService
      .getProfessionalsByNameRegion(
        this.currentPage,
        this.pageSize,
        this.selectedName,
        this.selectedRegion
      )
      .subscribe((result) => {
        this.professionalsSubject.next(result);
      });
  }

  private searchByNameAndSpecialty() {
    this.professionalService
      .getProfessionalsByNameSpecialty(
        this.currentPage,
        this.pageSize,
        this.selectedName,
        this.selectedSpecialty
      )
      .subscribe((result) => {
        this.professionalsSubject.next(result);
      });
  }

  private searchBySpecialtyAndRegion() {
    this.professionalService
      .getProfessionalsBySpecialtyRegion(
        this.currentPage,
        this.pageSize,
        this.selectedSpecialty,
        this.selectedRegion
      )
      .subscribe((result) => {
        this.professionalsSubject.next(result);
      });
  }

  private searchByName() {
    this.professionalService
      .getProfessionalsBypageAndName(
        this.currentPage,
        this.pageSize,
        this.selectedName
      )
      .subscribe((result) => {
        this.professionalsSubject.next(result);
      });
  }

  private searchBySpecialty() {
    this.professionalService
      .getProfessionalsByPageAndSpecialty(
        this.currentPage,
        this.pageSize,
        this.selectedSpecialty
      )
      .subscribe((result) => {
        this.professionalsSubject.next(result);
      });
  }

  private searchByRegion() {
    this.professionalService
      .getProfessionalsByPageAndRegion(
        this.currentPage,
        this.pageSize,
        this.selectedRegion
      )
      .subscribe((result) => {
        this.professionalsSubject.next(result);
      });
  }

  private searchAll() {
    this.professionalService
      .getPaginated(this.currentPage, this.pageSize)
      .subscribe((result) => {
        this.professionalsSubject.next(result);
      });
  }

  // Reset search fields
  resetSearch() {
    this.selectedSpecialty = '';
    this.selectedRegion = '';
    this.selectedName = '';
    this.currentPage = 0;

    // Update the service with reset criteria
    this.professionalService.updateLastSearchCriteria({
      name: undefined,
      specialty: undefined,
      region: undefined,
    });

    this.onSearch();
  }

  ngOnDestroy() {
    // Clean up subscriptions
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }
}
