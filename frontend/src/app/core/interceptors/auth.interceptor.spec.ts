import { TestBed } from '@angular/core/testing';
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpHeaders } from '@angular/common/http';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  const interceptor: HttpInterceptorFn = (req, next) =>
    TestBed.runInInjectionContext(() => authInterceptor(req, next));

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
  });

  it('should add Authorization header when token exists', () => {
    localStorage.setItem('token_clinica', 'my-jwt-token');

    const req = new HttpRequest('GET', '/api/test');
    const next: HttpHandlerFn = (clonedReq) => {
      expect(clonedReq.headers.get('Authorization')).toBe('Bearer my-jwt-token');
      return new Observable((obs) => obs.next());
    };

    interceptor(req, next);
  });

  it('should not modify request when no token', () => {
    const req = new HttpRequest('GET', '/api/test');
    const next: HttpHandlerFn = (clonedReq) => {
      expect(clonedReq.headers.has('Authorization')).toBeFalse();
      return new Observable((obs) => obs.next());
    };

    interceptor(req, next);
  });
});

import { Observable } from 'rxjs';
