import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ResetPasswordService } from '../../services/reset-password.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-password-reset',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule,RouterModule],
  templateUrl: './password-reset.component.html',
  styleUrl: './password-reset.component.css'
})
export class PasswordResetComponent {

  resetForm: FormGroup;
  submitted = false;
  loading = false;
  success = false;
  error = false;
  message = '';
  email='';

  constructor(
    private formBuilder: FormBuilder,
    private passwordResetService: ResetPasswordService
  ) {
    this.resetForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  get f() { return this.resetForm.controls; }

  onSubmit() {
    this.submitted = true;
    this.success = false;
    this.error = false;
    this.message = '';

    if (this.resetForm.invalid) {
      return;
    }

    this.loading = true;
    
    this.passwordResetService.forgotPassword(this.f['email'].value)
      .subscribe({
        next: (response) => {
          this.success = true;
          this.message = response;
          this.loading = false;
        },
        error: (error) => {
          this.error = true;
          this.message = 'An error occurred. Please try again.';
          this.loading = false;
          console.error('Error:', error);
        }
      });
  }}
