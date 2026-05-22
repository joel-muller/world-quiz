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
import { Category } from '../entities/Category';
import { CardDto, FinishGameRequest, GameStatDto, QuizDto } from '../entities/Dto';

@Component({
  selector: 'app-quiz-detail',
  imports: [],
  templateUrl: './quiz-detail.html',
  styleUrl: './quiz-detail.css',
})
export class QuizDetail {
  quiz = input.required<QuizDto>();
  quizFinished = output<void>();

  cards = signal<CardDto[]>([]);
  cardsGuessedRight = signal<CardDto[]>([]);
  stats = signal<GameStatDto | null>(null);
  showBack = signal(false);

  private quizService: QuizService = inject(QuizService);

  constructor() {
    effect(() => {
      const quiz = this.quiz();
      this.cards.set([...quiz.cards]);
      this.cardsGuessedRight.set([]);
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
      if (right) {
        front.guessedRight++;
        this.cardsGuessedRight.update((cards) => [...cards, front]);
        return rest;
      }
      front.guessedWrong++;
      return [...rest, front];
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
    const cards = [...this.cards(), ...this.cardsGuessedRight()];
    const request: FinishGameRequest = { id: this.quiz().id, cards: cards };
    this.quizService.finishGame(request).subscribe({
      next: (stats) => {
        this.stats.set(stats);
      },
    });
  }

  protected readonly Category = Category;
}
