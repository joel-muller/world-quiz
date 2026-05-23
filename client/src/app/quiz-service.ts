import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { CreateQuizRequest, FinishQuizRequest, QuizStatDto, QuizDto } from './entities/Dto';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private http: HttpClient = inject(HttpClient);

  fetchQuiz(request: CreateQuizRequest): Observable<QuizDto> {
    return this.http.post<QuizDto>(`${environment.apiUrl}/quiz/create`, request);
  }

  finishGame(request: FinishQuizRequest): Observable<QuizStatDto> {
    return this.http.post<QuizStatDto>(`${environment.apiUrl}/quiz/finish`, request);
  }
}
