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
}

interface CategoryOption {
  name: string;
  value: Category;
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
  selectedTags: Set<Tag> = new Set();
  selectedCategories: Set<Category> = new Set();

  tagOptions: TagOption[] = [
    { name: '🏔 Europe', value: Tag.EUROPE },
    { name: '🏯 Asia', value: Tag.ASIA },
    { name: '🏝️ Oceania', value: Tag.OCEANIA },
    { name: '🦅 North America', value: Tag.NORTH_AMERICA },
    { name: '🦜 South America', value: Tag.SOUTH_AMERICA },
    { name: '🦁 Africa', value: Tag.AFRICA },
    { name: '🌊 Oceans and Seas', value: Tag.OCEANS_AND_SEAS },
    { name: '🗺️ Continents', value: Tag.CONTINENTS },
    { name: '🏛️ Sovereign State', value: Tag.SOVEREIGN_STATE },
    { name: '🌿 Mediterranean', value: Tag.MEDITERRANEAN },
    { name: '🇪🇺 European Union', value: Tag.EUROPEAN_UNION },
    { name: '🏜️ Middle East', value: Tag.MIDDLE_EAST },
    { name: '🦓 East Africa', value: Tag.EAST_AFRICA },
    { name: '🍜 Southeast Asia', value: Tag.SOUTHEAST_ASIA },
    { name: '🏖️ Caribbean', value: Tag.CARIBBEAN },
  ];

  categoryOptions: CategoryOption[] = [
    { name: 'Map Name', value: Category.MAP_NAME },
    { name: 'Flag Name', value: Category.FLAG_NAME },
    { name: 'Capital Name', value: Category.CAPITAL_NAME },
    { name: 'Name Capital', value: Category.NAME_CAPITAL },
  ];

  constructor(private quizService: QuizService) {}

  fetchQuiz() {
    const tagsArray = Array.from(this.selectedTags);
    const categoryArray = Array.from(this.selectedCategories);
    if (this.maxCards != null && this.maxCards <= 0) {
      this.currentError = 'The number of cards cannot be 0 or lower';
      return;
    }

    if (this.maxCards && this.maxCards > 1000000) {
      this.currentError = 'Chill, i dont have so many cards, just  leave it empty then idiot';
      return;
    }

    if (tagsArray.length === 0) {
      this.currentError = 'Add at least one tag';
      return;
    }

    if (categoryArray.length === 0) {
      this.currentError = 'Add at least one category';
      return;
    }

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

  toggleTag(tag: Tag) {
    this.selectedTags.has(tag) ? this.selectedTags.delete(tag) : this.selectedTags.add(tag);
  }

  toggleCategory(category: Category) {
    this.selectedCategories.has(category)
      ? this.selectedCategories.delete(category)
      : this.selectedCategories.add(category);
  }

  resetSelectedValues() {
    this.selectedTags = new Set([]);
    this.selectedCategories = new Set();
    this.maxCards = undefined;
    this.currentError = null;
  }
}
