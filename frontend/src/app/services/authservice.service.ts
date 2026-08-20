import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {
  catchError,
  Observable,
  throwError,
  tap,
  BehaviorSubject,
  map,
  switchMap,
  of,
} from 'rxjs';
import { User } from '../models/user';
import { jwtDecode } from 'jwt-decode';
import { isPlatformBrowser } from '@angular/common';


export interface LoginResponse {
  token: string;
  refreshToken?: string; 
}

export interface TokenRefreshRequest {
  refreshToken: string;
}

export interface TokenRefreshResponse {
  accessToken: string;
  refreshToken: string;
}

export interface UserInfo {
  email: string;
  roles: string[];
}

@Injectable({
  providedIn: 'root',
})
export class AuthserviceService {
  private apiUrl = 'http://localhost:8090/api/auth';

  private currentUserSubject = new BehaviorSubject<UserInfo | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  public isLoggedIn$ = this.isLoggedInSubject.asObservable();

  private refreshingToken = false;
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);

  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
    if (this.isBrowser) {
      this.isLoggedInSubject.next(this.hasValidToken());
      this.loadUserFromToken();
    }
  }

  private loadUserFromToken(): void {
    if (!this.isBrowser) return;

    const token = this.getToken();
    if (token) {
      try {
        const decodedToken: any = jwtDecode(token);
        const user: UserInfo = {
          email: decodedToken.sub,
          roles: decodedToken.roles || [],
        };
        this.currentUserSubject.next(user);
        this.isLoggedInSubject.next(true);
      } catch (error) {
        this.logout();
      }
    }
  }

  private hasValidToken(): boolean {
    if (!this.isBrowser) return false;

    const token = this.getToken();
    if (!token) return false;
    try {
      const decodedToken: any = jwtDecode(token);
      const currentTime = Date.now() / 1000;
      return decodedToken.exp > currentTime;
    } catch {
      return false;
    }
  }

  register(user: User): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/register`, user, { responseType: 'text' })
      .pipe(
        catchError((error) => {
          console.error('Registration error:', error);
          return throwError(() => error);
        })
      );
  }

  login(user: User): Observable<LoginResponse> {
    const loginRequest = { email: user.email, password: user.password };
    return this.http
      .post<any>(`${this.apiUrl}/login`, loginRequest, {
        headers: { 'Content-Type': 'application/json' },
      })
      .pipe(
        tap((response) => {
          if (response && response.token && this.isBrowser) {
            this.saveToken(response.token);
            
        
            if (response.refreshToken) {
              this.saveRefreshToken(response.refreshToken);
            }
            
            const decodedToken: any = jwtDecode(response.token);
            const loggedInUser: UserInfo = {
              email: decodedToken.sub,
              roles: decodedToken.roles || [],
            };
            this.currentUserSubject.next(loggedInUser);
            this.isLoggedInSubject.next(true);
          }
        }),
        catchError((error) => {
          console.error('Login error:', error);
          return throwError(() => error);
        })
      );
  }

  saveToken(token: string): void {
    if (this.isBrowser) {
      localStorage.setItem('authToken', token);
    }
  }

  getToken(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem('authToken');
    }
    return null;
  }

  
  saveRefreshToken(token: string): void {
    if (this.isBrowser) {
      localStorage.setItem('refreshToken', token);
    }
  }

  getRefreshToken(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem('refreshToken');
    }
    return null;
  }

  refreshToken(): Observable<TokenRefreshResponse> {
    if (!this.isBrowser) {
      return throwError(() => new Error('Not in browser environment'));
    }

    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    if (this.refreshingToken) {
      return this.refreshTokenSubject.pipe(
        map(token => {
          if (token === null) {
            throw new Error('Refresh token is null');
          }
          return { accessToken: this.getToken()!, refreshToken: token };
        })
      );
    }

    this.refreshingToken = true;
    this.refreshTokenSubject.next(null);

    return this.http
      .post<TokenRefreshResponse>(`${this.apiUrl}/refreshtoken`, { refreshToken })
      .pipe(
        tap((response) => {
          this.saveToken(response.accessToken);
          this.saveRefreshToken(response.refreshToken);
          this.refreshingToken = false;
          this.refreshTokenSubject.next(response.refreshToken);
          
          // Update user info from the new token
          const decodedToken: any = jwtDecode(response.accessToken);
          const loggedInUser: UserInfo = {
            email: decodedToken.sub,
            roles: decodedToken.roles || [],
          };
          this.currentUserSubject.next(loggedInUser);
          this.isLoggedInSubject.next(true);
        }),
        catchError((error) => {
          this.refreshingToken = false;
          this.logout();
          return throwError(() => error);
        })
      );
  }

  isTokenExpired(): boolean {
    if (!this.isBrowser) return true;

    const token = this.getToken();
    if (!token) return true;
    
    try {
      const decodedToken: any = jwtDecode(token);
      const currentTime = Date.now() / 1000;
      // Consider token expired if less than 30 seconds left
      return decodedToken.exp < (currentTime + 30);
    } catch {
      return true;
    }
  }

  getRoles(): string[] {
    if (!this.isBrowser) return [];

    const token = this.getToken();
    if (token) {
      const decodedToken: any = jwtDecode(token);
      return decodedToken.roles || [];
    }
    return [];
  }

  getCurrentUser(): UserInfo | null {
    return this.currentUserSubject.value;
  }

  getUserEmail(): string {
    const user = this.getCurrentUser();
    return user ? user.email : '';
  }

  isAuthenticated(): boolean {
    return this.hasValidToken();
  }

  isAdmin(): boolean {
    return this.getRoles().includes('admin');
  }
  isSuperAdmin(): boolean {
    return this.getRoles().includes('super-admin');
  }

  logout(): void {
    if (this.isBrowser) {
      const email = this.getUserEmail();
      if (email) {
        
        this.http.post(`${this.apiUrl}/logout`, { email })
          .subscribe({
            next: () => console.log('Logged out successfully on server'),
            error: (err) => console.error('Error logging out on server:', err)
          });
      }
      
      localStorage.removeItem('authToken');
      localStorage.removeItem('refreshToken');
    }
    this.currentUserSubject.next(null);
    this.isLoggedInSubject.next(false);
  }

  getUserProfile(): Observable<User> {
    const currentUserEmail = this.getUserEmail();
    if (!currentUserEmail) {
      return throwError(() => new Error('No authenticated user'));
    }
    return this.http.get<User[]>(`${this.apiUrl}/all`).pipe(
      map((users) => users.find((user) => user.email === currentUserEmail)),
      map((user) => {
        if (!user) {
          throw new Error('User not found');
        }
        return user;
      }),
      catchError((error) => {
        console.error('Error fetching user profile:', error);
        return throwError(() => error);
      })
    );
  }

  updateProfile(
    firstName: string,
    lastName: string,
    address: string
  ): Observable<User> {
    return this.getUserProfile().pipe(
      switchMap((user) => {
        if (!user || !user.id) {
          return throwError(() => new Error('User ID not found'));
        }

        
        let params = new HttpParams();
        if (firstName) params = params.set('firstName', firstName);
        if (lastName) params = params.set('lastName', lastName);
        if (address) params = params.set('address', address);

        
        return this.http
          .put<User>(`${this.apiUrl}/${user.id}/profile`, null, { params })
          .pipe(
            tap((updatedUser) => console.log('Profile updated:', updatedUser)),
            catchError((error) => {
              console.error('Error updating profile:', error);
              return throwError(() => error);
            })
          );
      })
    );
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/all`);
  }
  geocodeUser(userId: number): Observable<boolean> {
    return this.http.put<string>(`${this.apiUrl}/geoCode/${userId}`, {}).pipe(
      map(response => true),
      catchError(error => {
        console.error('Geocoding error:', error);
        return of(false);
      })
    );
  }
  geocodeAddress(address:string):Observable<number[]>{
    return this.http.get<number[]>(`${this.apiUrl}/geoCode`, {
      params: new HttpParams().set('adress', address)});
  }
  
}