import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {QuizManager} from './quiz-manager/quiz-manager';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, QuizManager],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('world-quiz');
}
