import { CommonModule, NgClass } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { User } from '../../models/user';
import { AuthserviceService } from '../../services/authservice.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [NgClass, ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css',
})
export class AuthComponent implements OnInit {
  isRightPanelActive = false;
  signUpForm: FormGroup;
  signInForm: FormGroup;

  socialIcons = [
    { icon: 'fab fa-facebook-f', link: '#' },
    { icon: 'fab fa-google-plus-g', link: '#' },
    { icon: 'fab fa-linkedin-in', link: '#' },
  ];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private authService: AuthserviceService,
    private router: Router
  ) {
    this.signUpForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]], // Updated to email with validation
      password: ['', Validators.required],
      role: ['VISITOR', Validators.required], // Default role
    });

    this.signInForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]], // Changed from username to email
      password: ['', Validators.required],
    });
  }

  togglePanel(isSignUp: boolean) {
    this.isRightPanelActive = isSignUp;
  }

  onSignUp() {
    if (this.signUpForm.valid) {
      this.authService.register(this.signUpForm.value).subscribe({
        next: (response) => {
          console.log('Registration successful', response);
          alert('Registration successful! Please log in.');
          this.router.navigate(['/auth'], { queryParams: { mode: 'signin' } }); // Redirect to login
          this.togglePanel(false); // Switch to sign-in panel
        },
        error: (error) => {
          console.error('Registration failed:', error);
          alert('Registration failed. Please try again.');
        },
      });
    }
  }

  onSignIn() {
    if (this.signInForm.valid) {
      const { email, password } = this.signInForm.value; // Changed from username to email
      const user = new User(email, password);
      console.log(user);

      this.authService.login(user).subscribe(
        (response) => {
          console.log('Login successful', response);
          this.authService.saveToken(response.token);

          // Check user roles and redirect
          if (this.authService.isAdmin()) {
            this.router.navigate(['/admin']);
          } else {
            this.router.navigate(['/']);
          }
        },
        (error) => {
          console.error('Login failed:', error);
          alert('Login failed. Please check your credentials.');
        }
      );
    }
  }

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.isRightPanelActive = params['mode'] === 'signup';
    });
  }
}
