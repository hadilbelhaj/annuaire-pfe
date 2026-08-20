  import { Component, OnInit } from '@angular/core';
  import { AuthserviceService } from '../../services/authservice.service';
  import { UserInfo } from '../../services/authservice.service';
  import { CommonModule } from '@angular/common';
  import { User } from '../../models/user';
  import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Suggestion, SuggestionService } from '../../services/suggestion.service';
  @Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css'],
    imports: [CommonModule,ReactiveFormsModule], // Add this for NgIf and async pipe
    standalone: true
  })
  export class ProfileComponent implements OnInit {
    user: User = {
      id: 0,
      email: '',
      firstName: '',
      lastName: '',
      address: '',
      password: '',
      role: ''
    };

      mySuggestions: Suggestion[] = [];
  loadingSuggestions = true;
  suggestionError: string | null = null;
    profileForm!: FormGroup;
    isEditMode = false;
    loading = true;
    error: string | null = null;
    originalValues: any = {};

    constructor(
      private authService: AuthserviceService,
      private fb: FormBuilder,private router: Router,   private suggestionService: SuggestionService
    ) {
      this.profileForm = this.fb.group({
        firstName: [''],
        lastName: [''],
        address: ['']
      });
    }

    ngOnInit(): void {
      this.loadUserProfile();
      this.loadUserSuggestions();
    }
    loadUserSuggestions(): void {
      this.loadingSuggestions = true;
      this.suggestionError = null;
      
      this.suggestionService.getMySuggestions().subscribe(
        (suggestions) => {
          this.mySuggestions = suggestions;
          this.loadingSuggestions = false;
        },
        (err) => {
          this.suggestionError = 'Failed to load your suggestions. Please try again later.';
          this.loadingSuggestions = false;
          console.error('Error loading user suggestions:', err);
        }
      );
    }
    viewSuggestionDetails(suggestion: Suggestion): void {
      this.router.navigate(['/suggestion', suggestion.id]);
    }
  
    navigateToNewSuggestion(): void {
      this.router.navigate(['/suggestion']);
    }

    loadUserProfile(): void {
      this.loading = true;
      this.error = null;

      const userEmail = this.authService.getUserEmail();
      if (userEmail) {
        this.user.email = userEmail;
        
        this.authService.getUserProfile().subscribe({
          next: (userData) => {
            this.user = userData;
            this.updateFormValues();
            this.saveOriginalValues();
            this.loading = false;
          },
          error: (err) => {
            console.error('Failed to load profile', err);
            this.error = 'Failed to load profile data';
            this.loading = false;
            
            // If we can't load the profile, use sample data
            this.user = {
              id: 0,
              email: userEmail,
              firstName: 'Sample',
              lastName: 'User',
              address: '123 Sample St',
              password: '',
              role: ''
            };
            this.updateFormValues();
            this.saveOriginalValues();
          }
        });
      } else {
        this.loading = false;
        this.error = 'User not authenticated';
      }
    }

    updateFormValues(): void {
      this.profileForm.patchValue({
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        address: this.user.address
      });
    }

    saveOriginalValues(): void {
      this.originalValues = {
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        address: this.user.address
      };
    }

    toggleEditMode(): void {
      this.isEditMode = !this.isEditMode;
      if (!this.isEditMode) {
        // When cancelling edit, reset the form to original values
        this.updateFormValues();
      }
    }

    saveChanges(): void {
      if (!this.profileForm.valid) {
        return;
      }

      const formValues = this.profileForm.value;
      console.log('Updating profile with:', formValues);
      this.authService.updateProfile(
        formValues.firstName,
        formValues.lastName,
        formValues.address
      ).subscribe({
        next: (updatedUser) => {
          this.user = updatedUser;
          this.saveOriginalValues();
          this.isEditMode = false;
          this.error = null;
        },
        error: (err: any) => {
          console.error('Failed to update profile', err);
          this.error = 'Failed to update profile. Please try again.';
        }
      });
    }

    cancelEdit(): void {
      this.isEditMode = false;
      // Reset to original values
      this.profileForm.patchValue(this.originalValues);
    }
    goHome() {
      this.router.navigate(['/']);
    }
  }
