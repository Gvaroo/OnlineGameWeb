import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Observable, catchError, map, of } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    return this.authService.isLoggedIn().pipe(
      map((response) => {
        if (response.data) {
          return true; // User is logged in, allow navigation
        } else {
          localStorage.removeItem('email');
          localStorage.removeItem('fullName');
          this.router.navigate(['/login']); // User is not logged in, redirect to login page
          return false;
        }
      }),
      catchError(() => {
        localStorage.removeItem('email');
        localStorage.removeItem('fullName');
        this.router.navigate(['/login']); // Error occurred, redirect to login page
        return of(false);
      })
    );
  }
}
