import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { QuizService } from './quiz-service';
import { catchError, switchMap, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const quizService = inject(QuizService);
  const token = quizService.getAccessToken();

  const authReq = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status !== 401) {
        return throwError(() => err);
      }

      const refreshToken = quizService.getRefreshToken();
      if (!refreshToken) {
        quizService.logout();
        return throwError(() => err);
      }

      return quizService.refresh({ refreshToken }).pipe(
        switchMap((res) => {
          quizService.setTokens(res);

          return next(
            req.clone({
              setHeaders: {
                Authorization: `Bearer ${res.accessToken}`,
              },
            }),
          );
        }),
      );
    }),
  );
};
