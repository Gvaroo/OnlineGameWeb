import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class HomeRedirectGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    if (this.authService.isLoggedIn()) {
      // this._notification.create(
      //   'error',
      //   'login error',
      //   'You are already logged in!',
      //   { nzDuration: 400000 }
      // );
      // setTimeout(() => {
      //   this.router.navigateByUrl('/').then(() => {
      //     window.location.reload();
      //   }); // Redirect to the home page after notification closes
      // }, 4000);
      return false;
    } else {
      return true;
    }
  }
}
