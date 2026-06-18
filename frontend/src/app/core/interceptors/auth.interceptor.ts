import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { STORAGE_KEYS } from '../constants';
import { storage } from '../storage';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token = storage.getItem(STORAGE_KEYS.TOKEN);

  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedRequest).pipe(
      catchError((err) => {
        console.log('[authInterceptor] error status:', err.status, err.statusText);
        if (err.status === 401) {
          console.log('[authInterceptor] 401 detectado, limpiando sesion');
          storage.removeItem(STORAGE_KEYS.TOKEN);
          router.navigate(['/login']);
        }
        return throwError(() => err);
      })
    );
  }

  return next(req);
};
