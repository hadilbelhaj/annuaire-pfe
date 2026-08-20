import { Component, OnInit } from '@angular/core';
import { AuthserviceService } from '../../services/authservice.service';
import { UserInfo } from '../../services/authservice.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnInit {
  isLoggedIn$: Observable<boolean>;
  currentUser$: Observable<UserInfo | null>;

  constructor(public router: Router, private authService: AuthserviceService) {
    this.isLoggedIn$ = this.authService.isLoggedIn$;
    this.currentUser$ = this.authService.currentUser$;
  }
  goBack() {
    this.router.navigate(['/']);
  }
  makeSuggestion() {
    this.router.navigate(['/suggestion']);
  }

  ngOnInit(): void {
    // Optional: Log state for debugging
    this.isLoggedIn$.subscribe((isLoggedIn) =>
      console.log('Logged in:', isLoggedIn)
    );
    this.currentUser$.subscribe((user) => console.log('Current user:', user));
  }
  navigateToAuth(isSignUp: boolean): void {
    // Use query params to toggle between signup and signin
    this.router.navigate(['/auth'], {
      queryParams: { mode: isSignUp ? 'signup' : 'signin' },
    });
  }

  navigateToProfile(): void {
    this.router.navigate(['/profile']);
  }
  navigateToAdmin(): void {
    this.router.navigate(['/admin']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
