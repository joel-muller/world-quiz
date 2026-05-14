import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, switchMap, tap } from 'rxjs';
import { environment } from '../environments/environment';
import {
  AuthResponse,
  LoginRequest,
  RefreshRequest,
  RegisterRequest,
  ResendVerificationRequest,
  UserResponse,
  VerifyEmailRequest,
} from './entities/Dto';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http: HttpClient = inject(HttpClient);
  private token = signal<string | null>(localStorage.getItem('accessToken'));
  private user = signal<UserResponse | null>(JSON.parse(localStorage.getItem('user') || 'null'));

  currentUser = computed(() => this.user());

  loggedIn = computed(() => {
    return this.token() !== null;
  });

  getUser(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${environment.apiUrl}/user/me`).pipe(
      tap((user) => {
        this.user.set(user);
        localStorage.setItem('user', JSON.stringify(user));
      }),
    );
  }

  createUser(request: RegisterRequest) {
    return this.http.post(`${environment.apiUrl}/auth/register`, request);
  }

  login(request: LoginRequest): Observable<UserResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, request).pipe(
      tap((res) => this.setTokens(res)),
      switchMap(() => this.getUser()),
    );
  }

  refresh(request: RefreshRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/refresh`, request)
      .pipe(tap((res) => this.setTokens(res)));
  }

  verifyEmail(request: VerifyEmailRequest): Observable<UserResponse> {
    return this.http
      .post<void>(`${environment.apiUrl}/auth/verify-email`, request)
      .pipe(switchMap(() => this.getUser()));
  }

  resendVerification(request: ResendVerificationRequest): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/resend-verification`, request);
  }

  logout() {
    this.clearTokens();
    this.user.set(null);
    localStorage.removeItem('user');
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  setTokens(res: AuthResponse) {
    localStorage.setItem('accessToken', res.accessToken);
    localStorage.setItem('refreshToken', res.refreshToken);
    this.token.set(res.accessToken);
  }

  clearTokens() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    this.token.set(null);
  }
}
