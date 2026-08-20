import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map, mergeMap, take } from 'rxjs/operators';
import { AuthserviceService } from '../services/authservice.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuardService {
  constructor(
    private authService: AuthserviceService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const requiredRoles = route.data['roles'] as string[];
    
    return this.authService.isLoggedIn$.pipe(
      take(1),
      mergeMap(isLoggedIn => {
        if (!isLoggedIn) {
          this.router.navigate(['/auth'], { queryParams: { returnUrl: state.url } });
          return of(false);
        }
        if (this.authService.isTokenExpired()) {
          return this.authService.refreshToken().pipe(
            mergeMap(() => {
              return this.checkRoles(requiredRoles, state.url);
            }),
            catchError(() => {
              this.router.navigate(['/auth'], { queryParams: { returnUrl: state.url } });
              return of(false);
            })
          );
        }
        return this.checkRoles(requiredRoles, state.url);
      })
    );
  }
  
  private checkRoles(requiredRoles: string[], returnUrl: string): Observable<boolean> {
    const userRoles = this.authService.getRoles();
    const hasRequiredRole = requiredRoles.some(role => 
      userRoles.includes(role.toUpperCase()) || userRoles.includes(role.toLowerCase())
    );
    
    if (hasRequiredRole) {
      return of(true);
    }
    this.router.navigate(['/unauthorized'], { queryParams: { returnUrl } });
    return of(false);
  }
}
