import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Quiz } from './entities/Quiz';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private apiUrl = 'http://localhost:8080/game';

  constructor(private http: HttpClient) {}

  fetchQuiz(categories: number[], tags: number[], number: number | undefined): Observable<Quiz> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (number) {
      const body = { categories, tags, number };
      return this.http.post<Quiz>(this.apiUrl, body, { headers });
    }
    const body = { categories, tags };
    return this.http.post<Quiz>(this.apiUrl, body, { headers });
  }
}
