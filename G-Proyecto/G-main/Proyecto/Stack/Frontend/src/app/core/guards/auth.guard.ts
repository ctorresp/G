import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { STORAGE_KEYS } from '../constants';
import { storage } from '../storage';

function isTokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.exp * 1000 < Date.now();
  } catch {
    return true;
  }
}

export const authGuard = () => {
  const router = inject(Router);
  const token = storage.getItem(STORAGE_KEYS.TOKEN);
  console.log('[authGuard] token encontrado:', !!token, 'storage:', storage.getItem(STORAGE_KEYS.TOKEN) ? 'session' : 'vacio');
  if (token) {
    const expired = isTokenExpired(token);
    console.log('[authGuard] token expirado:', expired);
    if (!expired) {
      return true;
    }
  }
  console.log('[authGuard] redirigiendo a /login');
  storage.removeItem(STORAGE_KEYS.TOKEN);
  return router.parseUrl('/login');
};
