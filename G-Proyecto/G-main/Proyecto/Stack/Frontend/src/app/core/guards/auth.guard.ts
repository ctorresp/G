import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { STORAGE_KEYS } from '../constants';

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
  const token = localStorage.getItem(STORAGE_KEYS.TOKEN);
  if (token && !isTokenExpired(token)) {
    return true;
  }
  localStorage.removeItem(STORAGE_KEYS.TOKEN);
  localStorage.removeItem(STORAGE_KEYS.PORTAL);
  return router.parseUrl('/login');
};
