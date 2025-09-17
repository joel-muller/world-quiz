import { Component } from '@angular/core';
import { Quiz, QuizService, Card } from '../quiz-service';

@Component({
  selector: 'app-quiz-manager',
  imports: [],
  templateUrl: './quiz-manager.html',
  styleUrl: './quiz-manager.css',
})
export class QuizManager {
  quiz: Quiz | null = null;
  cards: Card[] = [];

  constructor(private quizService: QuizService) {}

  ngOnInit() {
    this.quizService.startQuiz(3, [0, 1]).subscribe({
      next: (data) => {
        this.quiz = data;
        this.cards = this.quiz.Cards;
      },
      error: (err) => console.error(err),
    });
  }
}
