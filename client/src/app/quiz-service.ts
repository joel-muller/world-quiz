import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Quiz } from './entities/Quiz';
import { GameStats } from './entities/GameStats';
import { environment } from '../environments/environment';
import { Category } from './entities/Category';
import { Tag } from './entities/Tag';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  constructor(private http: HttpClient) {}

  fetchQuiz(categories: Category[], tags: Tag[], number: number | undefined): Observable<Quiz> {
    const url = `${environment.apiUrl}/quiz`;
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (number) {
      const body = { categories, tags, number };
      return this.http.post<Quiz>(url, body, { headers });
    }
    const body = { categories, tags };
    return this.http.post<Quiz>(url, body, { headers });
  }

  finishGame(gameId: string): Observable<GameStats> {
    const url = `${environment.apiUrl}/quiz/${gameId}/finish`;
    return this.http.post<GameStats>(url, {});
  }
}
