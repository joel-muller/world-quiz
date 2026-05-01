import { computed, Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Quiz } from './entities/Quiz';
import { GameStats } from './entities/GameStats';
import { environment } from '../environments/environment';
import {
  AuthResponse,
  LoginRequest,
  RefreshRequest,
  RequestGame,
  RequestUser,
} from './entities/Dto';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private token = signal<string | null>(localStorage.getItem('accessToken'));

  loggedIn = computed(() => {
    return this.token() !== null;
  });

  constructor(private http: HttpClient) {}

  fetchQuiz(request: RequestGame): Observable<Quiz> {
    return this.http.post<Quiz>(`${environment.apiUrl}/quiz`, request);
  }

  finishGame(gameId: string): Observable<GameStats> {
    return this.http.post<GameStats>(`${environment.apiUrl}/quiz/${gameId}/finish`, {});
  }

  createUser(request: RequestUser) {
    return this.http.post(`${environment.apiUrl}/auth/register`, request);
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, request)
      .pipe(tap((res) => this.setTokens(res)));
  }

  refresh(request: RefreshRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/refresh`, request)
      .pipe(tap((res) => this.setTokens(res)));
  }

  logout() {
    this.clearTokens();
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
