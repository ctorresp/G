import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { Login } from './login';
import { environment } from '../../environments/environment';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [Login, HttpClientTestingModule],
      providers: [
        {
          provide: Router,
          useValue: { navigate: (url: string[]) => { } },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    await fixture.whenStable();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should read portalSeleccionado from localStorage on init', () => {
    localStorage.setItem('portalSeleccionado', 'Médico');
    component.ngOnInit();
    expect(component.portalSeleccionado).toBe('Médico');
  });

  it('should call login endpoint and navigate on success', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.emailOrRut = 'admin@test.cl';
    component.password = 'pass123';

    component.iniciarSesion();

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.email).toBe('admin@test.cl');
    expect(req.request.body.rut).toBeNull();

    req.flush({ token: 'jwt-token' });

    expect(localStorage.getItem('token_clinica')).toBe('jwt-token');
    expect(navigateSpy).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should use rut field when input does not contain @', () => {
    component.emailOrRut = '12345678-9';
    component.password = 'pass';

    component.iniciarSesion();

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.body.email).toBeNull();
    expect(req.request.body.rut).toBe('12345678-9');
    req.flush({ token: 'jwt' });
  });

  it('should show error message on failed login', () => {
    component.emailOrRut = 'bad@test.cl';
    component.password = 'wrong';

    component.iniciarSesion();

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(component.mensajeError).toContain('Credenciales incorrectas');
  });
});
