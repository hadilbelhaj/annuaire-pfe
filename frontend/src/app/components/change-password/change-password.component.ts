import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ResetPasswordService } from '../../services/reset-password.service';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [RouterModule,ReactiveFormsModule,CommonModule],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css'
})
export class ChangePasswordComponent {
  resetForm: FormGroup;
  submitted = false;
  loading = false;
  success = false;
  error = false;
  message = '';
  token: string = '';
  tokenValid = false;
  tokenChecked = false;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private passwordResetService: ResetPasswordService
  ) {
    this.resetForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    }, {
      validator: this.mustMatch('password', 'confirmPassword')
    });
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
      
      if (!this.token) {
        this.tokenValid = false;
        this.tokenChecked = true;
        return;
      }
      
      // Validate token
      this.passwordResetService.validateToken(this.token).subscribe({
        next: () => {
          this.tokenValid = true;
          this.tokenChecked = true;
        },
        error: () => {
          this.tokenValid = false;
          this.tokenChecked = true;
        }
      });
    });
  }

  // Custom validator to check if passwords match
  mustMatch(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];

      if (matchingControl.errors && !matchingControl.errors['mustMatch']) {
        return;
      }

      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ mustMatch: true });
      } else {
        matchingControl.setErrors(null);
      }
    };
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
    
    this.passwordResetService.resetPassword(this.token, this.f['password'].value)
      .subscribe({
        next: (response) => {
          this.success = true;
          console.log(this.message);
          this.loading = false;
        },
        error: (error) => {
          this.error = true;
          this.message = error?.error || 'An error occurred. Please try again.';
          console.log(this.message);
          this.loading = false;
        }
      });
  }

}
