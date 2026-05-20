import { Component, computed, inject, signal } from '@angular/core';
import { QuizService } from '../quiz-service';
import { QuizDetail } from '../quiz-detail/quiz-detail';
import { Quiz } from '../entities/Quiz';
import { Tag } from '../entities/Tag';
import { Category } from '../entities/Category';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth-service';
import { CreateQuizRequest } from '../entities/Dto';

interface TagOption {
  name: string;
  value: Tag;
  selected: boolean;
}

interface CategoryOption {
  name: string;
  value: Category;
  selected: boolean;
}

@Component({
  selector: 'app-quiz-manager',
  imports: [QuizDetail, FormsModule],
  templateUrl: './quiz-manager.html',
  styleUrl: './quiz-manager.css',
})
export class QuizManager {
  private quizService: QuizService = inject(QuizService);
  private authService: AuthService = inject(AuthService);

  currentUser = computed(() => {
    return this.authService.currentUser();
  });

  verificationMailSent = signal(false);

  quiz = signal<Quiz | null>(null);
  currentError = signal<string | null>(null);
  maxCards = signal<number | undefined>(undefined);

  tagOptions = signal<TagOption[]>([
    { name: 'Europe', value: Tag.EUROPE, selected: false },
    { name: 'Asia', value: Tag.ASIA, selected: false },
    { name: 'Oceania', value: Tag.OCEANIA, selected: false },
    { name: 'North America', value: Tag.NORTH_AMERICA, selected: false },
    { name: 'South America', value: Tag.SOUTH_AMERICA, selected: false },
    { name: 'Africa', value: Tag.AFRICA, selected: false },
    { name: 'Oceans and Seas', value: Tag.OCEANS_AND_SEAS, selected: false },
    { name: 'Continents', value: Tag.CONTINENTS, selected: false },
  ]);

  categoryOptions = signal<CategoryOption[]>([
    { name: 'Map → Name (Capital)', value: Category.MAP_NAME, selected: false },
    { name: 'Flag → Name (Capital)', value: Category.FLAG_NAME, selected: false },
    { name: 'Capital → Name', value: Category.CAPITAL_NAME, selected: false },
    { name: 'Name → Capital', value: Category.NAME_CAPITAL, selected: false },
  ]);

  quizReady = computed(() => {
    const maxCards = this.maxCards();

    const validMaxCards = maxCards == null || (maxCards > 0 && maxCards <= 1_000_000);

    return validMaxCards && this.selectedTags().length > 0 && this.selectedCategories().length > 0;
  });

  selectedTags = computed(() =>
    this.tagOptions()
      .filter((t) => t.selected)
      .map((t) => t.value),
  );

  selectedCategories = computed(() =>
    this.categoryOptions()
      .filter((c) => c.selected)
      .map((c) => c.value),
  );

  toggleMaxCards(value: number) {
    if (this.maxCards() === value) {
      this.maxCards.set(undefined);
      return;
    }
    this.maxCards.set(value);
  }

  toggleTag(value: Tag) {
    this.tagOptions.update((tags) =>
      tags.map((tag) => (tag.value === value ? { ...tag, selected: !tag.selected } : tag)),
    );
  }

  toggleCategory(value: Category) {
    this.categoryOptions.update((categories) =>
      categories.map((category) =>
        category.value === value ? { ...category, selected: !category.selected } : category,
      ),
    );
  }

  setAllTags(selected: boolean) {
    this.tagOptions.update((tags) => tags.map((tag) => ({ ...tag, selected })));
  }

  setAllCategories(selected: boolean) {
    this.categoryOptions.update((categories) =>
      categories.map((category) => ({ ...category, selected })),
    );
  }

  fetchQuiz() {
    if (!this.quizReady()) return;

    const request: CreateQuizRequest = {
      categories: this.selectedCategories(),
      tags: this.selectedTags(),
      number: this.maxCards(),
    };

    this.quizService.fetchQuiz(request).subscribe({
      next: (quiz) => {
        if (quiz.cards.length === 0) {
          this.currentError.set('Ups, it seems that there are no cards with that selection');
          return;
        }
        this.quiz.set(quiz);
        this.resetSelectedValues();
      },
      error: () => {
        this.currentError.set('Failed to fetch quiz. Please try again later.');
      },
    });
  }

  resetSelectedValues() {
    this.maxCards.set(undefined);
    this.currentError.set(null);

    this.setAllTags(false);
    this.setAllCategories(false);
  }

  onQuizFinished() {
    this.quiz.set(null);
  }

  resendVerificationMail() {
    const user = this.currentUser();
    if (user) {
      this.authService.resendVerification({ email: user.email }).subscribe({
        next: () => {
          this.verificationMailSent.set(true);
        },
      });
    }
  }
}
