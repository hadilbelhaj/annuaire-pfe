import { Component } from '@angular/core';
import { Suggestion, SUGGESTION_CATEGORIES, SuggestionService } from '../../services/suggestion.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-suggestion-form',
  standalone: true,
  imports: [FormsModule,CommonModule,ReactiveFormsModule],
  templateUrl: './suggestion-form.component.html',
  styleUrl: './suggestion-form.component.css'
})
export class SuggestionFormComponent {
  suggestionForm: FormGroup;
  categories = SUGGESTION_CATEGORIES;
  isSubmitting = false;
  formError = '';
  submitted = false;
  submitSuccess = false;

  constructor(
    private fb: FormBuilder,
    private suggestionService: SuggestionService,
    private router: Router
  ) {
    this.suggestionForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(20), Validators.maxLength(1000)]],
      category: ['Healthcare Provider Search', Validators.required]
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    this.submitted = true;
    
    if (this.suggestionForm.invalid) {
      return;
    }
    
    this.isSubmitting = true;
    this.formError = '';
    
    const suggestion: Suggestion = {
      title: this.suggestionForm.value.title,
      description: this.suggestionForm.value.description,
      category: this.suggestionForm.value.category
    };
    
    this.suggestionService.createSuggestion(suggestion).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        this.submitSuccess = true; 

      },
      error: (error) => {
        this.isSubmitting = false;
        this.formError = error.error?.message || 'Failed to submit suggestion. Please try again.';
      }
    });
  }

  get f() {
    return this.suggestionForm.controls;
  }
  
  reset(): void {
    this.suggestionForm.reset({
      category: 'Healthcare Provider Search'
    });
    this.submitted = false;
  }
  goToHomePage(): void {
    this.router.navigate(['/']);
  }

}
