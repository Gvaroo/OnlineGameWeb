import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { RegisterUserDTO } from '../models/User/RegisterUserDTO';
import { Observable, catchError, map, of, switchMap, tap } from 'rxjs';
import { LoginUserDTO } from '../models/User/LoginUserDTO';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  register(user: RegisterUserDTO): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}public/register`, user);
  }

  login(user: LoginUserDTO): Observable<any> {
    console.log('testtt');
    return this.http
      .post<any>(`${this.apiUrl}public/login`, user, {
        withCredentials: true,
      })
      .pipe(
        tap((response) => {
          console.log(response);
          localStorage.setItem('fullName', response.data.name);
          localStorage.setItem('email', response.data.email);
        })
      );
  }

  //
  isLoggedIn(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}public/IsLoggedIn`, {
      withCredentials: true,
    });
  }

  logout(): Observable<boolean> {
    return this.http
      .get<any>(`${this.apiUrl}public/LogOut`, {
        withCredentials: true,
      })
      .pipe(
        tap((response) => {
          localStorage.clear();
        })
      );
  }
}
