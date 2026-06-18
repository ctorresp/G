import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { Home } from './home';

describe('Home', () => {
  let router: Router;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        {
          provide: Router,
          useValue: { navigate: (url: string[]) => {} },
        },
      ],
    }).compileComponents();
  });

  it('should create the component', () => {
    const fixture = TestBed.createComponent(Home);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });

  it('should navigate to login with rol Médico', () => {
    const fixture = TestBed.createComponent(Home);
    const component = fixture.componentInstance;
    router = TestBed.inject(Router);
    const navigateSpy = spyOn(router, 'navigate');

    component.irAlLogin('Médico');

    expect(localStorage.getItem('portalSeleccionado')).toBe('Médico');
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should navigate to login with rol Administrador', () => {
    const fixture = TestBed.createComponent(Home);
    const component = fixture.componentInstance;
    router = TestBed.inject(Router);
    const navigateSpy = spyOn(router, 'navigate');

    component.irAlLogin('Administrador');

    expect(localStorage.getItem('portalSeleccionado')).toBe('Administrador');
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });
});
