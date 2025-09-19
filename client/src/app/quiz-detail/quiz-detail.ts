import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core';
import { QuizService } from '../quiz-service';
import { Quiz } from '../entities/Quiz';
import { Card } from '../entities/Card';

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
  showBack: boolean = false;

  constructor(private quizService: QuizService) {}

  ngOnInit() {
    this.cards = this.quiz.Cards;
  }

  getTextCardFlipper() {
    return this.showBack ? 'Show Back' : 'Hide Back';
  }

  getCurrentCategory(): number {
    return this.isActive() ? this.cards[0].Category : -1;
  }

  isActive(): boolean {
    return this.cards.length > 0;
  }

  getCurrentFront(): string {
    return this.isActive() ? this.cards[0].Front : '';
  }

  getCurrentInfoFront(): string {
    return this.isActive() ? this.cards[0].InfoFront : '';
  }

  getCurrentBack(): string {
    return this.isActive() ? this.cards[0].Back : '';
  }

  getCurrentInfoBack(): string {
    return this.isActive() ? this.cards[0].InfoBack : '';
  }

  toggleBack(): void {
    this.showBack = !this.showBack;
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
}
