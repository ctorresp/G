import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { STORAGE_KEYS } from '../constants';
import { storage } from '../storage';
import { ToastService } from '../services/toast.service';

function crearRoleGuard(...allowedRoles: string[]) {
  return () => {
    const router = inject(Router);
    const toastService = inject(ToastService);
    const raw = storage.getItem(STORAGE_KEYS.ROLES);
    if (!raw) return router.parseUrl('/login');
    const hasAccess = allowedRoles.includes(raw);
    if (hasAccess) return true;
    toastService.mostrar('Credenciales no válidas', 'error');
    return router.parseUrl('/');
  };
}

export const adminGuard = crearRoleGuard('ROLE_ADMIN');
export const medicoGuard = crearRoleGuard('ROLE_MEDICO');
export const coordinadorGuard = crearRoleGuard('ROLE_COORDINADOR');
export const triajerGuard = crearRoleGuard('ROLE_TRIAJER');
