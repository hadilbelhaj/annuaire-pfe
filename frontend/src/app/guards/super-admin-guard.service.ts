
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map, take, mergeMap } from 'rxjs/operators';
import { AuthserviceService } from '../services/authservice.service';
@Injectable({
  providedIn: 'root'
})
export class SuperAdminGuardService {

  constructor(
    private authService: AuthserviceService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.authService.isLoggedIn$.pipe(
      take(1),
      mergeMap(isLoggedIn => {
        if (isLoggedIn) {
          if (this.authService.isTokenExpired()) {
            return this.authService.refreshToken().pipe(
              map(() => true),
              catchError(() => {
                this.router.navigate(['/auth'], { queryParams: { returnUrl: state.url } });
                return of(false);
              })
            );
          }
          return of(true);
        }
        this.router.navigate(['/auth'], { queryParams: { returnUrl: state.url } });
        return of(false);
      })
    );
  }
}
