import { Routes } from '@angular/router';
import { Dashboard } from './dashboard/dashboard';
import { QuizManager } from './quiz-manager/quiz-manager';
import { Login } from './login/login';
import { Verify } from './verify/verify';
import { authGuard } from './auth-guard';
import { ResetPassword } from './reset-password/reset-password';

export const routes: Routes = [
  { path: '', component: QuizManager, canActivate: [authGuard], title: 'Quiz' },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard], title: 'Dashboard' },
  { path: 'login', component: Login, title: 'Login' },
  { path: 'verify/:token', component: Verify, title: 'Verify' },
  { path: 'reset-password/:token', component: ResetPassword, title: 'Reset Password' },
];
