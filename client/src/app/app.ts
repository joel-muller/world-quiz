import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { QuizService } from './quiz-service';
import { Login } from './login/login';
import { AuthService } from './auth-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly authService = inject(AuthService);
  protected readonly title = signal('world-quiz');

  constructor() {
    if (this.authService.getRefreshToken()) {
      this.authService.getUser().subscribe();
    }
  }
}
