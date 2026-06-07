import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  let router: Router;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        {
          provide: Router,
          useValue: { parseUrl: (url: string) => url },
        },
      ],
    });
    router = TestBed.inject(Router);
  });

  it('should allow activation when token exists', () => {
    localStorage.setItem('token_clinica', 'valid-token');
    const result = authGuard();
    expect(result).toBeTrue();
  });

  it('should redirect to /login when no token', () => {
    const result = authGuard();
    expect(result).toBe('/login');
  });
});
