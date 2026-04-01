import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const isAuthRequest = req.url.includes('/api/auth/login') || req.url.includes('/api/auth/register');
  const token = authService.getValidToken();

  const requestToSend = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(requestToSend).pipe(
    catchError((err: unknown) => {
      if (!isAuthRequest && err instanceof HttpErrorResponse && err.status === 401) {
        authService.clearSession();
        router.navigate(['/login']);
      }
      return throwError(() => err);
    })
  );
};
