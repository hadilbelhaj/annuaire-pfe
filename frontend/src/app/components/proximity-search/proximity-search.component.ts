import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { switchMap, tap, catchError } from 'rxjs/operators';
import { NearbyProviderDTO, PrestationType, ProximitySearchService,PageResponse } from '../../services/proximity-search.service';
import { AuthserviceService } from '../../services/authservice.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { User } from '../../models/user';

@Component({
  selector: 'app-proximity-search',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule,RouterModule],
  templateUrl: './proximity-search.component.html',
  styleUrl: './proximity-search.component.css'
})
export class ProximitySearchComponent implements OnInit {
  searchPerformed = false;
  searchForm: FormGroup;
  addressForm: FormGroup;
  showAddressForm = false;
  isAuthenticated = false;
  hasUserAddress = false;
  prestationTypes = Object.values(PrestationType);
  searchResults: NearbyProviderDTO[] = [];
  totalResults = 0;
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  isLoading = false;
  errorMessage = '';
  useCurrentAddress = true;

  constructor(
    private fb: FormBuilder,
    private proximitySearchService: ProximitySearchService,
    private authService: AuthserviceService,
    private router: Router
  ) {
    this.searchForm = this.fb.group({
      addressType: ['current'],
      address: ['', Validators.required],
      prestation: [null],
      maxDistance: [10, [Validators.required, Validators.min(1), Validators.max(600)]]
    });

    this.addressForm = this.fb.group({
      address: ['', Validators.required],
      saveAddress: [false]
    });
  }

  ngOnInit(): void {
    this.isAuthenticated = this.authService.isAuthenticated();
   
    this.authService.getUserProfile().subscribe({
      next: (user) => {
        this.hasUserAddress = !!user.address;
        console.log(user.address);
        if (!this.hasUserAddress) {
          this.router.navigate(['/profile']);
        }
        
        // Initialize the form properly based on user having an address
        if (this.hasUserAddress) {
          // If user has address, set addressType to 'current' and clear address validators
          this.searchForm.get('addressType')?.setValue('current');
          this.searchForm.get('address')?.clearValidators();
          this.searchForm.get('address')?.updateValueAndValidity();
        } else {
          // If no address, default to 'new' and keep address required
          this.searchForm.get('addressType')?.setValue('new');
        }
      },
      error: (error) => {
        console.error('Error fetching user profile:', error);
        this.errorMessage = 'Could not load user profile information';
      }
    });
    
    this.searchForm.get('addressType')?.valueChanges.subscribe(value => {
      this.useCurrentAddress = value === 'current';
      if (value === 'new') {
        this.searchForm.get('address')?.setValidators(Validators.required);
      } else {
        this.searchForm.get('address')?.clearValidators();
      }
      this.searchForm.get('address')?.updateValueAndValidity();
    });
  }

  onSearch(): void {
    if (this.searchForm.invalid) return;

    if (!this.isAuthenticated) {
      this.router.navigate(['/auth']);
      return;
    }
    this.isLoading = true;
    this.errorMessage = '';

    const prestation = this.searchForm.get('prestation')?.value;
    const maxDistance = this.searchForm.get('maxDistance')?.value;
    const addressType = this.searchForm.get('addressType')?.value;

    let searchObservable: Observable<PageResponse<NearbyProviderDTO>>;

    if (addressType === 'current' && this.hasUserAddress) {
      searchObservable = this.proximitySearchService.findNearbyProvidersForUser(
        prestation,
        maxDistance,
        this.currentPage,
        this.pageSize
      );
    } else {
      const address = this.searchForm.get('address')?.value;
      
      searchObservable = this.authService.geocodeAddress(address).pipe(
        switchMap(result => {
          if (!result) {
            throw new Error('Could not geocode the provided address.');
          }
          return this.proximitySearchService.findNearbyProvidersByAddress(
            address,
            prestation,
            maxDistance,
            this.isAuthenticated,
            this.currentPage,
            this.pageSize
          );
        })
      );
    }

    searchObservable.subscribe({
      next: (response) => {
        this.searchResults = response.content;
        this.totalResults = response.totalElements;
        this.totalPages = response.totalPages;
        this.searchPerformed = true;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'An error occurred while searching. Please try again.';
        this.searchPerformed = true;
        this.isLoading = false;
        console.error('Search error:', error);
      }
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.onSearch();
  }

  saveUserAddress(): void {
    if (!this.isAuthenticated) {
      this.router.navigate(['/auth']);
      return;
    }
    this.router.navigate(['/profile']);
  }

  showSaveAddressForm(): void {
    if (!this.isAuthenticated) {
      this.router.navigate(['/auth']);
      return;
    }
    this.router.navigate(['/profile']);
  }
}
