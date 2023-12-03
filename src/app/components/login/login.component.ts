import { Component, HostListener } from '@angular/core';
import { Router } from '@angular/router';

import { LoginUserDTO } from 'src/app/models/User/LoginUserDTO';
import { AuthService } from 'src/app/services/auth.service';
import { ValidationError } from 'src/app/models/Validation/ValidationError';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  credentials: LoginUserDTO = {
    email: '',
    password: '',
  };

  validationError: ValidationError = {};
  loading = false;

  constructor(private _auth: AuthService, private _router: Router) {}

  ngOnInit(): void {}

  onSubmit(): void {
    this.loading = true;
    if (!this.credentials.email || !this.credentials.password) {
      this.validationError = { _generic: 'Make sure to fill everything ;)' };
    } else {
      this._auth.login(this.credentials).subscribe(
        (res) => {
          this.loading = false;
          this._router.navigateByUrl('/').then(() => {
            window.location.reload();
          });
        },
        (err) => {
          if (err && err.error && err.error.errors) {
            // 'err.error.errors' contains the validation errors sent by the API
            const errorsObject = err.error.errors as {
              [key: string]: string[];
            };
            // Initialize the 'error' object to display the validation errors
            this.validationError = {};

            // Iterate through each property in the 'errors' object and extract the error messages
            for (const [field, errors] of Object.entries(errorsObject)) {
              this.validationError[field] = errors.join(' ');
              this.loading = false;
            }
          } else if (err && err.message) {
            // for a generic error message
            this.validationError = { _generic: err.error.message };
            this.loading = false;
          }
        }
      );
    }
  }

  // Method to check if the 'error' object is empty
  hasErrors(): boolean {
    return Object.keys(this.validationError).length > 0;
  }
  errorKeys(): string[] {
    return Object.keys(this.validationError);
  }
  canSubmit(): boolean {
    return (
      this.credentials.email.length > 0 && this.credentials.password.length > 0
    );
  }
}
