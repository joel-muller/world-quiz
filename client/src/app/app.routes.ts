import { Routes } from '@angular/router';
import { Dashboard } from './dashboard/dashboard';
import { QuizManager } from './quiz-manager/quiz-manager';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'quiz',
    pathMatch: 'full',
  },
  {
    path: 'dashboard',
    component: Dashboard,
    title: 'Dashboard',
  },
  {
    path: 'quiz',
    component: QuizManager,
    title: 'Image Uploader',
  },
];
