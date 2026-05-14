import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { QuizService } from './quiz-service';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth-service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getAccessToken();

  const authReq = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status !== 401) {
        return throwError(() => err);
      }

      const refreshToken = authService.getRefreshToken();
      if (!refreshToken) {
        authService.logout();
        return throwError(() => err);
      }

      return authService.refresh({ refreshToken }).pipe(
        switchMap((res) => {
          authService.setTokens(res);

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
