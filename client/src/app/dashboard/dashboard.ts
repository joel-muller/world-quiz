import { Component, inject } from '@angular/core';
import { QuizService } from '../quiz-service';

@Component({
  selector: 'app-dashboard',
  imports: [],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  protected quizService = inject(QuizService);
}
