import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core';
import { QuizService } from '../quiz-service';
import { Quiz } from '../entities/Quiz';
import { Card } from '../entities/Card';
import { GameStats } from '../entities/GameStats';
import { Category } from '../entities/Category';

@Component({
  selector: 'app-quiz-detail',
  imports: [],
  templateUrl: './quiz-detail.html',
  styleUrl: './quiz-detail.css',
})
export class QuizDetail {
  @Input({ required: true }) quiz!: Quiz;
  @Output() quizFinished = new EventEmitter<void>();
  cards: Card[] = [];
  currentCard: Card | null = null;
  stats: GameStats | null = null;
  showBack: boolean = false;

  constructor(private quizService: QuizService) {}

  ngOnInit() {
    this.cards = this.quiz.cards;
    this.loadCard()
  }

  getTextCardFlipper() {
    return this.showBack ? 'Show Back' : 'Hide Back';
  }

  toggleBack(): void {
    this.showBack = !this.showBack;
  }

  loadCard() {
      if (this.cards.length > 0) {
        this.currentCard = this.cards[0];
      } else {
        this.currentCard = null;
      }
  }

  guess(right: boolean): void {
    if (!this.showBack) {
      this.showBack = true;
      return;
    }
    let front = this.cards.shift();
    if (front && !right) {
      this.cards.push(front);
    }
    this.showBack = false;
    this.loadCard()

    if (this.currentCard == null) {
      this.finishQuizAndLoadStats();
    }
  }

  finishQuiz(): void {
    this.quizFinished.emit();
  }

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ' || event.key === '2') {
      event.preventDefault();
      this.guess(true);
    }
    if (event.key === '1') {
      event.preventDefault();
      this.guess(false);
    }
  }

  private finishQuizAndLoadStats(): void {
    this.quizService.finishGame(this.quiz.id).subscribe({
      next: (stats) => {
        this.stats = stats;
        console.log('Quiz finished, stats:', stats);
      },
      error: (err) => {
        console.error('Failed to finish quiz', err);
      },
    });
  }

  protected readonly Category = Category;
}
