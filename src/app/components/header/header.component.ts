import { Component, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { CartDatasDTO } from 'src/app/models/Cart/CartDatasDTO';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  screenHeight: any;
  screenWidth: any;
  isMenuOpen = false;
  isMobile = false;
  isLoggedIn = false;
  dropdownVisible = false;
  isAdmin = false;
  cartData!: CartDatasDTO;

  @HostListener('window:resize', ['$event'])
  getScreenSize() {
    this.screenHeight = window.innerHeight;
    this.screenWidth = window.innerWidth;

    if (this.screenWidth > 768) this.isMobile = false;
    else this.isMobile = true;
  }
  constructor(private _auth: AuthService, private router: Router) {
    this.getScreenSize();
    this.checkUser();
  }

  ngOnInit(): void {}

  toggleDropdown() {
    this.dropdownVisible = !this.dropdownVisible;
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }
  logout() {
    this._auth.logout().subscribe(
      (response) => {
        this.router.navigateByUrl('/').then(() => {
          window.location.reload();
        });
      },
      (error) => {
        console.error(error);
      }
    );
    this.isMenuOpen = false;
  }

  checkUser(): boolean {
    this._auth.isLoggedIn().subscribe(
      (res) => {
        if (res.data) {
          this.isLoggedIn = true;
          return true;
        } else {
          this.isLoggedIn = false;
          return false;
        }
      },
      (err) => {
        this.isLoggedIn = false;
        return false;
      }
    );
    return false;
  }
}
