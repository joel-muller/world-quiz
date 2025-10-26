import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import { jwtDecode } from 'jwt-decode';
import { SecretMessage } from './entities/SecretMessage';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private apiUrl = environment.apiUrl;
  private tokenKey = 'auth_token';
  private loginStatus = new BehaviorSubject<boolean>(this.isLoggedIn());

  constructor(private http: HttpClient) {}

  public getToken() {
    return localStorage.getItem(this.tokenKey);
  }

  public getApiUrl() {
    return this.apiUrl;
  }

  public login(username: string, password: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, { username, password }).pipe(
      tap((response) => {
        localStorage.setItem(this.tokenKey, response.token);
        this.loginStatus.next(true);
      }),
    );
  }

  public register(username: string, password: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/register`, {
      username,
      password,
    });
  }

  public logOut(): void {
    localStorage.removeItem(this.tokenKey);
    this.loginStatus.next(false);
  }

  public isLoggedIn(): boolean {
    return this.isTokenValid();
  }

  private isTokenValid(): boolean {
    const token = localStorage.getItem(this.tokenKey);
    if (!token) return false;

    try {
      const decoded: any = jwtDecode(token);
      return decoded.exp > Math.floor(Date.now() / 1000);
    } catch (error) {
      return false;
    }
  }

  public validateToken(): Observable<{ valid: boolean; username: string }> {
    return this.http
      .get<{ valid: boolean; username: string }>(`${this.apiUrl}/validate-token`)
      .pipe(
        tap((response) => {
          if (!response.valid) {
            this.logOut();
          }
        }),
      );
  }

  getLoginStatus(): Observable<boolean> {
    return this.loginStatus.asObservable();
  }

  getSecretMessage(): Observable<SecretMessage> {
    const token = this.getToken();
    return this.http.post<SecretMessage>(
      `${this.getApiUrl()}/protected`,
      { message: 'super secret message' },
      { headers: { Authorization: `Bearer ${token}` } },
    );
  }
}
