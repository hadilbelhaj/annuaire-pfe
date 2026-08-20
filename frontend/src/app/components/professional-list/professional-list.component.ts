import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { Professional } from '../../models/professional.modal';
import { ProfessionalCardComponent } from '../professional-card/professional-card.component';
import { CommonModule } from '@angular/common';
import { ProfessionalService } from '../../services/professional.service';
import { catchError, finalize, Subscription } from 'rxjs';
import { of } from 'rxjs';
import { SearchBarComponent } from '../search-bar/search-bar.component';
import { Page } from '../../models/page.modal';

@Component({
  selector: 'app-professional-list',
  standalone: true,
  imports: [ProfessionalCardComponent, CommonModule, SearchBarComponent],
  templateUrl: './professional-list.component.html',
  styleUrl: './professional-list.component.css',
})
export class ProfessionalListComponent implements OnInit, OnDestroy {
  professionals: Professional[] = [];
  currentPage = 0;
  pageSize = 10;
  totalItems = 0;
  isLoading = false;
  error: string | null = null;

  // Add properties for search state
  private searchSubscription?: Subscription;
  private lastSearchCriteria: {
    name?: string;
    specialty?: string;
    region?: string;
  } = {};

  constructor(private professionalService: ProfessionalService) {}

  ngOnInit() {
    this.professionalService.lastSearchCriteria$.subscribe((criteria) => {
      this.lastSearchCriteria = criteria;
    });
    this.loadInitialData();
    this.subscribeToSearchResults();
  }

  ngOnDestroy() {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  private loadInitialData() {
    this.loadProfessionals();
  }

  private subscribeToSearchResults() {
    this.searchSubscription = this.professionalService.professionals$.subscribe(
      (page: Page<Professional>) => {
        this.professionals = page.content || []; // Add null check
        this.totalItems = page.totalElements || 0; // Provide default value
        this.currentPage = page.number || 0;
        this.pageSize = page.size || 10;
      }
    );
  }

  loadProfessionals() {
    this.isLoading = true;
    this.error = null;

    const searchMethod = this.getAppropriateSearchMethod();

    searchMethod
      .pipe(
        catchError((error) => {
          this.error = 'Failed to load professionals';
          return of({
            content: [],
            pageable: {
              pageNumber: this.currentPage,
              pageSize: this.pageSize,
              offset: this.currentPage * this.pageSize,
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
            size: this.pageSize,
            number: this.currentPage,
            first: true,
            last: true,
            empty: true,
            numberOfElements: 0,
            sort: {
              empty: true,
              sorted: false,
              unsorted: true,
            },
          } as Page<Professional>);
        }),
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((page) => {
        this.professionals = page.content;

        this.totalItems = page.totalElements;
      });
  }

  private getAppropriateSearchMethod() {
    const { name, specialty, region } = this.lastSearchCriteria;
    if (name && specialty && region) {
      return this.professionalService.getPaginatedProfessionals(
        this.currentPage,
        this.pageSize,
        specialty,
        region,
        name
      );
    } else if (name && region) {
      return this.professionalService.getProfessionalsByNameRegion(
        this.currentPage,
        this.pageSize,
        name,
        region
      );
    } else if (name && specialty) {
      return this.professionalService.getProfessionalsByNameSpecialty(
        this.currentPage,
        this.pageSize,
        name,
        specialty
      );
    } else if (specialty && region) {
      return this.professionalService.getProfessionalsBySpecialtyRegion(
        this.currentPage,
        this.pageSize,
        specialty,
        region
      );
    } else if (name) {
      return this.professionalService.getProfessionalsBypageAndName(
        this.currentPage,
        this.pageSize,
        name
      );
    } else if (specialty) {
      return this.professionalService.getProfessionalsByPageAndSpecialty(
        this.currentPage,
        this.pageSize,
        specialty
      );
    } else if (region) {
      return this.professionalService.getProfessionalsByPageAndRegion(
        this.currentPage,
        this.pageSize,
        region
      );
    } else {
      return this.professionalService.getPaginated(
        this.currentPage,
        this.pageSize
      );
    }
  }

  onPageChange(newPage: number) {
    if (newPage < 0 || newPage >= this.totalPages) return;
    this.currentPage = newPage;
    this.loadProfessionals(); // ✅ Fetch data for the new page
  }

  get totalPages(): number {
    return isNaN(Math.ceil(this.totalItems / this.pageSize))
      ? 0
      : Math.ceil(this.totalItems / this.pageSize);
  }

  retryLoad() {
    this.error = null;
    this.loadProfessionals();
  }
}
