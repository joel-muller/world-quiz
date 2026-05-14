import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Quiz } from './entities/Quiz';
import { environment } from '../environments/environment';
import { CreateQuizRequest, FinishGameRequest, GameStatResponse } from './entities/Dto';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private http: HttpClient = inject(HttpClient);

  fetchQuiz(request: CreateQuizRequest): Observable<Quiz> {
    return this.http.post<Quiz>(`${environment.apiUrl}/quiz/create`, request);
  }

  finishGame(request: FinishGameRequest): Observable<GameStatResponse> {
    return this.http.post<GameStatResponse>(`${environment.apiUrl}/quiz/finish`, request);
  }
}
