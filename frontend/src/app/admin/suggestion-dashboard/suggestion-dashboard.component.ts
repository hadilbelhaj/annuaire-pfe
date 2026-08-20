import { Component, OnInit } from '@angular/core';
import { Suggestion, SuggestionService, SuggestionStatus } from '../../services/suggestion.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs';
@Component({
  selector: 'app-suggestion-dashboard',
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule,FormsModule],
  templateUrl: './suggestion-dashboard.component.html',
  styleUrl: './suggestion-dashboard.component.css'
})
export class SuggestionDashboardComponent implements OnInit {
  suggestions: Suggestion[] = [];
  filteredSuggestions: Suggestion[] = [];
  selectedSuggestion: Suggestion | null = null;
  showUpdateModal = false;
  updateForm: FormGroup;
  statusOptions = Object.values(SuggestionStatus);
  statusFilter: string = '';
  categoryFilter: string = '';
  searchTerm: string = '';
  isLoading = true;
  isSubmitting = false;
  showDeleteModal = false;
  
  // Toast notification
  showToast = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  SUGGESTION_CATEGORIES = [
    'Healthcare Provider Search',
    'Nearest Provider Locator',
    'Appointment Booking',
    'User Interface',
    'User Experience',
    'Mobile App',
    'Security',
    'Accessibility',
    'Other'
  ];

  constructor(
    public suggestionService: SuggestionService,
    private fb: FormBuilder
  ) {
    this.updateForm = this.fb.group({
      status: [''],
      adminFeedback: ['']
    });
  }

  ngOnInit(): void {
    this.loadSuggestions();
  }

  deleteSuggestion(): void {
    if (!this.selectedSuggestion || !this.selectedSuggestion.id) return;
    
    this.isSubmitting = true;
    this.suggestionService.deleteSuggestion(this.selectedSuggestion.id)
      .pipe(finalize(() => this.isSubmitting = false))
      .subscribe({
        next: () => {
          // Remove the suggestion from the array
          this.suggestions = this.suggestions.filter(s => s.id !== this.selectedSuggestion?.id);
          this.applyFilters();
          this.closeModals();
          this.showNotification('Suggestion deleted successfully', 'success');
        },
        error: (error) => {
          console.error('Error deleting suggestion', error);
          this.showNotification('Failed to delete suggestion', 'error');
        }
      });
  }

  showNotification(message: string, type: 'success' | 'error'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;
    
    // Auto-hide the toast after 3 seconds
    setTimeout(() => {
      this.showToast = false;
    }, 3000);
  }
  
  openDeleteModal(suggestion: Suggestion): void {
    this.selectedSuggestion = suggestion;
    this.showDeleteModal = true;
  }

  closeModals(): void {
    this.showUpdateModal = false;
    this.showDeleteModal = false;
    this.selectedSuggestion = null;
  }
  loadSuggestions(): void {
    this.suggestionService.getAllSuggestions().subscribe({
      next: (data) => {
        this.suggestions = data;
        this.isLoading=false;
        this.applyFilters();
      },
      error: (error) => {
        console.error('Error fetching suggestions', error);
      }
    });
  }

  applyFilters(): void {
    this.filteredSuggestions = this.suggestions.filter(suggestion => {
      // Apply status filter
      if (this.statusFilter && suggestion.status !== this.statusFilter) {
        return false;
      }
      
      // Apply category filter
      if (this.categoryFilter && suggestion.category !== this.categoryFilter) {
        return false;
      }
      
      // Apply search term (on title and description)
      if (this.searchTerm) {
        const searchLower = this.searchTerm.toLowerCase();
        return suggestion.title.toLowerCase().includes(searchLower) || 
               suggestion.description.toLowerCase().includes(searchLower);
      }
      
      return true;
    });
  }

  resetFilters(): void {
    this.statusFilter = '';
    this.categoryFilter = '';
    this.searchTerm = '';
    this.applyFilters();
  }

  openUpdateModal(suggestion: Suggestion): void {
    this.selectedSuggestion = suggestion;
    this.updateForm.patchValue({
      status: suggestion.status || SuggestionStatus.PENDING,
      adminFeedback: suggestion.adminFeedback || ''
    });
    this.showUpdateModal = true;
  }

  closeModal(): void {
    this.showUpdateModal = false;
    this.selectedSuggestion = null;
  }

  updateSuggestionStatus(): void {
    if (!this.selectedSuggestion || !this.selectedSuggestion.id) return;
    
    const updatedSuggestion: Suggestion = {
      ...this.selectedSuggestion,
      status: this.updateForm.value.status,
      adminFeedback: this.updateForm.value.adminFeedback
    };
    
    this.suggestionService.updateSuggestionStatus(this.selectedSuggestion.id, updatedSuggestion).subscribe({
      next: (response) => {
        // Update the suggestion in the array
        const index = this.suggestions.findIndex(s => s.id === this.selectedSuggestion?.id);
        if (index !== -1) {
          this.suggestions[index] = response;
          this.applyFilters();
        }
        this.closeModal();
      },
      error: (error) => {
        console.error('Error updating suggestion status', error);
      }
    });
  }

  getStatusClass(status: SuggestionStatus | undefined): string {
    if (!status) return 'status-pending';
    
    switch(status) {
      case SuggestionStatus.PENDING:
        return 'status-pending';
      case SuggestionStatus.UNDER_REVIEW:
        return 'status-review';
      case SuggestionStatus.APPROVED:
        return 'status-approved';
      case SuggestionStatus.IMPLEMENTED:
        return 'status-implemented';
      case SuggestionStatus.REJECTED:
        return 'status-rejected';
      default:
        return '';
    }
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString();
  }
  

}
