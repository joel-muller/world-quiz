import { Routes } from '@angular/router';
import { Dashboard } from './dashboard/dashboard';
import { QuizManager } from './quiz-manager/quiz-manager';
import { Login } from './login/login';

export const routes: Routes = [
  {
    path: '',
    component: QuizManager,
    title: 'Quiz',
  },
  {
    path: 'dashboard',
    component: Dashboard,
    title: 'Dashboard',
  },
  {
    path: 'login',
    component: Login,
    title: 'Login',
  },
];
