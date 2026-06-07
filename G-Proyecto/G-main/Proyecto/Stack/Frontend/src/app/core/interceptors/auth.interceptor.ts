import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { STORAGE_KEYS } from '../constants';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token = localStorage.getItem(STORAGE_KEYS.TOKEN);

  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedRequest).pipe(
      catchError((err) => {
        if (err.status === 401) {
          localStorage.removeItem(STORAGE_KEYS.TOKEN);
          localStorage.removeItem(STORAGE_KEYS.PORTAL);
          router.navigate(['/login']);
        }
        return throwError(() => err);
      })
    );
  }

  return next(req);
};
