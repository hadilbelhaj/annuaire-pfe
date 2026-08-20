import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, take, switchMap, finalize } from 'rxjs/operators';
import { inject } from '@angular/core';
import { AuthserviceService } from '../services/authservice.service';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>, 
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  // Only intercept the proximity search endpoint
  if (req.url.includes('/search/user') || req.url.includes('/api/users')|| req.url.includes('/api/suggestions')){
    console.log('Intercepting proximity search request:', req.url);
    
    const authToken = localStorage.getItem('authToken');
    console.log('Auth token exists:', !!authToken);

    if (authToken) {
      const authReq = addTokenToRequest(req, authToken);
      console.log('Adding auth token to proximity search request');
      return next(authReq);
    }
  }
  
  // For all other requests, just pass through without modifying
  return next(req);
};

function addTokenToRequest(request: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  console.log('Adding token to request:', token.substring(0, 10) + '...');
  return request.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
}

function shouldSkipAuthInterceptor(url: string): boolean {
  const skippedUrls = [
    '/auth/login',
    '/auth/register'
  ];
    
  return skippedUrls.some(skipUrl => url.includes(skipUrl));
}

// Implement handle401Error as needed for your app
function handle401Error(request: HttpRequest<unknown>, next: HttpHandlerFn, authService: AuthserviceService): Observable<HttpEvent<unknown>> {
  // Your token refresh logic here
  console.log('Handling 401 error');
  // For now, just logout and throw error
  authService.logout();
  return throwError(() => new Error('Session expired'));
}