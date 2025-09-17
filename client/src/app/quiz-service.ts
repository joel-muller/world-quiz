import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Card {
  PlaceId: number;
  Front: string;
  InfoFront: string;
  Back: string;
  InfoBack: string;
}

export interface Quiz {
  Id: string;
  Category: number;
  Cards: Card[];
}

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private apiUrl = 'http://localhost:8080/game';

  constructor(private http: HttpClient) {}

  startQuiz(category: number, tags: number[]): Observable<Quiz> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const body = { category, tags };
    return this.http.post<Quiz>(this.apiUrl, body, { headers });
  }
}
