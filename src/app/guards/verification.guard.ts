import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class VerificationGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    return this.authService.isVerified().pipe(
      map((response) => {
        if (response.data) {
          return true;
        } else {
          this.router.navigate(['/unverified']);
          return false;
        }
      }),
      catchError(() => {
        // Handle errors if needed
        this.router.navigate(['/unverified']);
        return of(false);
      })
    );
  }
}
