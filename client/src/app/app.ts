import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { QuizService } from './quiz-service';
import { Login } from './login/login';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, Login],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly quizService = inject(QuizService);
  protected readonly title = signal('world-quiz');
}
