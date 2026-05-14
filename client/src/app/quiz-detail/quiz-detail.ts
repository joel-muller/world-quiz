import {
  Component,
  computed,
  effect,
  HostListener,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import { QuizService } from '../quiz-service';
import { Quiz } from '../entities/Quiz';
import { Card } from '../entities/Card';
import { Category } from '../entities/Category';
import { FinishGameRequest, GameStatResponse } from '../entities/Dto';

@Component({
  selector: 'app-quiz-detail',
  imports: [],
  templateUrl: './quiz-detail.html',
  styleUrl: './quiz-detail.css',
})
export class QuizDetail {
  quiz = input.required<Quiz>();
  quizFinished = output<void>();

  cards = signal<Card[]>([]);
  stats = signal<GameStatResponse | null>(null);
  showBack = signal(false);

  private quizService: QuizService = inject(QuizService);

  constructor() {
    effect(() => {
      const quiz = this.quiz();
      this.cards.set([...quiz.cards]);
    });
  }

  readonly currentCard = computed(() => this.cards()[0]);

  readonly cardFlipperIcon = computed(() =>
    this.showBack() ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye',
  );

  toggleBack() {
    this.showBack.update((v) => !v);
  }

  guess(right: boolean) {
    if (!this.showBack()) {
      this.showBack.set(true);
      return;
    }

    this.cards.update((cards) => {
      const [front, ...rest] = cards;

      if (!front) {
        return cards;
      }

      return right ? rest : [...rest, front];
    });

    this.showBack.set(false);

    if (!this.currentCard()) {
      this.getStats();
    }
  }

  abortQuiz() {
    this.cards.set([]);
    this.getStats();
  }

  closeQuiz() {
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

  private getStats() {
    const request: FinishGameRequest = { id: this.quiz().id };
    this.quizService.finishGame(request).subscribe({
      next: (stats) => {
        this.stats.set(stats);
      },
    });
  }

  protected readonly Category = Category;
}
