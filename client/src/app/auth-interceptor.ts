import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth-service';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (req.url.includes('/auth/refresh')) return next(req);

  const logoutAndRedirect = () => {
    authService.logout();
    void router.navigate(['/login']);
  };

  const withAuth = (token: string) =>
    next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));

  const accessToken = authService.getAccessToken();
  const refreshToken = authService.getRefreshToken();

  return (accessToken ? withAuth(accessToken) : next(req)).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status !== 401) return throwError(() => err);
      if (!refreshToken) {
        logoutAndRedirect();
        return throwError(() => err);
      }

      return authService.refresh({ refreshToken }).pipe(
        switchMap((res) => {
          authService.setTokens(res);
          return withAuth(res.accessToken);
        }),
        catchError(() => {
          logoutAndRedirect();
          return throwError(() => err);
        }),
      );
    }),
  );
};
