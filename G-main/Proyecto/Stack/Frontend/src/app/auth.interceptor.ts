import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Buscamos el token que guardaste en el login
  const token = localStorage.getItem('token_clinica');

  // Si el token existe, clonamos la petición y le agregamos el header de Authorization
  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    // Enviamos la petición modificada al backend
    return next(clonedRequest);
  }

  // Si no hay token, dejamos pasar la petición normal
  return next(req);
};