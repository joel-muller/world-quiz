import { Routes } from '@angular/router';
import { Dashboard } from './dashboard/dashboard';
import { QuizManager } from './quiz-manager/quiz-manager';
import { Account } from './account/account';

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
    title: 'Quiz',
  },
  {
    path: 'account',
    component: Account,
    title: 'Account',
  },
];
