import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterUserDTO } from 'src/app/models/User/RegisterUserDTO';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {
  newUser: RegisterUserDTO = {
    fullName: '',
    email: '',
    password: '',
  };

  confirmPassword: string = '';
  errorMessage = '';
  loading = false;
  constructor(private _auth: AuthService, private _router: Router) {}

  ngOnInit(): void {}

  onSubmit(): void {
    this.errorMessage = '';
    if (
      this.newUser.fullName &&
      this.newUser.password &&
      this.newUser.email &&
      this.confirmPassword
    ) {
      if (this.newUser.password !== this.confirmPassword) {
        this.errorMessage = 'Passwords need to match';
      } else {
        this.loading = true;
        this._auth.register(this.newUser).subscribe(
          (res) => {
            this.loading = false;
            this._router.navigate(['/']);
          },
          (err) => {
            this.errorMessage = err.error.message;
            this.loading = false;
          }
        );
      }
    } else {
      this.errorMessage = 'Make sure to fill everything ;)';
    }
  }

  canSubmit(): boolean {
    return this.newUser.fullName &&
      this.newUser.email &&
      this.newUser.password &&
      this.confirmPassword
      ? true
      : false;
  }
}
