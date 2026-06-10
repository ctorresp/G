import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';
import { STORAGE_KEYS } from '../../core/constants';
import { storage } from '../../core/storage';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login implements OnInit {
  emailOrRut: string = '';
  password: string = '';

  portalSeleccionado: string = 'Administrador';

  mensajeError: string = '';

  private readonly portalRoleMap: Record<string, string> = {
    Administrador: 'ROLE_ADMIN',
    Médico: 'ROLE_MEDICO',
    Coordinador: 'ROLE_COORDINADOR',
    Triajer: 'ROLE_TRIAJER',
  };

  constructor(private router: Router, private http: HttpClient, private cdr: ChangeDetectorRef) { }

  ngOnInit() {
    const guardado =     storage.getItem(STORAGE_KEYS.PORTAL);
    if (guardado) this.portalSeleccionado = guardado;
  }

  iniciarSesion() {
    this.mensajeError = '';

    const inputLimpio = this.emailOrRut ? this.emailOrRut.trim() : '';
    const body = {
      email: inputLimpio.includes('@') ? inputLimpio : null,
      rut: !inputLimpio.includes('@') ? inputLimpio : null,
      contrasena: this.password,
    };

    this.http.post(environment.apiUrl + '/auth/login', body).subscribe({
      next: (respuesta: any) => {
        const rol: string = respuesta.rol || '';
        const rolRequerido = this.portalRoleMap[this.portalSeleccionado];
        if (!rolRequerido || rol !== rolRequerido) {
          this.mensajeError = 'Credenciales inválidas';
          this.cdr.detectChanges();
          return;
        }
        storage.setItem(STORAGE_KEYS.TOKEN, respuesta.token);
        if (respuesta.rut) storage.setItem(STORAGE_KEYS.USUARIO_RUT, respuesta.rut);
        if (respuesta.idUsuario) storage.setItem(STORAGE_KEYS.USUARIO_ID, String(respuesta.idUsuario));
        if (respuesta.nombre) storage.setItem(STORAGE_KEYS.USUARIO_NOMBRE, respuesta.nombre);
        if (respuesta.rol) storage.setItem(STORAGE_KEYS.ROLES, respuesta.rol);
        if (respuesta.especialidadId != null) storage.setItem(STORAGE_KEYS.USUARIO_ESPECIALIDAD_ID, String(respuesta.especialidadId));
        if (respuesta.especialidadNombre) storage.setItem(STORAGE_KEYS.USUARIO_ESPECIALIDAD_NOMBRE, respuesta.especialidadNombre);
        const ruta = rol === 'ROLE_ADMIN' ? '/admin'
          : rol === 'ROLE_COORDINADOR' ? '/coordinador'
            : rol === 'ROLE_MEDICO' ? '/medico'
              : rol === 'ROLE_TRIAJER' ? '/triajer'
                : '/dashboard';
        this.router.navigate([ruta]);
      },
      error: () => {
        this.mensajeError = 'Credenciales incorrectas. Revisa tu usuario y contraseña.';
        this.cdr.detectChanges();
      },
    });
  }
}
