import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Quiz } from './entities/Quiz';
import { GameStats } from './entities/GameStats';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  fetchQuiz(categories: number[], tags: number[], number: number | undefined): Observable<Quiz> {
    const url = `${this.apiUrl}/game`;
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (number) {
      const body = { categories, tags, number };
      return this.http.post<Quiz>(url, body, { headers });
    }
    const body = { categories, tags };
    return this.http.post<Quiz>(url, body, { headers });
  }

  finishGame(gameId: string): Observable<GameStats> {
    const url = `${this.apiUrl}/game/finish`;
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const body = { id: gameId };
    return this.http.post<GameStats>(url, body, { headers });
  }
}
