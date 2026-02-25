import { Component } from '@angular/core';
import { QuizService } from '../quiz-service';
import { QuizDetail } from '../quiz-detail/quiz-detail';
import { Quiz } from '../entities/Quiz';
import { Tag } from '../entities/Tag';
import { Category } from '../entities/Category';
import { FormsModule } from '@angular/forms';

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
  quiz: Quiz | null = null;
  active: boolean = false;
  currentError: string | null = null;
  maxCards: number | undefined = undefined;

  tagOptions: TagOption[] = [
    { name: 'Europe', value: Tag.EUROPE, selected: false },
    { name: 'Asia', value: Tag.ASIA, selected: false },
    { name: 'Oceania', value: Tag.OCEANIA, selected: false },
    { name: 'North America', value: Tag.NORTH_AMERICA, selected: false },
    { name: 'South America', value: Tag.SOUTH_AMERICA, selected: false },
    { name: 'Africa', value: Tag.AFRICA, selected: false },
    { name: 'Oceans and Seas', value: Tag.OCEANS_AND_SEAS, selected: false },
    { name: 'Continents', value: Tag.CONTINENTS, selected: false },
  ];

  categoryOptions: CategoryOption[] = [
    { name: 'Map → Name (Capital)', value: Category.MAP_NAME, selected: false },
    { name: 'Flag → Name (Capital)', value: Category.FLAG_NAME, selected: false },
    { name: 'Capital → Name', value: Category.CAPITAL_NAME, selected: false },
    { name: 'Name → Capital', value: Category.NAME_CAPITAL, selected: false },
  ];

  constructor(private quizService: QuizService) {}

  quizReady(): boolean {
    if (this.maxCards != null && this.maxCards <= 0) {
      return false;
    }

    if (this.maxCards && this.maxCards > 1000000) {
      return false;
    }

    if (this.tagOptions.filter((t) => t.selected).length === 0) {
      return false;
    }

    if (this.categoryOptions.filter((c) => c.selected).length === 0) {
      return false;
    }
    return true;
  }

  fetchQuiz() {
    if (!this.quizReady()) {
      return;
    }
    const tagsArray = this.tagOptions.filter((t) => t.selected).map((t) => t.value);
    const categoryArray = this.categoryOptions.filter((c) => c.selected).map((c) => c.value);

    this.quizService.fetchQuiz(categoryArray, tagsArray, this.maxCards).subscribe({
      next: (data) => {
        this.quiz = data;
        if (data.cards.length === 0) {
          this.currentError = 'Ups, it seems that there are no cards with that selection';
        } else {
          this.active = true;
          this.resetSelectedValues();
        }
      },
      error: (err) => {
        this.currentError = 'Failed to fetch quiz. Please try again later.';
        console.log(err);
      },
    });
  }

  onQuizFinished() {
    this.active = false;
  }

  toggleTag(value: Tag): void {
    const tag = this.tagOptions.find((t) => t.value === value);
    if (tag) {
      tag.selected = !tag.selected;
    }
  }

  setAllTags(value: boolean): void {
    this.tagOptions.forEach((tag) => (tag.selected = value));
  }

  toggleCategory(value: Category): void {
    const category = this.categoryOptions.find((c) => c.value === value);
    if (category) {
      category.selected = !category.selected;
    }
  }

  setAllCategories(value: boolean): void {
    this.categoryOptions.forEach((c) => (c.selected = value));
  }

  resetSelectedValues() {
    this.maxCards = undefined;
    this.currentError = null;
    this.setAllTags(false);
    this.setAllCategories(false);
  }
}
